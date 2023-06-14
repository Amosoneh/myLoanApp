package com.myloanapp.data.dtos.responses;

import com.myloanapp.data.models.AppUser;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
    private String message;
    private String userId;
}

