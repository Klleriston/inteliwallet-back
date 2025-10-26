package com.inteliwallet.service;

import com.inteliwallet.dto.request.WaitlistRequestDTO;
import com.inteliwallet.dto.response.WaitlistResponseDTO;
import com.inteliwallet.entity.WaitlistUser;
import com.inteliwallet.exception.BadRequestException;
import com.inteliwallet.repository.WaitlistUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WaitlistService {

    private static final Logger logger = LoggerFactory.getLogger(WaitlistService.class);
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int PASSWORD_LENGTH = 16;

    @Autowired
    private WaitlistUserRepository waitlistUserRepository;

    @Autowired
    private MailService mailService;

    @Transactional
    public WaitlistResponseDTO registerUser(WaitlistRequestDTO request) {
        logger.info("Registrando novo usuário na waitlist: {}", request.getEmail());

        if (waitlistUserRepository.existsByEmail(request.getEmail())) {
            logger.warn("Email já cadastrado na waitlist: {}", request.getEmail());
            throw new BadRequestException("Este email já está cadastrado na lista de espera");
        }

        try {
            WaitlistUser waitlistUser = new WaitlistUser();
            waitlistUser.setName(request.getName());
            waitlistUser.setEmail(request.getEmail());
            waitlistUser.setTemporaryPassword(generateTemporaryPassword());
            waitlistUser.setEmailSent(false);
            waitlistUser.setNotified(false);

            WaitlistUser savedUser = waitlistUserRepository.save(waitlistUser);
            logger.info("Usuário salvo na waitlist com ID: {}", savedUser.getId());

            /*boolean emailSent = mailService.sendWaitlistWelcomeEmail(
                    savedUser.getEmail(),
                    savedUser.getName()
            );

            if (emailSent) {
                savedUser.setEmailSent(true);
                waitlistUserRepository.save(savedUser);
                logger.info("Email de boas-vindas enviado com sucesso para: {}", savedUser.getEmail());
            } else {
                logger.warn("Falha ao enviar email de boas-vindas para: {}", savedUser.getEmail());
            }
            */

            return WaitlistResponseDTO.builder()
                    .id(savedUser.getId())
                    .name(savedUser.getName())
                    .email(savedUser.getEmail())
                    .emailSent(false)
                    .createdAt(savedUser.getCreatedAt())
                    .message("Cadastro realizado com sucesso! Verifique seu email.")
                    .build();

        } catch (DataIntegrityViolationException e) {
            logger.error("Erro de integridade ao salvar usuário: {}", request.getEmail(), e);
            throw new BadRequestException("Este email já está cadastrado");
        } catch (Exception e) {
            logger.error("Erro ao registrar usuário na waitlist: {}", request.getEmail(), e);
            throw new BadRequestException("Erro ao processar cadastro. Tente novamente mais tarde.");
        }
    }

    @Transactional
    public void notifyAllUsers() {
        logger.info("Iniciando notificação de todos os usuários da waitlist...");

        List<WaitlistUser> usersToNotify = waitlistUserRepository.findAllNotNotified();
        logger.info("Encontrados {} usuários para notificar", usersToNotify.size());

        int successCount = 0;
        int failCount = 0;

        for (WaitlistUser user : usersToNotify) {
            try {
                boolean emailSent = mailService.sendLaunchNotificationEmail(
                        user.getEmail(),
                        user.getName(),
                        user.getTemporaryPassword()
                );

                if (emailSent) {
                    user.setNotified(true);
                    user.setNotifiedAt(LocalDateTime.now());
                    waitlistUserRepository.save(user);
                    successCount++;
                    logger.info("Notificação enviada para: {}", user.getEmail());
                } else {
                    failCount++;
                    logger.warn("Falha ao enviar notificação para: {}", user.getEmail());
                }

                Thread.sleep(500);

            } catch (Exception e) {
                failCount++;
                logger.error("Erro ao notificar usuário: {}", user.getEmail(), e);
            }
        }

        logger.info("Notificação concluída. Sucessos: {}, Falhas: {}", successCount, failCount);
    }

    public long getTotalWaitlistCount() {
        return waitlistUserRepository.countTotal();
    }

    public List<WaitlistUser> getAllUsers() {
        return waitlistUserRepository.findAll();
    }

    private String generateTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }
}