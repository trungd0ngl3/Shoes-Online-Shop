package com.shoestore.shoestoreWeb.service;


import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.shoestore.shoestoreWeb.dto.request.AuthenticateRequest;
import com.shoestore.shoestoreWeb.dto.request.IntrospectRequest;
import com.shoestore.shoestoreWeb.dto.response.AuthenticateResponse;
import com.shoestore.shoestoreWeb.dto.response.IntrospectResponse;
import com.shoestore.shoestoreWeb.exception.AppException;
import com.shoestore.shoestoreWeb.exception.ErrorCode;
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

import java.awt.image.AreaAveragingScaleFilter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class AuthenticateService {
    UserRepository userRepository;

    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {

        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiredDate = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        return IntrospectResponse.builder()
                .isValid(verified && expiredDate.after(new Date()))
                .build();
    }



    public AuthenticateResponse authenticate(AuthenticateRequest request) {
        PasswordEncoder encoder = new BCryptPasswordEncoder(10);
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));

        boolean isAuth = encoder.matches(request.getPassword(), user.getPassword());

        if(!isAuth){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        var token = generateToken(request.getEmail());

        return AuthenticateResponse.builder()
                .token(token)
                .isAuth(true)
                .build();
    }

    private String generateToken(String email){

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(email)
                .issuer("cc")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .claim("userID",userRepository.findByEmail(email).get().getUid())
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



}
