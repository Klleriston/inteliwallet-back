package com.inteliwallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaitlistResponseDTO {

    private String id;
    private String name;
    private String email;
    private Boolean emailSent;
    private LocalDateTime createdAt;
    private String message;

    public WaitlistResponseDTO(String message) {
        this.message = message;
    }
}