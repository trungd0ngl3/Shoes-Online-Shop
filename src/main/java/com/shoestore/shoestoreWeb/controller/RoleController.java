package com.shoestore.shoestoreWeb.controller;

import com.shoestore.shoestoreWeb.dto.request.ApiResponse;
import com.shoestore.shoestoreWeb.dto.request.RoleRequest;
import com.shoestore.shoestoreWeb.dto.response.PermissionResponse;
import com.shoestore.shoestoreWeb.dto.response.RoleResponse;
import com.shoestore.shoestoreWeb.service.RoleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping()
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAll(){
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }


    @DeleteMapping("/{name}")
    ApiResponse<String> delete(@RequestBody String name){
        roleService.delete(name);
        return ApiResponse.<String>builder()
                .result("Permission deleted.")
                .build();
    }

}
