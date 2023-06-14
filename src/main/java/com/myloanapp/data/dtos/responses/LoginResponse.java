package com.myloanapp.data.dtos.responses;

import com.myloanapp.data.models.AppUser;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private AppUser user;
    private String message;
    private int code;
}
