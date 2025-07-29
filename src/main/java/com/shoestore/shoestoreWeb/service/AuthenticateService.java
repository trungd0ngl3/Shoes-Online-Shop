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


    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected int VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected int REFRESHABLE_DURATION;

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

    public void logout(LogoutRequest request) throws JOSEException, ParseException {
        try {
            var signedToken = verifyToken(request.getToken(), true);

            String jit = signedToken.getJWTClaimsSet().getJWTID();

            Date expiryDate = signedToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryDate(expiryDate)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        }
        catch (AppException e){
            log.info("Token already logout");
        }
    }

    public AuthenticateResponse authenticate(AuthenticateRequest request) {
        PasswordEncoder encoder = new BCryptPasswordEncoder(10);
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));

        boolean isAuth = encoder.matches(request.getPassword(), user.getPassword());

        if(!isAuth){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        var token = generateToken(user);

        return AuthenticateResponse.builder()
                .token(token)
                .isAuth(true)
                .build();
    }

    public AuthenticateResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);

        // logout token cu
        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryDate = signedJWT.getJWTClaimsSet().getExpirationTime();


        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryDate(expiryDate)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        // cap token moi cho user
        var email = signedJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);
        return AuthenticateResponse.builder()
                .token(token)
                .isAuth(true)
                .build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiredDate = (isRefresh) ?
                new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                        .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())

                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiredDate.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    private String generateToken(User user){

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("shoes store")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.HOURS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope",buildScope(user))
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
