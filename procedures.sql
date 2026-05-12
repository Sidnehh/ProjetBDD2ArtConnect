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

-- 1. Inscrire un membre à une exposition (Correct)
-- Aucune modification majeure nécessaire, la logique est saine.
DROP PROCEDURE IF EXISTS sp_Register_Member_To_Exhibition;
DELIMITER //
CREATE PROCEDURE sp_Register_Member_To_Exhibition(
    IN p_IdMember INT,
    IN p_IdExhibition INT,
    OUT p_Message VARCHAR(255)
)
BEGIN
    DECLARE already_registered INT;
    SELECT COUNT(*) INTO already_registered
    FROM RegisterExhibition
    WHERE IdMember = p_IdMember AND IdExhibition = p_IdExhibition;

    IF already_registered > 0 THEN
        SET p_Message = CONCAT(
            'Échec : Le membre ',
            p_IdMember,
            ' est déjà inscrit à l''exposition ',
            p_IdExhibition,
            '.'
        );
    ELSE
        INSERT INTO RegisterExhibition (IdMember, IdExhibition)
        VALUES (p_IdMember, p_IdExhibition);

        SET p_Message = CONCAT(
                'Succès : Inscription confirmée pour le membre ',
                p_IdMember,
                ' à l''exposition ',
                p_IdExhibition,
                '.'
            );
    END IF;
END//
DELIMITER ;

-- 3. Désinscrire un membre à un atelier (Correct)
-- Aucune modification majeure nécessaire, la logique est saine.
DROP PROCEDURE IF EXISTS sp_Unregister_Member_To_Workshop;
DELIMITER //
CREATE PROCEDURE sp_Unregister_Member_To_Workshop(
    IN p_IdMember INT,
    IN p_IdWorkshop INT,
    OUT p_Message VARCHAR(255)
)
BEGIN
    DECLARE already_registered INT;
    SELECT COUNT(*) INTO already_registered
    FROM RegisterWorkshop
    WHERE IdMember = p_IdMember AND IdWorkshop = p_IdWorkshop;
    IF already_registered = 0 THEN
        SET p_Message = CONCAT(
            'Échec : Le membre ',
            p_IdMember,
            ' n''est pas inscrit à l''atelier ',
            p_IdWorkshop,
            '.'
        );
    ELSE
        DELETE FROM RegisterWorkshop 
        WHERE IdMember = p_IdMember AND IdWorkshop = p_IdWorkshop;

        SET p_Message = CONCAT(
                'Succès : Inscription annulée pour le membre ',
                p_IdMember,
                ' à l''atelier ',
                p_IdWorkshop,
                '.'
            );
    END IF;
END//
DELIMITER ;

-- 4. Désinscrire un membre à une exposition (Correct)
-- Aucune modification majeure nécessaire, la logique est saine.
DROP PROCEDURE IF EXISTS sp_Unregister_Member_To_Exhibition;
DELIMITER //
CREATE PROCEDURE sp_Unregister_Member_To_Exhibition(
    IN p_IdMember INT,
    IN p_IdExhibition INT,
    OUT p_Message VARCHAR(255)
)
BEGIN
    DECLARE already_registered INT;
    SELECT COUNT(*) INTO already_registered
    FROM RegisterExhibition
    WHERE IdMember = p_IdMember AND IdExhibition = p_IdExhibition;
    IF already_registered = 0 THEN
        SET p_Message = CONCAT(
            'Échec : Le membre ',
            p_IdMember,
            ' n''est pas inscrit à l''exposition',
            p_IdExhibition,
            '.'
        );
    ELSE
        DELETE FROM RegisterExhibition 
        WHERE IdMember = p_IdMember AND IdExhibition = p_IdExhibition;

        SET p_Message = CONCAT(
                'Succès : Inscription annulée pour le membre ',
                p_IdMember,
                ' à l''exposition ',
                p_IdExhibition,
                '.'
            );
    END IF;
END//
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