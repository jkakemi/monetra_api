-- Create system user for default categories (optional)
INSERT INTO users (cpf, name, email, password, role)
VALUES ('000.000.000-00', 'Sistema', 'sistema@monetra.local', '$2a$10$placeholder', 'ADMIN')
ON CONFLICT (email) DO NOTHING;

-- Get the system user ID and create default expense categories
DO $$
DECLARE
    system_user_id BIGINT;
BEGIN
    SELECT id INTO system_user_id FROM users WHERE email = 'sistema@monetra.local';

    IF system_user_id IS NOT NULL THEN
        INSERT INTO categories (name, type, user_id) VALUES
            ('Alimentacao', 'DESPESA', system_user_id),
            ('Transporte', 'DESPESA', system_user_id),
            ('Moradia', 'DESPESA', system_user_id),
            ('Saude', 'DESPESA', system_user_id),
            ('Educacao', 'DESPESA', system_user_id),
            ('Lazer', 'DESPESA', system_user_id),
            ('Vestuario', 'DESPESA', system_user_id),
            ('Outros', 'DESPESA', system_user_id),
            ('Salario', 'RECEITA', system_user_id),
            ('Freelance', 'RECEITA', system_user_id),
            ('Investimentos', 'RECEITA', system_user_id),
            ('Outros', 'RECEITA', system_user_id)
        ON CONFLICT DO NOTHING;
    END IF;
END $$;
