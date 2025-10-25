package com.inteliwallet.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendInviteResponse {
    private String id;
    private FromUserInfo fromUser;
    private String toUserId;
    private String status; // "pending", "accepted", "declined"

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FromUserInfo {
        private String id;
        private String username;
        private String avatar;
    }
}