package com.nexusmart.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // A handy lombok annotation for a constructor with all fields
public class LoginResponseDTO {
    private String token;
}
