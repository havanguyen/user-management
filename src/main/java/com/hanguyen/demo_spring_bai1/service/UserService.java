package com.hanguyen.demo_spring_bai1.service;


import com.hanguyen.demo_spring_bai1.dto.request.UserCreationRequest;
import com.hanguyen.demo_spring_bai1.dto.request.UserUpdateRequest;
import com.hanguyen.demo_spring_bai1.entity.User;
import com.hanguyen.demo_spring_bai1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository ;

    public User createUser(UserCreationRequest request){
        User user = new User();

        if(userRepository.existsByUsername(request.getUsername())){
            throw  new RuntimeException("User existed");
        }

        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setDod(request.getDod());

        return  userRepository.save(user) ;

    }

    public List<User> getAllUser(){
        return  userRepository.findAll();
    }

    public User getUser (String id){
        return userRepository.findById(id).
                orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUser (String id , UserUpdateRequest request){

        User user = getUser(id);

        user.setPassword(request.getPassword());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setDod(request.getDod());

        return userRepository.save(user);
    }

    public  void deleteUser(String userId){
        userRepository.deleteById(userId);
    }
}
