package com.shoestore.shoestoreWeb.mapper;

import com.shoestore.shoestoreWeb.dto.request.RoleRequest;
import com.shoestore.shoestoreWeb.dto.response.RoleResponse;
import com.shoestore.shoestoreWeb.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);

}
