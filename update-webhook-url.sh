#!/bin/bash

# Obter URL real do serviço
SERVICE_URL=$(gcloud run services describe inteliwallet-api --region us-central1 --format='value(status.url)')

echo "🔍 URL atual do serviço: $SERVICE_URL"
echo ""
echo "📝 Atualizando secrets com a URL correta..."

# Atualizar secret do webhook
echo -n "${SERVICE_URL}/api/subscriptions/webhook" | \
  gcloud secrets versions add MERCADOPAGO_NOTIFICATION_URL --data-file=- --project=inteliwallet-back

echo ""
echo "✅ Secret MERCADOPAGO_NOTIFICATION_URL atualizada!"
echo ""
echo "🔗 Nova URL do webhook: ${SERVICE_URL}/api/subscriptions/webhook"
echo ""
echo "📋 Configure esta URL no Mercado Pago:"
echo "   https://www.mercadopago.com.br/developers/panel/app"
echo ""
echo "🧪 Teste o endpoint:"
echo "   curl ${SERVICE_URL}/api/subscriptions/webhook"
