package com.inteliwallet.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFriendRequest {

    @NotBlank(message = "Nome de usuário é obrigatório")
    private String username;
}