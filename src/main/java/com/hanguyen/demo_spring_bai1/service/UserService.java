package com.hanguyen.demo_spring_bai1.service;


import com.hanguyen.demo_spring_bai1.dto.request.UserCreationRequest;
import com.hanguyen.demo_spring_bai1.dto.request.UserUpdateRequest;
import com.hanguyen.demo_spring_bai1.dto.response.UserResponse;
import com.hanguyen.demo_spring_bai1.entity.User;
import com.hanguyen.demo_spring_bai1.enums.Roles;
import com.hanguyen.demo_spring_bai1.mapper.UserMapper;
import com.hanguyen.demo_spring_bai1.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class UserService {
    UserRepository userRepository ;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public User createUser(UserCreationRequest request){
        if(userRepository.existsByUsername(request.getUsername())){
            throw  new RuntimeException("User existed");
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Roles.USER.name());

        user.setRoles(roles);
        return  userRepository.save(user) ;
    }

    public List<User> getAllUser(){
        return  userRepository.findAll();
    }

    public UserResponse getUser (String id){
        return userMapper.toUserResponse(userRepository.findById(id).
                orElseThrow(() -> new RuntimeException("User not found")));
    }

    public UserResponse updateUser (String id , UserUpdateRequest request){

        User user = userRepository.findById(id).
                orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateUser(user , request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public  void deleteUser(String userId){
        userRepository.deleteById(userId);
    }
}
