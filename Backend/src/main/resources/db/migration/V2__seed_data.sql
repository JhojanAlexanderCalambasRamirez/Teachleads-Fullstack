-- =============================================
-- V2: Seed initial users
-- Passwords encoded with BCrypt:
--   admin123  → $2a$12$...
--   externo123 → $2a$12$...
-- =============================================

INSERT INTO usuario (correo, password_hash, nombre, rol) VALUES
  ('admin@techleads.com',
   '$2a$12$eImiTXuWVxfM37uY4JANjuNqgNbilNsKU2k0KRDhf4FvjHFJtg2Aq',
   'Administrador',
   'ADMIN'),
  ('externo@techleads.com',
   '$2a$12$WXjC4pAoP1YFJSwIGMVJou.FMi1qMETwfI7nVR2Ul2/Y3yC5WiI9y',
   'Usuario Externo',
   'EXTERNO');

INSERT INTO categoria (nombre) VALUES
  ('Electrónica'),
  ('Ropa'),
  ('Alimentos'),
  ('Hogar'),
  ('Tecnología');
