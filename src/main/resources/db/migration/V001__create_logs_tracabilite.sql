-- Migration SQL pour créer la table logs_tracabilite
-- Conforme au cahier des charges UCAC-ICAM
-- Date: 2026-06-11

CREATE TABLE IF NOT EXISTS logs_tracabilite (
    id_log BIGSERIAL PRIMARY KEY,
    id_utilisateur BIGINT,
    id_patient UUID,
    action_effectuee VARCHAR(100) NOT NULL,
    id_dossier_concerne UUID,
    adresse_ip VARCHAR(45),
    horodatage TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index pour améliorer les performances des requêtes
CREATE INDEX IF NOT EXISTS idx_logs_horodatage ON logs_tracabilite(horodatage DESC);
CREATE INDEX IF NOT EXISTS idx_logs_patient ON logs_tracabilite(id_patient);
CREATE INDEX IF NOT EXISTS idx_logs_utilisateur ON logs_tracabilite(id_utilisateur);
CREATE INDEX IF NOT EXISTS idx_logs_action ON logs_tracabilite(action_effectuee);
CREATE INDEX IF NOT EXISTS idx_logs_dossier ON logs_tracabilite(id_dossier_concerne);

-- Commentaires sur la table et les colonnes
COMMENT ON TABLE logs_tracabilite IS 'Table de traçabilité stricte des accès et modifications (Contrainte sécurité CDC)';
COMMENT ON COLUMN logs_tracabilite.id_log IS 'Identifiant unique du log';
COMMENT ON COLUMN logs_tracabilite.id_utilisateur IS 'ID du personnel médical ayant effectué l''action';
COMMENT ON COLUMN logs_tracabilite.id_patient IS 'ID du patient concerné par l''action';
COMMENT ON COLUMN logs_tracabilite.action_effectuee IS 'Description de l''action effectuée (ex: CONSULTATION_ACCES, PATIENT_MODIFICATION)';
COMMENT ON COLUMN logs_tracabilite.id_dossier_concerne IS 'ID du dossier/passage médical concerné';
COMMENT ON COLUMN logs_tracabilite.adresse_ip IS 'Adresse IP de l''utilisateur';
COMMENT ON COLUMN logs_tracabilite.horodatage IS 'Date et heure de l''action';
