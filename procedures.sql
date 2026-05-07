USE ArtConnect;
-- -------------------------------------------------------------------------------------
-- I - PROCEDURES

-- 1. Inscrire un membre à un atelier
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

-- CALL sp_Register_Member_To_Workshop(1, 1, @msg);
-- SELECT @msg;

-- 2. Creation d'Atelier
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
    SET p_Message = CONCAT('Succès : Le Workshop', p_Title, 'a bien été créé.');
END//
DELIMITER ;

-- 3. Suppression d'Atelier 
DROP PROCEDURE IF EXISTS sp_Delete_Workshop;
DELIMITER //
CREATE PROCEDURE sp_Delete_Workshop(
	IN p_IdWorkshop INT,
    OUT p_Message VARCHAR(255)
)
BEGIN
	DECLARE p_Title VARCHAR(50);
    SELECT Title INTO p_Title FROM Workshop WHERE IdWorkshop = p_IdWorkshop;
	DELETE FROM Workshop WHERE Id_Workshop = p_IdWorkshop;
    DELETE FROM RegisterWorkshop WHERE Id_Workshop = p_IdWorkshop;
    SET p_Message = CONCAT('Succès : Le Workshop', p_Title, 'a bien été créé.');
END //
DELIMITER //;
    
-- -------------------------------------------------------------------------------------
-- II - Programmes Stockés

-- 1. Retourner le nombre exact de participants inscrits à une exposition
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

-- SELECT fn_Count_Exhibition_Participants(1) AS Participants_Expo_1;

-- 2. Retourner le nombre exact de participants inscrits à un atelier
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

-- 3. Retourner places restantes d'un atelier;
DROP FUNCTION IF EXISTS fn_Count_Workshop_Remaining_Participants;
DELIMITER //
CREATE FUNCTION fn_Count_Workshop_Remaining_Participants(p_IdWorkshop INT)
    RETURNS INT
    DETERMINISTIC
    READS SQL DATA
BEGIN
    DECLARE nb_participants INT;

    SELECT COUNT(*) INTO nb_participants
    FROM RegisterWorkshop
    WHERE IdWorkshop = p_IdWorkshop;

    RETURN 20 - nb_participants;
END//
DELIMITER ;

