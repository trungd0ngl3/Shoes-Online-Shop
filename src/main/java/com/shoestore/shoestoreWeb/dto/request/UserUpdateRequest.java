package com.shoestore.shoestoreWeb.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;
    String firstname;
    String lastname;
    LocalDate dob;
    
    String address;

    @Pattern(regexp = "^(\\+84|0)[0-9]{9}$", message = "PHONE_INVALID")
    String phone;
}
