package com.shoestore.shoestoreWeb.configuration;

import com.shoestore.shoestoreWeb.enums.Role;
import com.shoestore.shoestoreWeb.entity.User;
import com.shoestore.shoestoreWeb.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
            if(userRepository.findByEmail("admin@gmail.com").isEmpty()){
                var roles = new HashSet<String>();
                roles.add(Role.ADMIN.name());

                User user = User.builder()
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("12345678"))
//                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn("admin has been created with password 12345678, please change it!!");
            }
        };
    }

}
