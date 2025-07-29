package com.shoestore.shoestoreWeb.service;

import com.shoestore.shoestoreWeb.dto.request.PermissionRequest;
import com.shoestore.shoestoreWeb.dto.request.RoleRequest;
import com.shoestore.shoestoreWeb.dto.response.PermissionResponse;
import com.shoestore.shoestoreWeb.dto.response.RoleResponse;
import com.shoestore.shoestoreWeb.entity.Permission;
import com.shoestore.shoestoreWeb.entity.Role;
import com.shoestore.shoestoreWeb.mapper.RoleMapper;
import com.shoestore.shoestoreWeb.repository.PermissionRepository;
import com.shoestore.shoestoreWeb.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request){
        // map request thành role
        var role = roleMapper.toRole(request);

        //tìm permission và thêm permission vào role
        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        // map thành RoleResponse cho controller
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public List<RoleResponse> getAll(){
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    public void delete(String name){
        roleRepository.deleteById(name);
    }



}
