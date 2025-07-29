package com.shoestore.shoestoreWeb.configuration;

import com.nimbusds.jose.JOSEException;
import com.shoestore.shoestoreWeb.dto.request.IntrospectRequest;
import com.shoestore.shoestoreWeb.exception.AppException;
import com.shoestore.shoestoreWeb.exception.ErrorCode;
import com.shoestore.shoestoreWeb.service.AuthenticateService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class CustomJWTDecoder implements JwtDecoder {
    final AuthenticateService authenticateService;
    NimbusJwtDecoder jwtDecoder;

    @Value("${jwt.signer-key}")
    private String SIGNER_KEY;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var response = authenticateService.introspect(IntrospectRequest.builder()
                    .token(token)
                    .build());

            if(!response.isValid()){
                throw new JwtException("Token invalid");
            }
        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }

        if(Objects.isNull(jwtDecoder)){
            SecretKeySpec secretKey = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
            jwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKey)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return jwtDecoder.decode(token);
    }
}
