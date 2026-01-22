package com.hanguyen.registercourses;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
@SpringBootApplication
@EnableJpaAuditing
public class RegisterCoursesApplication {
	public static void main(String[] args) {
		SpringApplication.run(RegisterCoursesApplication.class, args);
	}
}
