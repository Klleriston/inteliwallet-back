-- Insert basic achievements for InteliWallet
-- You can edit these directly in the database after insertion

-- TRANSACTION ACHIEVEMENTS (Transações)
INSERT INTO achievements (id, title, description, icon, points, code, category, target_value, created_at) VALUES
    (gen_random_uuid(), 'Primeira Transação', 'Registre sua primeira transação no InteliWallet', '💸', 10, 'FIRST_TRANSACTION', 'TRANSACTION', 1, CURRENT_TIMESTAMP),
    (gen_random_uuid(),
     'Gastador Iniciante', 'Registre 10 transações', '💰', 50, 'TRANSACTIONS_10', 'TRANSACTION', 10, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Gastador Experiente', 'Registre 50 transações', '💵', 100, 'TRANSACTIONS_50', 'TRANSACTION', 50, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Mestre das Finanças', 'Registre 100 transações', '💎', 200, 'TRANSACTIONS_100', 'TRANSACTION', 100, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Organizador Profissional', 'Registre 500 transações', '👑', 500, 'TRANSACTIONS_500', 'TRANSACTION', 500, CURRENT_TIMESTAMP);

-- SAVING ACHIEVEMENTS (Economia)
INSERT INTO achievements (id, title, description, icon, points, code, category, target_value, created_at) VALUES
    (gen_random_uuid(), 'Poupador Iniciante', 'Economize R$ 100', '🐷', 20, 'SAVED_100', 'SAVING', 100, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Economista', 'Economize R$ 500', '💸', 50, 'SAVED_500', 'SAVING', 500, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Investidor', 'Economize R$ 1.000', '💰', 100, 'SAVED_1000', 'SAVING', 1000, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Milionário em Formação', 'Economize R$ 5.000', '💵', 250, 'SAVED_5000', 'SAVING', 5000, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Mestre da Economia', 'Economize R$ 10.000', '💎', 500, 'SAVED_10000', 'SAVING', 10000, CURRENT_TIMESTAMP);

-- GOAL ACHIEVEMENTS (Metas)
INSERT INTO achievements (id, title, description, icon, points, code, category, target_value, created_at) VALUES
    (gen_random_uuid(), 'Primeira Meta', 'Crie sua primeira meta financeira', '🎯', 10, 'FIRST_GOAL', 'GOAL', 1, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Planejador', 'Crie 5 metas financeiras', '📊', 30, 'GOALS_CREATED_5', 'GOAL', 5, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Conquistador', 'Complete sua primeira meta', '🏆', 50, 'FIRST_GOAL_COMPLETED', 'GOAL', 1, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Realizador', 'Complete 5 metas', '🌟', 100, 'GOALS_COMPLETED_5', 'GOAL', 5, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Mestre das Metas', 'Complete 20 metas', '👑', 300, 'GOALS_COMPLETED_20', 'GOAL', 20, CURRENT_TIMESTAMP);

-- SOCIAL ACHIEVEMENTS (Social)
INSERT INTO achievements (id, title, description, icon, points, code, category, target_value, created_at) VALUES
    (gen_random_uuid(), 'Primeiro Amigo', 'Adicione seu primeiro amigo', '👋', 10, 'FIRST_FRIEND', 'SOCIAL', 1, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Popular', 'Tenha 5 amigos', '👥', 30, 'FRIENDS_5', 'SOCIAL', 5, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Influencer', 'Tenha 20 amigos', '🌟', 100, 'FRIENDS_20', 'SOCIAL', 20, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Primeiro Desafio', 'Participe do seu primeiro desafio', '🚀', 20, 'FIRST_CHALLENGE', 'SOCIAL', 1, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Desafiador', 'Crie 3 desafios', '🎮', 50, 'CHALLENGES_CREATED_3', 'SOCIAL', 3, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Campeão', 'Complete 5 desafios', '🏅', 150, 'CHALLENGES_COMPLETED_5', 'SOCIAL', 5, CURRENT_TIMESTAMP);

-- STREAK ACHIEVEMENTS (Consistência)
INSERT INTO achievements (id, title, description, icon, points, code, category, target_value, created_at) VALUES
    (gen_random_uuid(), 'Consistente', 'Mantenha 7 dias de sequência', '🔥', 30, 'STREAK_7_DAYS', 'STREAK', 7, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Dedicado', 'Mantenha 30 dias de sequência', '⚡', 100, 'STREAK_30_DAYS', 'STREAK', 30, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Imparável', 'Mantenha 90 dias de sequência', '💪', 300, 'STREAK_90_DAYS', 'STREAK', 90, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Lendário', 'Mantenha 180 dias de sequência', '👑', 500, 'STREAK_180_DAYS', 'STREAK', 180, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Imortal', 'Mantenha 365 dias de sequência', '🏆', 1000, 'STREAK_365_DAYS', 'STREAK', 365, CURRENT_TIMESTAMP);
