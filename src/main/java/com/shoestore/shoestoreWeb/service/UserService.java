package com.shoestore.shoestoreWeb.service;

import com.shoestore.shoestoreWeb.dto.request.UserCreationRequest;
import com.shoestore.shoestoreWeb.dto.request.UserUpdateRequest;
import com.shoestore.shoestoreWeb.dto.response.UserResponse;
import com.shoestore.shoestoreWeb.entity.Role;
import com.shoestore.shoestoreWeb.entity.User;
import com.shoestore.shoestoreWeb.exception.AppException;
import com.shoestore.shoestoreWeb.exception.ErrorCode;
import com.shoestore.shoestoreWeb.mapper.UserMapper;
import com.shoestore.shoestoreWeb.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request){
        if(userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(request);

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());

        user.setRoles(roles);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public List<UserResponse> getUsers(){
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }


    public UserResponse getUser(String id){
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(()
                -> new RuntimeException("User not found")));
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }


    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = findUser(userId);
        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }

    private User findUser(String id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
