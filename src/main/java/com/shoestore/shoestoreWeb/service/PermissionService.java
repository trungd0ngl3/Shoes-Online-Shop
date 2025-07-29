package com.shoestore.shoestoreWeb.service;

import com.shoestore.shoestoreWeb.dto.request.PermissionRequest;
import com.shoestore.shoestoreWeb.dto.response.PermissionResponse;
import com.shoestore.shoestoreWeb.entity.Permission;
import com.shoestore.shoestoreWeb.mapper.PermissionMapper;
import com.shoestore.shoestoreWeb.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request){

        Permission permission = permissionMapper.toPermission(request);
        permissionRepository.save(permission);

        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll(){
        return permissionRepository.findAll()
                .stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }

    public void delete(String name){
        permissionRepository.deleteById(name);
    }

}
