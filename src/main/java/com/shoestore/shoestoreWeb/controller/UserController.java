package com.shoestore.shoestoreWeb.controller;

import com.shoestore.shoestoreWeb.dto.request.UserCreationRequest;
import com.shoestore.shoestoreWeb.entity.User;
import com.shoestore.shoestoreWeb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping()
    public User createUser(@RequestBody UserCreationRequest request){
        return userService.createUser(request);

    }

    @GetMapping()
    public List<User> getUsers(){
        return userService.getUsers();
    }
}
