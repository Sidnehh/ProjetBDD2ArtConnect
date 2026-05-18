USE artconnect;
-- -------------------------------------------------------------------------------------------------------
-- I - Déclencheurs d'intégrité de données

-- 1. Empecher la suppression d'un artiste s'il a encore des œuvres "FOR_SALE".
DROP TRIGGER IF EXISTS trg_Prevent_Delete_Artist_With_Works;
DELIMITER //
CREATE TRIGGER trg_Prevent_Delete_Artist_With_Works
BEFORE DELETE ON Artist FOR EACH ROW
BEGIN
    DECLARE nb_oeuvres INT;
    SELECT COUNT(*) INTO nb_oeuvres FROM Artwork WHERE IdArtist = OLD.IdArtist AND Status = 'FOR_SALE';
    IF nb_oeuvres > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Erreur : L''artiste a encore des oeuvres enregistrées.';
    END IF;
END//
DELIMITER ;


-- 2. Vérifier le type d'œuvre
DROP TRIGGER IF EXISTS trg_Validate_Artwork_Type;
DELIMITER //
CREATE TRIGGER trg_Validate_Artwork_Type
BEFORE INSERT ON Artwork
FOR EACH ROW
BEGIN
    IF NEW.Type NOT IN (
        'Painting', 
        'Sculpture', 
        'Photography',
        'Mixed Media',
        'Printmaking',
        'Digital Art',
        'Watercolor',
        'Installation'
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Erreur : Type d''œuvre invalide. Les types autorisés sont : Painting, Sculpture, Photography.';
    END IF;
END//
DELIMITER ;

-- 3. Vérifier la note de Gallerie
DROP TRIGGER IF EXISTS trg_Validate_AddedGallery_Rating;
CREATE TRIGGER trg_Validate_AddedGallery_Rating BEFORE INSERT ON gallery
FOR EACH ROW CALL validate_rating(NEW.rating);
-- Mise à jour
DROP TRIGGER IF EXISTS trg_Validate_UpdatedGallery_Rating;
CREATE TRIGGER trg_Validate_UpdatedGallery_Rating BEFORE UPDATE ON gallery
FOR EACH ROW CALL validate_rating(NEW.rating);

-- 4. Vérifier le prix d'une oeuvre
-- Insertion
DROP TRIGGER IF EXISTS trg_Check_AddedArtwork_Price;
CREATE TRIGGER trg_Check_AddedArtwork_Price BEFORE INSERT ON artwork
FOR EACH ROW CALL validate_price(NEW.price);
-- Mise à jour
DROP TRIGGER IF EXISTS trg_Check_UpdatedArtwork_Price;
CREATE TRIGGER trg_Check_UpdatedArtwork_Price BEFORE UPDATE ON artwork
FOR EACH ROW CALL validate_price(NEW.price);

-- 5. Vérifier le prix d'un workshop
-- Insertion
DROP TRIGGER IF EXISTS trg_Check_AddedWorkshop_Price;
CREATE TRIGGER trg_Check_AddedWorkshop_Price BEFORE INSERT ON workshop
FOR EACH ROW CALL validate_price(NEW.price);
-- Mise à jour
DROP TRIGGER IF EXISTS trg_Check_UpdatedWorkshop_Price;
CREATE TRIGGER trg_Check_UpdatedWorkshop_Price BEFORE UPDATE ON workshop
FOR EACH ROW CALL validate_price(NEW.price);

-- -------------------------------------------------------------------------------------------------------
-- II - Déclencheurs de mise à jour automatique


-- -------------------------------------------------------------------------------------------------------
-- III - Déclencheurs d'enregistrement de modifications

