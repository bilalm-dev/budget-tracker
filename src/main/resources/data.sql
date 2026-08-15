INSERT INTO categories (nom) VALUES
('Logement'),
('Nourriture'),
('Transport'),
('Loisirs'),
('Santé'),
('Abonnements'),
('Autre')
ON CONFLICT (nom) DO NOTHING;