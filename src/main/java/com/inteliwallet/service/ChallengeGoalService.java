package com.inteliwallet.service;

import com.inteliwallet.dto.request.ContributeChallengeRequest;
import com.inteliwallet.dto.request.CreateChallengeGoalRequest;
import com.inteliwallet.dto.request.UpdateChallengeGoalRequest;
import com.inteliwallet.dto.response.ChallengeGoalResponse;
import com.inteliwallet.dto.response.ChallengeParticipantResponse;
import com.inteliwallet.entity.*;
import com.inteliwallet.entity.ChallengeGoal.ChallengeStatus;
import com.inteliwallet.entity.ChallengeParticipant.ParticipantStatus;
import com.inteliwallet.exception.BadRequestException;
import com.inteliwallet.exception.ResourceNotFoundException;
import com.inteliwallet.repository.ChallengeGoalRepository;
import com.inteliwallet.repository.ChallengeParticipantRepository;
import com.inteliwallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeGoalService {

    private final ChallengeGoalRepository challengeGoalRepository;
    private final ChallengeParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final StreakService streakService;

    @Transactional
    public ChallengeGoalResponse createChallenge(String userId, CreateChallengeGoalRequest request) {
        User creator = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Long activeCount = challengeGoalRepository.countActiveCreatedChallengesByUserId(userId);
        if (activeCount >= creator.getPlan().getMaxChallenges()) {
            throw new BadRequestException(
                "Você atingiu o limite de desafios ativos do seu plano. " +
                "Upgrade para criar mais desafios."
            );
        }

        ChallengeGoal challenge = new ChallengeGoal();
        challenge.setCreator(creator);
        challenge.setTitle(request.getTitle());
        challenge.setDescription(request.getDescription());
        challenge.setTargetAmount(request.getTargetAmount());
        challenge.setCategory(request.getCategory());
        challenge.setDeadline(request.getDeadline());
        challenge.setMaxParticipants(request.getMaxParticipants());
        challenge.setRewardPoints(request.getRewardPoints());

        challenge = challengeGoalRepository.save(challenge);

        ChallengeParticipant participant = new ChallengeParticipant();
        participant.setChallengeGoal(challenge);
        participant.setUser(creator);
        participant.setIsCreator(true);
        participantRepository.save(participant);

        return mapToChallengeResponse(challenge);
    }

    @Transactional(readOnly = true)
    public List<ChallengeGoalResponse> listMyChallenges(String userId) {
        List<ChallengeGoal> challenges = challengeGoalRepository.findByParticipantUserId(userId);
        return challenges.stream()
            .map(this::mapToChallengeResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChallengeGoalResponse> listActiveChallenges(String userId) {
        List<ChallengeGoal> challenges = challengeGoalRepository.findByParticipantUserIdAndStatus(
            userId, ChallengeStatus.ACTIVE
        );
        return challenges.stream()
            .map(this::mapToChallengeResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChallengeGoalResponse> listAvailableChallenges() {
        List<ChallengeGoal> challenges = challengeGoalRepository.findAvailableChallenges();
        return challenges.stream()
            .map(this::mapToChallengeResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChallengeGoalResponse getChallengeById(String challengeId) {
        ChallengeGoal challenge = challengeGoalRepository.findById(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException("Desafio não encontrado"));
        return mapToChallengeResponse(challenge);
    }

    @Transactional
    public ChallengeGoalResponse updateChallenge(String userId, String challengeId, UpdateChallengeGoalRequest request) {
        ChallengeGoal challenge = challengeGoalRepository.findById(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException("Desafio não encontrado"));

        if (!challenge.getCreator().getId().equals(userId)) {
            throw new BadRequestException("Apenas o criador pode editar o desafio");
        }

        if (challenge.getStatus() != ChallengeStatus.ACTIVE) {
            throw new BadRequestException("Apenas desafios ativos podem ser editados");
        }

        if (request.getTitle() != null) challenge.setTitle(request.getTitle());
        if (request.getDescription() != null) challenge.setDescription(request.getDescription());
        if (request.getTargetAmount() != null) challenge.setTargetAmount(request.getTargetAmount());
        if (request.getCategory() != null) challenge.setCategory(request.getCategory());
        if (request.getDeadline() != null) challenge.setDeadline(request.getDeadline());
        if (request.getMaxParticipants() != null) challenge.setMaxParticipants(request.getMaxParticipants());
        if (request.getRewardPoints() != null) challenge.setRewardPoints(request.getRewardPoints());

        challenge = challengeGoalRepository.save(challenge);
        return mapToChallengeResponse(challenge);
    }

    @Transactional
    public void deleteChallenge(String userId, String challengeId) {
        ChallengeGoal challenge = challengeGoalRepository.findById(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException("Desafio não encontrado"));

        if (!challenge.getCreator().getId().equals(userId)) {
            throw new BadRequestException("Apenas o criador pode deletar o desafio");
        }

        if (challenge.getStatus() != ChallengeStatus.ACTIVE) {
            throw new BadRequestException("Apenas desafios ativos podem ser deletados");
        }

        challenge.setStatus(ChallengeStatus.CANCELLED);
        challengeGoalRepository.save(challenge);
    }

    @Transactional
    public ChallengeParticipantResponse joinChallenge(String userId, String challengeId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        ChallengeGoal challenge = challengeGoalRepository.findById(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException("Desafio não encontrado"));

        if (challenge.getStatus() != ChallengeStatus.ACTIVE) {
            throw new BadRequestException("Este desafio não está ativo");
        }

        if (participantRepository.existsByChallengeGoalIdAndUserId(challengeId, userId)) {
            throw new BadRequestException("Você já participa deste desafio");
        }

        Long participantCount = participantRepository.countActiveByChallengeGoalId(challengeId);
        if (challenge.getMaxParticipants() != null && participantCount >= challenge.getMaxParticipants()) {
            throw new BadRequestException("Este desafio já atingiu o máximo de participantes");
        }

        ChallengeParticipant participant = new ChallengeParticipant();
        participant.setChallengeGoal(challenge);
        participant.setUser(user);
        participant.setIsCreator(false);

        participant = participantRepository.save(participant);
        return mapToParticipantResponse(participant, challenge.getTargetAmount());
    }

    @Transactional
    public void leaveChallenge(String userId, String challengeId) {
        ChallengeParticipant participant = participantRepository
            .findByChallengeGoalIdAndUserId(challengeId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Você não participa deste desafio"));

        if (participant.getIsCreator()) {
            throw new BadRequestException("O criador não pode sair do desafio. Delete o desafio se necessário.");
        }

        ChallengeGoal challenge = participant.getChallengeGoal();
        if (challenge.getStatus() != ChallengeStatus.ACTIVE) {
            throw new BadRequestException("Você só pode sair de desafios ativos");
        }

        participant.setStatus(ParticipantStatus.LEFT);
        participantRepository.save(participant);
    }

    @Transactional
    public ChallengeGoalResponse contributeToChallenge(String userId, String challengeId, ContributeChallengeRequest request) {
        ChallengeParticipant participant = participantRepository
            .findByChallengeGoalIdAndUserId(challengeId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Você não participa deste desafio"));

        if (participant.getStatus() != ParticipantStatus.ACTIVE) {
            throw new BadRequestException("Você não está ativo neste desafio");
        }

        ChallengeGoal challenge = participant.getChallengeGoal();

        if (challenge.getStatus() != ChallengeStatus.ACTIVE) {
            throw new BadRequestException("Este desafio não está mais ativo");
        }

        challenge.checkOverdue();
        if (challenge.getStatus() == ChallengeStatus.FAILED) {
            challengeGoalRepository.save(challenge);
            throw new BadRequestException("Este desafio expirou");
        }

        participant.contribute(request.getAmount());
        challenge.contribute(request.getAmount());

        participantRepository.save(participant);
        challenge = challengeGoalRepository.save(challenge);

        try {
            streakService.recordChallengeContribution(participant.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (challenge.getStatus() == ChallengeStatus.COMPLETED) {
            distributeRewards(challenge);
        }

        return mapToChallengeResponse(challenge);
    }

    @Transactional(readOnly = true)
    public List<ChallengeParticipantResponse> listParticipants(String challengeId) {
        ChallengeGoal challenge = challengeGoalRepository.findById(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException("Desafio não encontrado"));

        List<ChallengeParticipant> participants = participantRepository
            .findTopContributorsByChallengeGoalId(challengeId);

        return participants.stream()
            .map(p -> mapToParticipantResponse(p, challenge.getTargetAmount()))
            .collect(Collectors.toList());
    }

    private void distributeRewards(ChallengeGoal challenge) {
        List<ChallengeParticipant> participants = participantRepository
            .findByChallengeGoalId(challenge.getId());

        for (ChallengeParticipant participant : participants) {
            if (participant.getStatus() == ParticipantStatus.ACTIVE && !participant.getRewardClaimed()) {
                User user = participant.getUser();
                user.setTotalPoints(user.getTotalPoints() + challenge.getRewardPoints());
                userRepository.save(user);

                participant.setRewardClaimed(true);
                participant.setStatus(ParticipantStatus.COMPLETED);
                participantRepository.save(participant);
            }
        }
    }

    private ChallengeGoalResponse mapToChallengeResponse(ChallengeGoal challenge) {
        ChallengeGoalResponse response = new ChallengeGoalResponse();
        response.setId(challenge.getId());

        ChallengeGoalResponse.CreatorInfo creatorInfo = new ChallengeGoalResponse.CreatorInfo();
        creatorInfo.setId(challenge.getCreator().getId());
        creatorInfo.setUsername(challenge.getCreator().getUsername());
        creatorInfo.setAvatar(challenge.getCreator().getAvatar());
        response.setCreator(creatorInfo);

        response.setTitle(challenge.getTitle());
        response.setDescription(challenge.getDescription());
        response.setTargetAmount(challenge.getTargetAmount());
        response.setCurrentAmount(challenge.getCurrentAmount());
        response.setCategory(challenge.getCategory());
        response.setDeadline(challenge.getDeadline());
        response.setStatus(challenge.getStatus().getValue());
        response.setMaxParticipants(challenge.getMaxParticipants());
        response.setCurrentParticipants(challenge.getParticipantCount());
        response.setRewardPoints(challenge.getRewardPoints());
        response.setProgressPercentage(challenge.getProgressPercentage());
        response.setCreatedAt(challenge.getCreatedAt());
        response.setUpdatedAt(challenge.getUpdatedAt());

        List<ChallengeParticipant> topContributors = participantRepository
            .findTopContributorsByChallengeGoalId(challenge.getId())
            .stream()
            .limit(3)
            .collect(Collectors.toList());

        List<ChallengeGoalResponse.ParticipantInfo> topContributorsInfo = topContributors.stream()
            .map(p -> {
                ChallengeGoalResponse.ParticipantInfo info = new ChallengeGoalResponse.ParticipantInfo();
                info.setId(p.getId());
                info.setUserId(p.getUser().getId());
                info.setUsername(p.getUser().getUsername());
                info.setAvatar(p.getUser().getAvatar());
                info.setContributedAmount(p.getContributedAmount());
                info.setContributionPercentage(p.getContributionPercentage(challenge.getTargetAmount()));
                info.setIsCreator(p.getIsCreator());
                return info;
            })
            .collect(Collectors.toList());

        response.setTopContributors(topContributorsInfo);

        return response;
    }

    private ChallengeParticipantResponse mapToParticipantResponse(
        ChallengeParticipant participant,
        java.math.BigDecimal targetAmount
    ) {
        ChallengeParticipantResponse response = new ChallengeParticipantResponse();
        response.setId(participant.getId());
        response.setChallengeGoalId(participant.getChallengeGoal().getId());

        ChallengeParticipantResponse.UserInfo userInfo = new ChallengeParticipantResponse.UserInfo();
        userInfo.setId(participant.getUser().getId());
        userInfo.setUsername(participant.getUser().getUsername());
        userInfo.setAvatar(participant.getUser().getAvatar());
        userInfo.setTotalPoints(participant.getUser().getTotalPoints());
        response.setUser(userInfo);

        response.setContributedAmount(participant.getContributedAmount());
        response.setStatus(participant.getStatus().getValue());
        response.setIsCreator(participant.getIsCreator());
        response.setRewardClaimed(participant.getRewardClaimed());
        response.setContributionPercentage(participant.getContributionPercentage(targetAmount));
        response.setJoinedAt(participant.getJoinedAt());

        return response;
    }
}
