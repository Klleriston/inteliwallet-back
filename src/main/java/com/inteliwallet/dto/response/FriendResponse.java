package com.inteliwallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendResponse {
    private String id;
    private String username;
    private String avatar;
    private Integer totalPoints;
    private Integer rank; // Calculado dinamicamente
    private String status; // "active" (sempre active para amigos confirmados)
}