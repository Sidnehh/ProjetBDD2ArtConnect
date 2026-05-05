USE ArtConnect;

-- Inscrire un membre à un atelier

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



-- Retourner le nombre exact de participants inscrits à une exposition

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