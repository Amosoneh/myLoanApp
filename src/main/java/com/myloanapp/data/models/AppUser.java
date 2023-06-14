package com.myloanapp.data.models;

import lombok.*;
import org.springframework.data.annotation.Id;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor @Builder
public class AppUser {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private Role role;

}
