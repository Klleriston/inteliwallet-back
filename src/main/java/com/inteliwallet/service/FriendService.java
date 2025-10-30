package com.inteliwallet.service;

import com.inteliwallet.dto.request.AddFriendRequest;
import com.inteliwallet.dto.response.FriendInviteResponse;
import com.inteliwallet.dto.response.FriendResponse;
import com.inteliwallet.entity.FriendInvite;
import com.inteliwallet.entity.FriendInvite.InviteStatus;
import com.inteliwallet.entity.Friendship;
import com.inteliwallet.entity.User;
import com.inteliwallet.exception.BadRequestException;
import com.inteliwallet.exception.ResourceNotFoundException;
import com.inteliwallet.repository.FriendInviteRepository;
import com.inteliwallet.repository.FriendshipRepository;
import com.inteliwallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendInviteRepository friendInviteRepository;
    private final AchievementService achievementService;

    public List<FriendResponse> listFriends(String userId) {
        List<User> friends = friendshipRepository.findFriendsByUserId(userId);

        List<FriendResponse> responses = friends.stream()
            .map(this::mapToFriendResponse)
            .sorted(Comparator.comparingInt(FriendResponse::getTotalPoints).reversed())
            .collect(Collectors.toList());

        for (int i = 0; i < responses.size(); i++) {
            responses.get(i).setRank(i + 1);
        }

        return responses;
    }

    @Transactional
    public FriendInviteResponse addFriend(String userId, AddFriendRequest request) {
        User currentUser = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        User friend = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (userId.equals(friend.getId())) {
            throw new BadRequestException("Você não pode adicionar a si mesmo como amigo");
        }

        if (friendshipRepository.areFriends(userId, friend.getId())) {
            throw new BadRequestException("Vocês já são amigos");
        }

        if (friendInviteRepository.existsPendingInvite(userId, friend.getId())) {
            throw new BadRequestException("Já existe um convite pendente entre vocês");
        }

        FriendInvite invite = new FriendInvite();
        invite.setFromUser(currentUser);
        invite.setToUser(friend);
        invite.setStatus(InviteStatus.PENDING);

        invite = friendInviteRepository.save(invite);

        return mapToInviteResponse(invite);
    }

    @Transactional
    public void removeFriend(String userId, String friendId) {
        List<Friendship> friendships = friendshipRepository.findFriendship(userId, friendId);

        if (friendships.isEmpty()) {
            throw new ResourceNotFoundException("Amizade não encontrada");
        }

        friendshipRepository.deleteAll(friendships);
    }

    @Transactional(readOnly = true)
    public List<FriendInviteResponse> listInvites(String userId) {
        List<FriendInvite> invites = friendInviteRepository
            .findByToUserIdAndStatus(userId, InviteStatus.PENDING);

        return invites.stream()
            .map(this::mapToInviteResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public FriendResponse acceptInvite(String userId, String inviteId) {
        FriendInvite invite = friendInviteRepository.findById(inviteId)
            .orElseThrow(() -> new ResourceNotFoundException("Convite não encontrado"));

        if (!invite.getToUser().getId().equals(userId)) {
            throw new BadRequestException("Você não tem permissão para aceitar este convite");
        }

        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new BadRequestException("Convite não está pendente");
        }

        invite.setStatus(InviteStatus.ACCEPTED);
        friendInviteRepository.save(invite);

        Friendship friendship1 = new Friendship();
        friendship1.setUser(invite.getFromUser());
        friendship1.setFriend(invite.getToUser());

        Friendship friendship2 = new Friendship();
        friendship2.setUser(invite.getToUser());
        friendship2.setFriend(invite.getFromUser());

        friendshipRepository.save(friendship1);
        friendshipRepository.save(friendship2);

        try {
            long friendCountToUser = friendshipRepository.findFriendsByUserId(userId).size();
            achievementService.updateProgress(userId, "FIRST_FRIEND", (int) friendCountToUser);
            achievementService.updateProgress(userId, "FRIENDS_5", (int) friendCountToUser);
            achievementService.updateProgress(userId, "FRIENDS_20", (int) friendCountToUser);

            long friendCountFromUser = friendshipRepository.findFriendsByUserId(invite.getFromUser().getId()).size();
            achievementService.updateProgress(invite.getFromUser().getId(), "FIRST_FRIEND", (int) friendCountFromUser);
            achievementService.updateProgress(invite.getFromUser().getId(), "FRIENDS_5", (int) friendCountFromUser);
            achievementService.updateProgress(invite.getFromUser().getId(), "FRIENDS_20", (int) friendCountFromUser);
        } catch (Exception e) {
        }

        return mapToFriendResponse(invite.getFromUser());
    }

    @Transactional
    public void declineInvite(String userId, String inviteId) {
        FriendInvite invite = friendInviteRepository.findById(inviteId)
            .orElseThrow(() -> new ResourceNotFoundException("Convite não encontrado"));

        if (!invite.getToUser().getId().equals(userId)) {
            throw new BadRequestException("Você não tem permissão para recusar este convite");
        }

        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new BadRequestException("Convite não está pendente");
        }

        invite.setStatus(InviteStatus.DECLINED);
        friendInviteRepository.save(invite);
    }

    private FriendResponse mapToFriendResponse(User user) {
        FriendResponse response = new FriendResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setAvatar(user.getAvatar());
        response.setTotalPoints(user.getTotalPoints());
        response.setRank(0);
        response.setStatus("active");
        return response;
    }

    private FriendInviteResponse mapToInviteResponse(FriendInvite invite) {
        FriendInviteResponse response = new FriendInviteResponse();
        response.setId(invite.getId());

        FriendInviteResponse.FromUserInfo fromUserInfo = new FriendInviteResponse.FromUserInfo();
        fromUserInfo.setId(invite.getFromUser().getId());
        fromUserInfo.setUsername(invite.getFromUser().getUsername());
        fromUserInfo.setAvatar(invite.getFromUser().getAvatar());

        response.setFromUser(fromUserInfo);
        response.setToUserId(invite.getToUser().getId());
        response.setStatus(invite.getStatus().getValue());
        response.setCreatedAt(invite.getCreatedAt());

        return response;
    }
}