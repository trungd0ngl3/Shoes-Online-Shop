package com.shoestore.shoestoreWeb.controller;

import com.shoestore.shoestoreWeb.dto.request.ApiResponse;
import com.shoestore.shoestoreWeb.dto.request.UserCreationRequest;
import com.shoestore.shoestoreWeb.dto.request.UserUpdateRequest;
import com.shoestore.shoestoreWeb.entity.User;
import com.shoestore.shoestoreWeb.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping()
    public ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request){
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }

    @GetMapping()
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public ApiResponse<User> getUser(@PathVariable String userId){
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getUser(userId));
        return apiResponse;
    }

    @PutMapping("/{userId}")
    public ApiResponse<User> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.updateUser(userId, request));
        return apiResponse;
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<User> deleteUser(@PathVariable String userId){
        ApiResponse<User> apiResponse = new ApiResponse<>();

        userService.deleteUser(userId);
        apiResponse.setMessage("delete success");
        return apiResponse;
    }

}
