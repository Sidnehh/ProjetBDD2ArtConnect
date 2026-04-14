CREATE VIEW view_artist_portfolio AS
SELECT ar.IdArtist, ar.Name AS ArtistName, aw.IdArtwork, 
       aw.Title, aw.Type, aw.Price, aw.Status
FROM Artist ar
JOIN Artwork aw ON ar.IdArtist = aw.IdArtist;

CREATE VIEW view_artist_dashboard AS
SELECT ar.IdArtist, ar.Name AS ArtistName,
       w.IdWorkshop, w.Title AS WorkshopTitle, 
       w.Date_, w.Level,
       e.Title AS ExhibitionTitle, g.Name AS GalleryName, 
       g.StreetName, g.City
FROM Artist ar
LEFT JOIN Workshop w ON ar.IdArtist = w.IdArtist
LEFT JOIN Artwork aw ON ar.IdArtist = aw.IdArtist
LEFT JOIN Exhibition e ON e.IdGallery IS NOT NULL
LEFT JOIN Gallery g ON e.IdGallery = g.IdGallery;

CREATE VIEW view_artist_workshop_members AS
SELECT ar.IdArtist, ar.Name AS ArtistName,
       w.Title AS WorkshopTitle, w.Date_,
       cm.Name AS MemberName
FROM Artist ar
JOIN Workshop w ON ar.IdArtist = w.IdArtist
JOIN Registerworkshop rw ON w.IdWorkshop = rw.IdWorkshop
JOIN CommunityMember cm ON rw.IdMember = cm.IdMember;


CREATE VIEW view_upcoming_events AS
SELECT 'Workshop' AS EventType, w.Title, w.Date_ AS EventDate,
       ar.Name AS ArtistName, w.Price, w.Level,
       NULL AS GalleryName
FROM Workshop w
JOIN Artist ar ON w.IdArtist = ar.IdArtist
WHERE w.Date_ > NOW()
UNION
SELECT 'Exhibition', e.Title, e.StartDate,
       NULL, NULL, NULL,
       g.Name
FROM Exhibition e
JOIN Gallery g ON e.IdGallery = g.IdGallery
WHERE e.StartDate > CURDATE();

CREATE VIEW view_member_registrations AS
SELECT cm.IdMember, cm.Name AS MemberName,
       'Workshop' AS EventType, w.Title, w.Date_ AS EventDate
FROM CommunityMember cm
JOIN Registerworkshop rw ON cm.IdMember = rw.IdMember
JOIN Workshop w ON rw.IdWorkshop = w.IdWorkshop
UNION
SELECT cm.IdMember, cm.Name,
       'Exhibition', e.Title, e.StartDate
FROM CommunityMember cm
JOIN RegisterExhibition re ON cm.IdMember = re.IdMember
JOIN Exhibition e ON re.IdExhibition = e.IdExhibition;
CREATE VIEW view_artworks_for_sale AS
SELECT aw.IdArtwork, aw.Title, aw.Type, aw.Price,
       ar.Name AS ArtistName, ar.City AS ArtistCity
FROM Artwork aw
JOIN Artist ar ON aw.IdArtist = ar.IdArtist
WHERE aw.Status = 'FOR_SALE';


CREATE VIEW view_admin_members AS
SELECT cm.IdMember, cm.Name, cm.Email, cm.City,
       COUNT(rw.IdWorkshop) AS NbWorkshops,
       COUNT(re.IdExhibition) AS NbExhibitions
FROM CommunityMember cm
LEFT JOIN Registerworkshop rw ON cm.IdMember = rw.IdMember
LEFT JOIN RegisterExhibition re ON cm.IdMember = re.IdMember
GROUP BY cm.IdMember, cm.Name, cm.Email, cm.City;


CREATE VIEW view_activity_summary AS
SELECT ar.Name AS ArtistName,
       COUNT(DISTINCT aw.IdArtwork) AS NbArtworks,
       COUNT(DISTINCT w.IdWorkshop) AS NbWorkshops,
       COUNT(DISTINCT rw.IdMember) AS TotalWorkshopParticipants
FROM Artist ar
LEFT JOIN Artwork aw ON ar.IdArtist = aw.IdArtist
LEFT JOIN Workshop w ON ar.IdArtist = w.IdArtist
LEFT JOIN Registerworkshop rw ON w.IdWorkshop = rw.IdWorkshop
GROUP BY ar.IdArtist, ar.Name;


-- Artwork : filtres sur IdArtist (jointure) et Status (WHERE FOR_SALE)
CREATE INDEX idx_artwork_artist ON Artwork(IdArtist);
CREATE INDEX idx_artwork_status ON Artwork(Status);

-- Workshop : filtre sur IdArtist (jointure) et Date_ (WHERE > NOW())
CREATE INDEX idx_workshop_artist ON Workshop(IdArtist);
CREATE INDEX idx_workshop_date ON Workshop(Date_);

-- Exhibition : filtre sur IdGallery (jointure) et StartDate (WHERE > CURDATE())
CREATE INDEX idx_exhibition_gallery ON Exhibition(IdGallery);
CREATE INDEX idx_exhibition_date ON Exhibition(StartDate);

-- Tables de jointure : les deux colonnes de chaque table d'association
CREATE INDEX idx_registerworkshop_member ON Registerworkshop(IdMember);
CREATE INDEX idx_registerworkshop_workshop ON Registerworkshop(IdWorkshop);
CREATE INDEX idx_registerexhibition_member ON RegisterExhibition(IdMember);
CREATE INDEX idx_registerexhibition_exhibition ON RegisterExhibition(IdExhibition);


CREATE USER 'artiste'@'localhost' IDENTIFIED BY 'artiste_pass';
CREATE USER 'membre'@'localhost' IDENTIFIED BY 'membre_pass';
CREATE USER 'admin_ac'@'localhost' IDENTIFIED BY 'admin_pass';

GRANT SELECT ON ArtConnect.view_artist_portfolio TO 'artiste'@'localhost';
GRANT SELECT ON ArtConnect.view_artist_dashboard TO 'artiste'@'localhost';
GRANT SELECT ON ArtConnect.view_artist_workshop_members TO 'artiste'@'localhost';

GRANT SELECT ON ArtConnect.view_upcoming_events TO 'membre'@'localhost';
GRANT SELECT ON ArtConnect.view_member_registrations TO 'membre'@'localhost';
GRANT SELECT ON ArtConnect.view_artworks_for_sale TO 'membre'@'localhost';

GRANT SELECT ON ArtConnect.view_admin_members TO 'admin_ac'@'localhost';
GRANT SELECT ON ArtConnect.view_activity_summary TO 'admin_ac'@'localhost';
GRANT SELECT ON ArtConnect.view_upcoming_events TO 'admin_ac'@'localhost';
GRANT SELECT ON ArtConnect.view_member_registrations TO 'admin_ac'@'localhost';
GRANT SELECT ON ArtConnect.view_artworks_for_sale TO 'admin_ac'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON ArtConnect.* TO 'admin_ac'@'localhost';

FLUSH PRIVILEGES;