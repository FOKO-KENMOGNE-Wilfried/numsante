-- Création de la table notifications
CREATE TABLE IF NOT EXISTS notifications (
    id_notification BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_patient BINARY(16) NOT NULL,
    type VARCHAR(100) NOT NULL,
    titre VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    est_lu BOOLEAN DEFAULT FALSE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_lecture TIMESTAMP NULL,
    id_passage BINARY(16) NULL,
    donnees TEXT NULL,

    -- Clés étrangères
    FOREIGN KEY (id_patient) REFERENCES patients(id_patient) ON DELETE CASCADE,

    -- Index pour améliorer les performances
    INDEX idx_patient_date (id_patient, date_creation DESC),
    INDEX idx_patient_non_lu (id_patient, est_lu),
    INDEX idx_type (type),
    INDEX idx_passage (id_passage)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Commentaires
COMMENT ON TABLE notifications IS 'Notifications envoyées aux patients pour les informer des événements';
COMMENT ON COLUMN notifications.type IS 'Type de notification: NOUVEAU_PASSAGE, CONSTANTES_PRISES, CONSULTATION_TERMINEE, PRESCRIPTION_CREEE, PRESCRIPTION_DELIVREE, RESULTATS_LABO, CARTE_SUSPENDUE, CARTE_RENOUVELEE';
COMMENT ON COLUMN notifications.donnees IS 'Données supplémentaires au format JSON (optionnel)';
