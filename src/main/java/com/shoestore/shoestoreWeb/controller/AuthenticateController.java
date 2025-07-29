package com.shoestore.shoestoreWeb.controller;


import com.nimbusds.jose.JOSEException;
import com.shoestore.shoestoreWeb.dto.request.*;
import com.shoestore.shoestoreWeb.dto.response.AuthenticateResponse;
import com.shoestore.shoestoreWeb.dto.response.IntrospectResponse;
import com.shoestore.shoestoreWeb.service.AuthenticateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticateController {
    AuthenticateService authenticateService;

    @PostMapping("/token")
    ApiResponse<AuthenticateResponse> authenticate(@RequestBody AuthenticateRequest request){
        var result = authenticateService.authenticate(request);
        return ApiResponse.<AuthenticateResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticateService.logout(request);
        return ApiResponse.<Void>builder().build();

    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticateResponse> refreshToken(@RequestBody RefreshTokenRequest request) throws ParseException, JOSEException {
        var result = authenticateService.refreshToken(request);
        return ApiResponse.<AuthenticateResponse>builder()
                .result(result)
                .build();
    }



}
