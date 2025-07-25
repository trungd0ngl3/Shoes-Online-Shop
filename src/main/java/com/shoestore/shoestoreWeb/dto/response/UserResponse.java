package com.shoestore.shoestoreWeb.dto.response;

import com.shoestore.shoestoreWeb.entity.Role;
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
    String uid;
    String email;
    String firstname;
    String lastname;
    LocalDate dob;
    Set<Role> roles;
}
