package com.proto.BIS.common.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String userName;
    private Boolean isAdmin;
}
