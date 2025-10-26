package com.inteliwallet.controller;

import com.inteliwallet.dto.request.WaitlistRequestDTO;
import com.inteliwallet.dto.response.WaitlistResponseDTO;
import com.inteliwallet.entity.WaitlistUser;
import com.inteliwallet.service.WaitlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/waitlist")
@Tag(name = "Waitlist", description = "Endpoints para gerenciamento da lista de espera")
@CrossOrigin(origins = "*")
public class WaitlistController {

    private static final Logger logger = LoggerFactory.getLogger(WaitlistController.class);

    @Autowired
    private WaitlistService waitlistService;

    @PostMapping
    @Operation(
            summary = "Cadastrar na lista de espera",
            description = "Registra um novo usuário na lista de espera e envia email de confirmação"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cadastro realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já cadastrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> register(@Valid @RequestBody WaitlistRequestDTO request) {
        logger.info("Nova requisição de cadastro na waitlist: {}", request.getEmail());

        try {
            WaitlistResponseDTO response = waitlistService.registerUser(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erro ao processar cadastro na waitlist: {}", request.getEmail(), e);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());

            if (e.getMessage().contains("já está cadastrado") || e.getMessage().contains("duplicate")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/notify-all")
    @Operation(
            summary = "Notificar todos os usuários",
            description = "Envia email de notificação de lançamento para todos os usuários da waitlist que ainda não foram notificados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificação iniciada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar notificações")
    })
    public ResponseEntity<?> notifyAllUsers() {
        logger.info("Iniciando notificação de todos os usuários da waitlist");

        try {
            waitlistService.notifyAllUsers();

            Map<String, String> response = new HashMap<>();
            response.put("message", "Notificações enviadas com sucesso!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erro ao notificar usuários da waitlist", e);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao enviar notificações");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/count")
    @Operation(
            summary = "Contar usuários na waitlist",
            description = "Retorna o total de usuários cadastrados na lista de espera"
    )
    @ApiResponse(responseCode = "200", description = "Contagem realizada com sucesso")
    public ResponseEntity<?> getCount() {
        long count = waitlistService.getTotalWaitlistCount();

        Map<String, Long> response = new HashMap<>();
        response.put("total", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Listar todos os usuários",
            description = "Retorna a lista completa de usuários cadastrados na waitlist"
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<WaitlistUser>> getAllUsers() {
        List<WaitlistUser> users = waitlistService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}