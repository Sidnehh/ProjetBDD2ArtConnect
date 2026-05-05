-- Empecher la suppression d'un artiste s'il a encore des œuvres "FOR_SALE".
DROP TRIGGER trg_Prevent_Delete_Artist_With_Works;
DELIMITER //
CREATE TRIGGER trg_Prevent_Delete_Artist_With_Works
BEFORE DELETE ON Artist FOR EACH ROW
BEGIN
    DECLARE nb_oeuvres INT;
    SELECT COUNT(*) INTO nb_oeuvres FROM Artwork WHERE IdArtist = OLD.IdArtist AND Status = 'FOR_SALE';
    IF nb_oeuvres > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Erreur : Impossible de supprimer un artiste ayant des œuvres à vendre.';
    END IF;
END//
DELIMITER ;


-- Vérifier le type d'œuvre
DROP TRIGGER trg_Validate_Artwork_Type;
DELIMITER //
CREATE TRIGGER trg_Validate_Artwork_Type
BEFORE INSERT ON Artwork
FOR EACH ROW
BEGIN
    IF NEW.Type NOT IN (
        'Painting', 
        'Sculpture', 
        'Photography'
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Erreur : Type d''œuvre invalide. Les types autorisés sont : Painting, Sculpture, Photography.';
    END IF;
END//
DELIMITER ;