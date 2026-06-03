package com.techleads.application.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginResponse {
    String token;
    String correo;
    String nombre;
    String rol;
}
