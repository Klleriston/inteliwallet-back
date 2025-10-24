package com.inteliwallet.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

/**
 * Anotação para injetar userId autenticado nos controllers
 *
 * Uso: public ResponseEntity<?> endpoint(@CurrentUser String userId)
 */
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {
}
