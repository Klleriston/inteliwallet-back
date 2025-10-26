package com.inteliwallet.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final Resend resend;

    @Value("${resend.api.key}")
    private String apiKey;

    public MailService(@Value("${resend.api.key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public boolean sendWaitlistWelcomeEmail(String toEmail, String userName) {
        try {
            String htmlContent = buildWaitlistWelcomeHtml(userName);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("Acme <onboarding@resend.dev>")
                    .to(toEmail)
                    .subject("🎉 Bem-vindo à lista de espera da InteliWallet!")
                    .html(htmlContent)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            logger.info("Email enviado com sucesso para: {} - ID: {}", toEmail, response.getId());
            return true;

        } catch (ResendException e) {
            logger.error("Erro ao enviar email para: {}", toEmail, e);
            return false;
        }
    }

    public boolean sendLaunchNotificationEmail(String toEmail, String userName, String temporaryPassword) {
        try {
            String htmlContent = buildLaunchNotificationHtml(userName, temporaryPassword);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("Acme <onboarding@resend.dev>")
                    .to(toEmail)
                    .subject("🚀 A InteliWallet está no ar! Acesse agora")
                    .html(htmlContent)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            logger.info("Email de lançamento enviado para: {} - ID: {}", toEmail, response.getId());
            return true;

        } catch (ResendException e) {
            logger.error("Erro ao enviar email de lançamento para: {}", toEmail, e);
            return false;
        }
    }

    private String buildWaitlistWelcomeHtml(String userName) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Bem-vindo à InteliWallet</title>
                </head>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 40px 20px; text-align: center; border-radius: 10px 10px 0 0;">
                        <h1 style="color: white; margin: 0; font-size: 28px;">🎉 Bem-vindo à InteliWallet!</h1>
                    </div>

                    <div style="background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px;">
                        <p style="font-size: 16px; margin-bottom: 20px;">Olá <strong>%s</strong>,</p>

                        <p style="font-size: 16px; margin-bottom: 20px;">
                            Obrigado por se inscrever na lista de espera da <strong>InteliWallet</strong>!
                            Estamos muito felizes em ter você conosco.
                        </p>

                        <div style="background: white; padding: 20px; border-left: 4px solid #667eea; margin: 20px 0;">
                            <p style="margin: 0; font-size: 15px;">
                                🎮 <strong>O que é a InteliWallet?</strong><br/>
                                Uma carteira financeira gamificada que torna o controle das suas finanças divertido e recompensador!
                            </p>
                        </div>

                        <p style="font-size: 16px; margin-bottom: 20px;">
                            ✨ <strong>Próximos passos:</strong>
                        </p>

                        <ul style="font-size: 15px; line-height: 2;">
                            <li>Você receberá um email assim que a aplicação estiver disponível</li>
                            <li>Será um dos primeiros a ter acesso exclusivo</li>
                            <li>Poderá começar a gamificar suas finanças imediatamente</li>
                        </ul>

                        <div style="background: #667eea; color: white; padding: 15px; border-radius: 8px; margin: 30px 0; text-align: center;">
                            <p style="margin: 0; font-size: 14px;">
                                💡 Fique de olho na sua caixa de entrada!
                            </p>
                        </div>

                        <p style="font-size: 14px; color: #666; margin-top: 30px; text-align: center;">
                            Atenciosamente,<br/>
                            <strong>Equipe InteliWallet</strong>
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(userName);
    }

    private String buildLaunchNotificationHtml(String userName, String temporaryPassword) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>InteliWallet - Acesso Liberado!</title>
                </head>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 40px 20px; text-align: center; border-radius: 10px 10px 0 0;">
                        <h1 style="color: white; margin: 0; font-size: 28px;">🚀 A InteliWallet está no ar!</h1>
                    </div>

                    <div style="background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px;">
                        <p style="font-size: 16px; margin-bottom: 20px;">Olá <strong>%s</strong>,</p>

                        <p style="font-size: 16px; margin-bottom: 20px;">
                            🎉 A espera acabou! A <strong>InteliWallet</strong> está oficialmente no ar e você já pode começar a usar.
                        </p>

                        <div style="background: white; padding: 25px; border-radius: 8px; margin: 25px 0; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                            <h3 style="margin-top: 0; color: #667eea;">🔑 Suas credenciais de acesso:</h3>
                            <p style="margin: 10px 0;"><strong>Email:</strong> %s</p>
                            <p style="margin: 10px 0;"><strong>Senha temporária:</strong> <code style="background: #f0f0f0; padding: 5px 10px; border-radius: 4px; font-size: 14px;">%s</code></p>

                            <div style="background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin-top: 20px;">
                                <p style="margin: 0; font-size: 14px; color: #856404;">
                                    ⚠️ <strong>Importante:</strong> Altere sua senha no primeiro acesso para garantir a segurança da sua conta.
                                </p>
                            </div>
                        </div>

                        <div style="text-align: center; margin: 30px 0;">
                            <a href="${app.url}" style="background: #667eea; color: white; padding: 15px 40px; text-decoration: none; border-radius: 25px; font-weight: bold; display: inline-block; font-size: 16px;">
                                Acessar InteliWallet
                            </a>
                        </div>

                        <p style="font-size: 16px; margin-bottom: 20px;">
                            ✨ <strong>Comece agora:</strong>
                        </p>

                        <ul style="font-size: 15px; line-height: 2;">
                            <li>Registre suas primeiras transações</li>
                            <li>Defina suas metas financeiras</li>
                            <li>Ganhe pontos e conquiste achievements</li>
                            <li>Conecte-se com amigos e compare seu progresso</li>
                        </ul>

                        <div style="background: #667eea; color: white; padding: 15px; border-radius: 8px; margin: 30px 0; text-align: center;">
                            <p style="margin: 0; font-size: 14px;">
                                🎮 Transforme suas finanças em uma jornada gamificada!
                            </p>
                        </div>

                        <p style="font-size: 14px; color: #666; margin-top: 30px; text-align: center;">
                            Atenciosamente,<br/>
                            <strong>Equipe InteliWallet</strong>
                        </p>

                        <p style="font-size: 12px; color: #999; margin-top: 30px; text-align: center; border-top: 1px solid #ddd; padding-top: 20px;">
                            Se você não solicitou este cadastro, por favor ignore este email.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(userName, temporaryPassword, temporaryPassword);
    }
}
