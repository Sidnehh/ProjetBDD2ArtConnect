-- POUR LES ARTISTES

-- Consulter la liste des œuvres et leur statut pour tous les artistes.
-- Dans l'application, filtrer par IdArtist
CREATE OR REPLACE VIEW view_artist_portfolio AS
SELECT ar.IdArtist, ar.Name AS ArtistName, aw.IdArtwork, 
       aw.Title, aw.Type, aw.Price, aw.Status
FROM Artist ar
JOIN Artwork aw ON ar.IdArtist = aw.IdArtist;

-- Donne à l'artiste la liste détaillée des participants à ses ateliers.
CREATE OR REPLACE VIEW view_artist_workshop_members AS
SELECT ar.IdArtist, ar.Name AS ArtistName,
       w.Title AS WorkshopTitle, w.Date_,
       cm.Name AS MemberName
FROM Artist ar
JOIN Workshop w ON ar.IdArtist = w.IdArtist
JOIN Registerworkshop rw ON w.IdWorkshop = rw.IdWorkshop
JOIN CommunityMember cm ON rw.IdMember = cm.IdMember;



-- POUR LES UTILISATEURS

-- Récupère les workshops et exhibitions à venir.
CREATE OR REPLACE VIEW view_upcoming_events AS
SELECT 
    'Exhibition' AS EventType,
    e.Title AS EventName,
    e.StartDate AS EventDate,
    g.Name AS Location,
    e.Theme AS Description,
    NULL AS Price
FROM Exhibition e
JOIN Gallery g ON e.IdGallery = g.IdGallery
WHERE e.StartDate >= CURDATE()
UNION ALL
SELECT 
    'Workshop' AS EventType,
    w.Title AS EventName,
    w.Date_ AS EventDate,
    a.Name AS Location, -- Nom de l'artiste comme lieu
    CONCAT('Level: ', w.Level) AS Description,
    w.Price AS Price
FROM Workshop w
JOIN Artist a ON w.IdArtist = a.IdArtist
WHERE w.Date_ >= NOW()
ORDER BY EventDate ASC;

-- Permet à un membre de voir toutes ses inscriptions.
-- Dans l'application, filtrer par IdMember.
CREATE OR REPLACE VIEW view_member_registrations AS
SELECT 
    m.IdMember,
    m.Name AS MemberName,
    'Exhibition' AS RegistrationType,
    e.Title AS EventTitle,
    e.StartDate AS EventDate,
    g.Name AS Location
FROM CommunityMember m
JOIN RegisterExhibition re ON m.IdMember = re.IdMember
JOIN Exhibition e ON re.IdExhibition = e.IdExhibition
JOIN Gallery g ON e.IdGallery = g.IdGallery
UNION ALL
SELECT 
    m.IdMember,
    m.Name AS MemberName,
    'Workshop' AS RegistrationType,
    w.Title AS EventTitle,
    w.Date_ AS EventDate,
    a.Name AS Instructor
FROM CommunityMember m
JOIN RegisterWorkshop rw ON m.IdMember = rw.IdMember
JOIN Workshop w ON rw.IdWorkshop = w.IdWorkshop
JOIN Artist a ON w.IdArtist = a.IdArtist
ORDER BY EventDate DESC;

-- Montre toutes les œuvres disponibles à la vente
CREATE OR REPLACE VIEW view_artworks_for_sale AS
SELECT aw.IdArtwork, aw.Title, aw.Type, aw.Price,
       ar.Name AS ArtistName, ar.City AS ArtistCity
FROM Artwork aw
JOIN Artist ar ON aw.IdArtist = ar.IdArtist
WHERE aw.Status = 'FOR_SALE';



-- POUR LES ORGANISATEURS

-- Montre les emails et le nombre total d'inscriptions de chaque membre
CREATE OR REPLACE VIEW view_admin_members AS
SELECT cm.IdMember, cm.Name, cm.Email, cm.City,
       COUNT(rw.IdWorkshop) AS NbWorkshops,
       COUNT(re.IdExhibition) AS NbExhibitions
FROM CommunityMember cm
LEFT JOIN Registerworkshop rw ON cm.IdMember = rw.IdMember
LEFT JOIN RegisterExhibition re ON cm.IdMember = re.IdMember
GROUP BY cm.IdMember, cm.Name, cm.Email, cm.City;

-- Tableau de bord global par artiste (Œuvres, Workshops, Participants totaux).
CREATE OR REPLACE VIEW view_activity_summary AS
SELECT ar.Name AS ArtistName,
       COUNT(DISTINCT aw.IdArtwork) AS NbArtworks,
       COUNT(DISTINCT w.IdWorkshop) AS NbWorkshops,
       COUNT(DISTINCT rw.IdMember) AS TotalWorkshopParticipants
FROM Artist ar
LEFT JOIN Artwork aw ON ar.IdArtist = aw.IdArtist
LEFT JOIN Workshop w ON ar.IdArtist = w.IdArtist
LEFT JOIN Registerworkshop rw ON w.IdWorkshop = rw.IdWorkshop
GROUP BY ar.IdArtist, ar.Name;