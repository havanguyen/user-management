package com.hanguyen.demo_spring_bai1.config;


import com.hanguyen.demo_spring_bai1.entity.User;
import com.hanguyen.demo_spring_bai1.enums.Roles;
import com.hanguyen.demo_spring_bai1.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return  args -> {
            if(userRepository.findByUsername("superadmin").isEmpty()){
                // --- SỬA LỖI Ở ĐÂY ---
                Set<Roles> roles = new HashSet<>(); // Đổi từ Set<String> sang Set<Roles>
                roles.add(Roles.ADMIN); // Thêm trực tiếp Enum
                // ---------------------

                User user = User.builder()
                        .username("superadmin")
                        .password(passwordEncoder.encode("SuperAdmin123!"))
                        .roles(roles)
                        .build();

                userRepository.save(user);

                log.warn("admin account has been created with default password , please change it .");
            }
        };
    }
}