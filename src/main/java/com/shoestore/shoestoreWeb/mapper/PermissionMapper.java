package com.shoestore.shoestoreWeb.mapper;

import com.shoestore.shoestoreWeb.dto.request.PermissionRequest;
import com.shoestore.shoestoreWeb.dto.response.PermissionResponse;
import com.shoestore.shoestoreWeb.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);

}
