# Configuração do Webhook Mercado Pago - InteliWallet

## ⚠️ Problema Identificado

A URL do serviço Cloud Run mudou automaticamente:

- ❌ **URL antiga (errada)**: `https://inteliwallet-back-238215485349.us-central1.run.app`
- ✅ **URL correta**: `https://inteliwallet-api-xbsw2vwyga-uc.a.run.app`

## 🔧 Solução

### 1. Deploy do Código Atualizado

```bash
cd /Users/klleristonandrade/dev/inteliwallet-back

# Commit das mudanças
git add .
git commit -m "feat: adiciona endpoint GET para webhook e corrige URL"
git push origin main
```

A pipeline CI/CD fará o deploy automaticamente.

### 2. Configurar Webhook no Mercado Pago

Após o deploy, configure no painel do Mercado Pago:

**URL do Webhook:**
```
https://inteliwallet-api-xbsw2vwyga-uc.a.run.app/api/subscriptions/webhook
```

**Passos:**
1. Acesse: https://www.mercadopago.com.br/developers/panel/app
2. Selecione sua aplicação
3. Clique em **Webhooks**
4. Adicione a URL: `https://inteliwallet-api-xbsw2vwyga-uc.a.run.app/api/subscriptions/webhook`
5. Selecione eventos: **Pagamentos**
6. Salve

### 3. Testar o Webhook

Após o deploy, teste com:

```bash
# Teste GET (verifica se endpoint está acessível)
curl https://inteliwallet-api-xbsw2vwyga-uc.a.run.app/api/subscriptions/webhook

# Deve retornar:
# {"status":"ok","message":"Webhook endpoint está acessível","timestamp":"2025-10-31T..."}
```

```bash
# Teste POST (simula webhook do Mercado Pago)
curl -X POST "https://inteliwallet-api-xbsw2vwyga-uc.a.run.app/api/subscriptions/webhook?type=payment&id=123" \
  -H "Content-Type: application/json" \
  -d '{
    "action": "payment.updated",
    "data": {"id": "123456"},
    "external_reference": "uuid-da-assinatura",
    "status": "approved"
  }'

# Deve retornar:
# {"status":"ok"}
```

## 🔍 URLs Corretas para Produção

Atualize todas as referências para usar as URLs corretas:

### Webhook
```
https://inteliwallet-api-xbsw2vwyga-uc.a.run.app/api/subscriptions/webhook
```

### Redirecionamentos
- **Sucesso**: `https://inteliwallet.vercel.app/payment/success`
- **Falha**: `https://inteliwallet.vercel.app/payment/failure`

## 📝 Atualizar Secrets no Google Cloud

Se necessário atualizar a secret (requer permissões adequadas):

```bash
# Atualizar URL do webhook
echo -n "https://inteliwallet-api-xbsw2vwyga-uc.a.run.app/api/subscriptions/webhook" | \
  gcloud secrets versions add MERCADOPAGO_NOTIFICATION_URL \
  --data-file=- \
  --project=inteliwallet-back

# Fazer novo deploy
gcloud run deploy inteliwallet-api \
  --region us-central1 \
  --update-secrets="MERCADOPAGO_NOTIFICATION_URL=MERCADOPAGO_NOTIFICATION_URL:latest" \
  --project=inteliwallet-back
```

## ✅ Checklist de Verificação

- [ ] Código atualizado com endpoint GET no webhook
- [ ] Commit e push para GitHub
- [ ] Deploy concluído com sucesso
- [ ] Endpoint GET retorna status "ok"
- [ ] URL configurada no painel do Mercado Pago
- [ ] Teste de webhook manual funcionando
- [ ] Pagamento real testado end-to-end

## 🐛 Troubleshooting

### Erro 404 - Not Found
- Verifique se a URL está correta
- Confirme que o deploy foi concluído
- Teste com: `curl https://inteliwallet-api-xbsw2vwyga-uc.a.run.app/api/subscriptions/webhook`

### Erro 500 - Internal Server Error
- Verifique logs: `gcloud run services logs read inteliwallet-api --region us-central1 --limit 50`
- Confirme que todas as secrets estão configuradas
- Verifique se o banco de dados está acessível

### Webhook não recebe notificações
- Confirme que a URL está configurada no Mercado Pago
- Verifique se o endpoint está público (allow-unauthenticated)
- Monitore logs durante um pagamento de teste

## 📊 Monitoramento

```bash
# Ver logs em tempo real
gcloud run services logs tail inteliwallet-api --region us-central1

# Filtrar logs de webhook
gcloud run services logs read inteliwallet-api --region us-central1 | grep "Webhook"
```

---

**Última atualização**: 31/10/2025
**URL do Serviço**: `https://inteliwallet-api-xbsw2vwyga-uc.a.run.app`