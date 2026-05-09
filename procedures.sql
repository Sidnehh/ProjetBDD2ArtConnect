USE ArtConnect;

-- -------------------------------------------------------------------------------------
-- I - PROCEDURES

-- 1. Inscrire un membre à un atelier (Correct)
-- Aucune modification majeure nécessaire, la logique est saine.
DROP PROCEDURE IF EXISTS sp_Register_Member_To_Workshop;
DELIMITER //
CREATE PROCEDURE sp_Register_Member_To_Workshop(
    IN p_IdMember INT,
    IN p_IdWorkshop INT,
    OUT p_Message VARCHAR(255)
)
BEGIN
    DECLARE already_registered INT;
    SELECT COUNT(*) INTO already_registered
    FROM RegisterWorkshop
    WHERE IdMember = p_IdMember AND IdWorkshop = p_IdWorkshop;

    IF already_registered > 0 THEN
        SET p_Message = CONCAT(
            'Échec : Le membre ',
            p_IdMember,
            ' est déjà inscrit à l''atelier ',
            p_IdWorkshop,
            '.'
        );
    ELSE
        INSERT INTO RegisterWorkshop (IdMember, IdWorkshop)
        VALUES (p_IdMember, p_IdWorkshop);

        SET p_Message = CONCAT(
                'Succès : Inscription confirmée pour le membre ',
                p_IdMember,
                ' à l''atelier ',
                p_IdWorkshop,
                '.'
            );
    END IF;
END//
DELIMITER ;

-- 2. Création d'Atelier (Correct)
-- Aucune modification majeure nécessaire.
DROP PROCEDURE IF EXISTS sp_Create_Workshop;
DELIMITER //
CREATE PROCEDURE sp_Create_Workshop(
    IN p_IdWorkshop INT,
    IN p_Title VARCHAR(50),
    IN p_IdArtist INT,
    IN p_Date DATETIME,
    IN p_Price DECIMAL(15,2),
    IN p_Level VARCHAR(50),
    OUT p_Message VARCHAR(255)
)
BEGIN
    INSERT INTO Workshop (IdWorkshop, Title, Date_, Price, Level, IdArtist) 
    VALUES (p_IdWorkshop, p_Title, p_Date, p_Price, p_Level, p_IdArtist);
    
    SET p_Message = CONCAT('Succès : Le Workshop "', p_Title, '" a bien été créé.');
END//
DELIMITER ;

-- 3. Suppression d'Atelier (Corrigé : Incohérence Id_Workshop vs IdWorkshop)
DROP PROCEDURE IF EXISTS sp_Delete_Workshop;
DELIMITER //
CREATE PROCEDURE sp_Delete_Workshop(
    IN p_IdWorkshop INT,
    OUT p_Message VARCHAR(255)
)
BEGIN
    DECLARE p_Title VARCHAR(50);
    
    -- Récupération du titre pour le message
    SELECT Title INTO p_Title 
    FROM Workshop 
    WHERE IdWorkshop = p_IdWorkshop; -- Correction : IdWorkshop (pas Id_Workshop)
    
    -- Suppression des inscriptions d'abord (si contrainte de clé étrangère)
    DELETE FROM RegisterWorkshop WHERE IdWorkshop = p_IdWorkshop; -- Correction : IdWorkshop
    
    -- Suppression de l'atelier
    DELETE FROM Workshop WHERE IdWorkshop = p_IdWorkshop; -- Correction : IdWorkshop
    
    IF p_Title IS NOT NULL THEN
        SET p_Message = CONCAT('Succès : Le Workshop "', p_Title, '" a bien été supprimé.');
    ELSE
        SET p_Message = 'Erreur : L''atelier spécifié n''existe pas.';
    END IF;
END //
DELIMITER ;

-- -------------------------------------------------------------------------------------
-- II - FONCTIONS (Programmes Stockés)

-- 1. Retourner le nombre exact de participants inscrits à une exposition (Correct)
DROP FUNCTION IF EXISTS fn_Count_Exhibition_Participants;
DELIMITER //
CREATE FUNCTION fn_Count_Exhibition_Participants(p_IdExhibition INT)
    RETURNS INT
    DETERMINISTIC
    READS SQL DATA
BEGIN
    DECLARE nb_participants INT;

    SELECT COUNT(*) INTO nb_participants
    FROM RegisterExhibition
    WHERE IdExhibition = p_IdExhibition;

    RETURN nb_participants;
END//
DELIMITER ;

-- 2. Retourner le nombre exact de participants inscrits à un atelier (Correct)
DROP FUNCTION IF EXISTS fn_Count_Workshop_Participants;
DELIMITER //
CREATE FUNCTION fn_Count_Workshop_Participants(p_IdWorkshop INT)
    RETURNS INT
    DETERMINISTIC
    READS SQL DATA
BEGIN
    DECLARE nb_participants INT;

    SELECT COUNT(*) INTO nb_participants
    FROM RegisterWorkshop
    WHERE IdWorkshop = p_IdWorkshop;

    RETURN nb_participants;
END//
DELIMITER ;

-- 3. Retourner le CA généré par la vente d'oeuvres d'un artiste (Corrigé logiquement)

DROP FUNCTION IF EXISTS fn_Count_Artist_Revenue;
DELIMITER //
CREATE FUNCTION fn_Count_Artist_Revenue(p_IdArtist INT)
    RETURNS DOUBLE
    DETERMINISTIC
    READS SQL DATA
BEGIN
    DECLARE artwork_revenue DOUBLE;
    DECLARE workshop_revenue DOUBLE;
    
    -- Calcul du CA : Somme des prix des ateliers de cet artiste qui ont des inscriptions + prix des oeuvres vendues
    SELECT SUM(Price) INTO artwork_revenue
    FROM Artwork 
    WHERE IdArtist = IdArtist AND Status = "SOLD";
    
    SELECT SUM(Price) INTO workshop_revenue
    FROM Workshop w
    INNER JOIN RegisterWorkshop rw ON w.IdWorkshop = rw.IdWorkshop
    WHERE w.IdArtist = p_IdArtist;
    
    RETURN artwork_revenue+workshop_revenue;
END//
DELIMITER ;