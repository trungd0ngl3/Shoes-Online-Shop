package com.shoestore.shoestoreWeb.controller;


import com.nimbusds.jose.JOSEException;
import com.shoestore.shoestoreWeb.dto.request.ApiResponse;
import com.shoestore.shoestoreWeb.dto.request.AuthenticateRequest;
import com.shoestore.shoestoreWeb.dto.request.IntrospectRequest;
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

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticateService.introspect(request);

        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();

    }




}
