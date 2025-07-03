package com.shoestore.shoestoreWeb.mapper;

import com.shoestore.shoestoreWeb.dto.request.UserCreationRequest;
import com.shoestore.shoestoreWeb.dto.request.UserUpdateRequest;
import com.shoestore.shoestoreWeb.dto.response.UserResponse;
import com.shoestore.shoestoreWeb.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
