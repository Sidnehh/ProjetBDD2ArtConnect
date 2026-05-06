USE ArtConnect;

DELIMITER //

--  procédure qui contient le bloc transactionnel
DROP PROCEDURE IF EXISTS Annulation_Atelier;
CREATE PROCEDURE Annulation_Atelier(IN p_IdWorkshop INT)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            GET DIAGNOSTICS CONDITION 1 @err_msg = MESSAGE_TEXT;
            SELECT CONCAT('ÉCHEC : ', @err_msg) AS Resultat;
        END;

    START TRANSACTION;

    -- vérification de l'existence
    IF NOT EXISTS (SELECT 1 FROM Workshop WHERE IdWorkshop = p_IdWorkshop) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Erreur : L''atelier spécifié n''existe pas.';
    END IF;

    -- supprimer les inscriptions
    DELETE FROM RegisterWorkshop WHERE IdWorkshop = p_IdWorkshop;
    -- supprimer l'atelier
    DELETE FROM Workshop WHERE IdWorkshop = p_IdWorkshop;

    COMMIT;

    SELECT CONCAT('SUCCÈS : Atelier ', p_IdWorkshop, ' annulé.') AS Resultat;
END//

DELIMITER ;

CALL Annulation_Atelier(27);