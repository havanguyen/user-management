package com.hanguyen.demo_spring_bai1.controller;


import com.hanguyen.demo_spring_bai1.dto.request.UserCreationRequest;
import com.hanguyen.demo_spring_bai1.dto.request.UserUpdateRequest;
import com.hanguyen.demo_spring_bai1.entity.User;
import com.hanguyen.demo_spring_bai1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService ;

    @PostMapping
    User createUser(@RequestBody UserCreationRequest userCreationRequest){
        return userService.createUser(userCreationRequest);
    }

    @GetMapping
    List<User> getAllUser (){
        return  userService.getAllUser();
    }

    @GetMapping("/{userId}")
    User getUser(@PathVariable("userId") String userId){
            return  userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    User updateUser(@RequestBody UserUpdateRequest userUpdateRequest , @PathVariable("userId") String userId){
        return  userService.updateUser(userId , userUpdateRequest);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return "User has been deleted";
    }



}
