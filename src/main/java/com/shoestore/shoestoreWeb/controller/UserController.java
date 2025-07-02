package com.shoestore.shoestoreWeb.controller;

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
    public User createUser(@RequestBody @Valid UserCreationRequest request){
        return userService.createUser(request);

    }

    @GetMapping()
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable String userId){
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    public User updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return "User has been deleted";
    }

}
