package com.inteliwallet.service;

import com.inteliwallet.dto.request.ContributeGoalRequest;
import com.inteliwallet.dto.request.CreateGoalRequest;
import com.inteliwallet.dto.request.UpdateGoalRequest;
import com.inteliwallet.dto.response.GoalResponse;
import com.inteliwallet.entity.Goal;
import com.inteliwallet.entity.User;
import com.inteliwallet.exception.BadRequestException;
import com.inteliwallet.exception.ResourceNotFoundException;
import com.inteliwallet.repository.GoalRepository;
import com.inteliwallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public List<GoalResponse> listGoals(String userId) {
        return goalRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public GoalResponse getGoal(String userId, String goalId) {
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        if (!goal.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Meta não encontrada");
        }

        goal.checkOverdue();
        goalRepository.save(goal);

        return mapToResponse(goal);
    }

    @Transactional
    public GoalResponse createGoal(String userId, CreateGoalRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        long activeGoalsCount = goalRepository.countByUserIdAndStatus(userId, Goal.GoalStatus.ACTIVE);
        if (activeGoalsCount >= user.getPlan().getMaxGoals()) {
            throw new BadRequestException(
                "Você atingiu o limite de metas ativas do seu plano (" +
                user.getPlan().getMaxGoals() + " metas). " +
                "Upgrade seu plano para criar mais metas."
            );
        }

        Goal goal = new Goal();
        goal.setUser(user);
        goal.setTitle(request.getTitle());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setCurrentAmount(request.getCurrentAmount() != null ? request.getCurrentAmount() : BigDecimal.ZERO);
        goal.setCategory(request.getCategory());
        goal.setDeadline(request.getDeadline());

        goal = goalRepository.save(goal);

        return mapToResponse(goal);
    }

    @Transactional
    public GoalResponse updateGoal(String userId, String goalId, UpdateGoalRequest request) {
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        if (!goal.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Meta não encontrada");
        }

        if (request.getTitle() != null) {
            goal.setTitle(request.getTitle());
        }
        if (request.getTargetAmount() != null) {
            goal.setTargetAmount(request.getTargetAmount());
        }
        if (request.getCategory() != null) {
            goal.setCategory(request.getCategory());
        }
        if (request.getDeadline() != null) {
            goal.setDeadline(request.getDeadline());
        }

        goal = goalRepository.save(goal);

        return mapToResponse(goal);
    }

    @Transactional
    public GoalResponse contributeToGoal(String userId, String goalId, ContributeGoalRequest request) {
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        if (!goal.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Meta não encontrada");
        }

        goal.contribute(request.getAmount());
        goal = goalRepository.save(goal);

        return mapToResponse(goal);
    }

    @Transactional
    public void deleteGoal(String userId, String goalId) {
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        if (!goal.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Meta não encontrada");
        }

        goalRepository.delete(goal);
    }

    private GoalResponse mapToResponse(Goal goal) {
        GoalResponse response = modelMapper.map(goal, GoalResponse.class);
        response.setStatus(goal.getStatus().getValue());
        response.setProgressPercentage(goal.getProgressPercentage());
        return response;
    }
}