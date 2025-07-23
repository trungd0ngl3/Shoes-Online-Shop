package com.shoestore.shoestoreWeb.dto.response;

import com.shoestore.shoestoreWeb.entity.Role;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String email;
    String firstname;
    String lastname;
    LocalDate dob;
    Set<String> roles;
}
