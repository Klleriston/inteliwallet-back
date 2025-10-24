package com.inteliwallet.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(min = 3, max = 50, message = "Nome de usuário deve ter entre 3 e 50 caracteres")
    private String username;

    @Email(message = "Email inválido")
    private String email;

    @Size(max = 10, message = "Avatar deve ter no máximo 10 caracteres")
    private String avatar;

    private Boolean hasCompletedOnboarding;
}