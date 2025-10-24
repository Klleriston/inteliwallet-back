package com.inteliwallet.controller;

import com.inteliwallet.dto.request.AddFriendRequest;
import com.inteliwallet.dto.response.FriendInviteResponse;
import com.inteliwallet.dto.response.FriendResponse;
import com.inteliwallet.security.CurrentUser;
import com.inteliwallet.service.FriendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    public ResponseEntity<List<FriendResponse>> listFriends(@CurrentUser String userId) {
        return ResponseEntity.ok(friendService.listFriends(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<FriendInviteResponse> addFriend(
        @CurrentUser String userId,
        @Valid @RequestBody AddFriendRequest request
    ) {
        return ResponseEntity.ok(friendService.addFriend(userId, request));
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> removeFriend(
        @CurrentUser String userId,
        @PathVariable String friendId
    ) {
        friendService.removeFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/invites")
    public ResponseEntity<List<FriendInviteResponse>> listInvites(@CurrentUser String userId) {
        return ResponseEntity.ok(friendService.listInvites(userId));
    }

    @PostMapping("/invites/{inviteId}/accept")
    public ResponseEntity<FriendResponse> acceptInvite(
        @CurrentUser String userId,
        @PathVariable String inviteId
    ) {
        return ResponseEntity.ok(friendService.acceptInvite(userId, inviteId));
    }

    @PostMapping("/invites/{inviteId}/decline")
    public ResponseEntity<Void> declineInvite(
        @CurrentUser String userId,
        @PathVariable String inviteId
    ) {
        friendService.declineInvite(userId, inviteId);
        return ResponseEntity.noContent().build();
    }
}