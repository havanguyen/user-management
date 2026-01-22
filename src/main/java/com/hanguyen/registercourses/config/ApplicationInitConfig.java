package com.hanguyen.registercourses.config;
import com.hanguyen.registercourses.entity.User;
import com.hanguyen.registercourses.constant.Roles;
import com.hanguyen.registercourses.repository.UserRepository;
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
                Set<Roles> roles = new HashSet<>();
                roles.add(Roles.ADMIN);
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