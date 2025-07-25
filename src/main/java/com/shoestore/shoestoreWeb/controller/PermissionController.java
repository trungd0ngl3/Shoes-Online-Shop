package com.shoestore.shoestoreWeb.controller;

import com.shoestore.shoestoreWeb.dto.request.ApiResponse;
import com.shoestore.shoestoreWeb.dto.request.PermissionRequest;
import com.shoestore.shoestoreWeb.dto.response.PermissionResponse;
import com.shoestore.shoestoreWeb.service.PermissionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping()
    ApiResponse<PermissionResponse> createPermission(@RequestBody @Valid PermissionRequest request){
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getPermissions(){
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{name}")
    ApiResponse<String> deletePermission(@RequestBody @Valid String name){
        permissionService.delete(name);
        return ApiResponse.<String>builder()
                .result("Permission deleted.")
                .build();
    }

}
