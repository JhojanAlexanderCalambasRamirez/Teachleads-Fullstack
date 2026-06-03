package com.techleads.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendPdfRequest {

    @NotBlank
    @Email
    private String destinatario;

    private String empresaNit;
}
