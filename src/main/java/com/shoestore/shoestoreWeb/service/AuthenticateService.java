package com.shoestore.shoestoreWeb.service;


import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.shoestore.shoestoreWeb.dto.request.AuthenticateRequest;
import com.shoestore.shoestoreWeb.dto.request.IntrospectRequest;
import com.shoestore.shoestoreWeb.dto.request.LogoutRequest;
import com.shoestore.shoestoreWeb.dto.request.RefreshTokenRequest;
import com.shoestore.shoestoreWeb.dto.response.AuthenticateResponse;
import com.shoestore.shoestoreWeb.dto.response.IntrospectResponse;
import com.shoestore.shoestoreWeb.entity.InvalidatedToken;
import com.shoestore.shoestoreWeb.entity.User;
import com.shoestore.shoestoreWeb.exception.AppException;
import com.shoestore.shoestoreWeb.exception.ErrorCode;
import com.shoestore.shoestoreWeb.repository.InvalidatedTokenRepository;
import com.shoestore.shoestoreWeb.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.awt.image.AreaAveragingScaleFilter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class AuthenticateService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    PasswordEncoder encoder;

    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected int VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected int REFRESHABLE_DURATION;


    //kiểm tra token
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        boolean isValid = true;

        try {
            verifyToken(request.getToken(), false);
        }catch (AppException e){
            isValid = false;
        }

        return IntrospectResponse.builder()
                .isValid(isValid)
                .build();
    }

    // Logout token
    public void logout(LogoutRequest request) throws JOSEException, ParseException {
        try {
            // lấy token đã được verify
            var signedToken = verifyToken(request.getToken(), true);

            String jit = signedToken.getJWTClaimsSet().getJWTID();
            Date expiryDate = signedToken.getJWTClaimsSet().getExpirationTime();

            // lấy thông tin và build InvalidatedToken để lưu vào DB
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryDate(expiryDate)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        }
        // nếu có lỗi thì log token đã được logout
        catch (AppException e){
            log.info("Token already logout");
        }
    }

    // Xác thực người dùng bằng token
    public AuthenticateResponse authenticate(AuthenticateRequest request) {
        // tìm người dùng từ email
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));// trả về AppException nếu không tìm thấy email

        // kiểm trả mật khẩu
        boolean isAuth = encoder.matches(request.getPassword(), user.getPassword());

        if(!isAuth){
            // bắt lỗi nếu như kiểm trả mật khẩu không đúng
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // generateToken cho Người dùng đã được xác thực
        var token = generateToken(user);

        return AuthenticateResponse.builder()
                .token(token)
                .isAuth(true)
                .build();
    }

    // Refresh lại token cũ
    public AuthenticateResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();// id của token
        var expiryDate = signedJWT.getJWTClaimsSet().getExpirationTime();// ngày hết hạn token

        // lấy thông tin token và logout token cũ
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryDate(expiryDate)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        // lấy email từ token cũ
        var email = signedJWT.getJWTClaimsSet().getSubject();

        // tìm người dùng từ email
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        // generate token mới và build response
        var token = generateToken(user);

        return AuthenticateResponse.builder()
                .token(token)
                .isAuth(true)
                .build();
    }

    // Xác thực token
    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        // khởi tạo ngày hết hạn
        Date expiredDate = (isRefresh) ?

                // thêm thời hạn refresh nếu isRefresh = true
                new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                        .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())

                // ngược lại thì lấy ngày hết hạn của token
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        // kiểm tra xác thực và ngày hết hạn
        if (!(verified && expiredDate.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        // kiểm tra token có tồn tại hay không
        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    // Generate token
    private String generateToken(User user){

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())// email yêu cầu
                .issuer("shoes store")
                .issueTime(new Date())// thời gian tạo token
                .expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.HOURS).toEpochMilli()))// ngày hết hạn token
                .jwtID(UUID.randomUUID().toString())// id token
                .claim("scope",buildScope(user))// các role và quyền hạn
                .build();

        JWSObject jwsObject = new JWSObject(header, jwtClaimsSet.toPayload());

        try{

            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes(StandardCharsets.UTF_8)));
            return jwsObject.serialize();
        } catch (JOSEException e){

            log.info("Cannot generate token");
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");

        if(!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());

                if(!CollectionUtils.isEmpty(role.getPermissions())){
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add(permission.getName());
                    });
                }
            });
        }
        return stringJoiner.toString();
    }

}
