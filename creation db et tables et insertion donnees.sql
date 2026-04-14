CREATE DATABASE IF NOT EXISTS ArtConnect;
USE ArtConnect;

CREATE TABLE IF NOT EXISTS Artist(
   IdArtist INT,
   Email VARCHAR(50) NOT NULL,
   Name VARCHAR(50) NOT NULL,
   City VARCHAR(50) NOT NULL,
   BirthYear INT NOT NULL,
   PRIMARY KEY(IdArtist)
);
 
CREATE TABLE IF NOT EXISTS Artwork(
   IdArtwork INT,
   Title VARCHAR(50) NOT NULL,
   Price DECIMAL(15,2) NOT NULL,
   Status VARCHAR(50) NOT NULL,
   Type VARCHAR(50) NOT NULL,
   IdArtist INT NOT NULL,
   PRIMARY KEY(IdArtwork),
   FOREIGN KEY(IdArtist) REFERENCES Artist(IdArtist)
);
 
CREATE TABLE IF NOT EXISTS Gallery(
   IdGallery INT,
   Name VARCHAR(50) NOT NULL,
   Rating DECIMAL(3,2),
   StreetName VARCHAR(50) NOT NULL,
   City VARCHAR(50) NOT NULL,
   PRIMARY KEY(IdGallery)
);
 
CREATE TABLE IF NOT EXISTS Exhibition(
   IdExhibition INT,
   Title VARCHAR(50) NOT NULL,
   StartDate DATE NOT NULL,
   Theme VARCHAR(50) NOT NULL,
   IdGallery INT NOT NULL,
   PRIMARY KEY(IdExhibition),
   FOREIGN KEY(IdGallery) REFERENCES Gallery(IdGallery)
);
 
CREATE TABLE IF NOT EXISTS CommunityMember(
   IdMember INT,
   Name VARCHAR(50) NOT NULL,
   Email VARCHAR(50) NOT NULL,
   City VARCHAR(50) NOT NULL,
   PRIMARY KEY(IdMember)
);
 
CREATE TABLE IF NOT EXISTS Workshop(
   IdWorkshop INT,
   Title VARCHAR(50),
   Date_ DATETIME NOT NULL,
   Price DECIMAL(15,2) NOT NULL,
   Level VARCHAR(50) NOT NULL,
   IdArtist INT NOT NULL,
   PRIMARY KEY(IdWorkshop),
   FOREIGN KEY(IdArtist) REFERENCES Artist(IdArtist)
);
 
CREATE TABLE IF NOT EXISTS RegisterExhibition(
   IdExhibition INT,
   IdMember INT,
   PRIMARY KEY(IdExhibition, IdMember),
   FOREIGN KEY(IdExhibition) REFERENCES Exhibition(IdExhibition),
   FOREIGN KEY(IdMember) REFERENCES CommunityMember(IdMember)
);
 
CREATE TABLE IF NOT EXISTS Registerworkshop(
   IdMember INT,
   IdWorkshop INT,
   PRIMARY KEY(IdMember, IdWorkshop),
   FOREIGN KEY(IdMember) REFERENCES CommunityMember(IdMember),
   FOREIGN KEY(IdWorkshop) REFERENCES Workshop(IdWorkshop)
);



-- ============================================================
--  ArtConnect – Script d'insertion des données d'exemple
--  Généré pour le projet TI603 – Groupe 2
-- ============================================================

USE ArtConnect;

-- ============================================================
-- TABLE : Artist  (15 artistes)
-- ============================================================
INSERT INTO Artist (IdArtist, Email, Name, City, BirthYear) VALUES
(1,  'sophie.martin@art.fr',          'Sophie Martin',       'Paris',        1985),
(2,  'lucas.bernard@studio.fr',       'Lucas Bernard',       'Lyon',         1978),
(3,  'amira.hassan@gallery.eg',       'Amira Hassan',        'Marseille',    1992),
(4,  'kenji.tanaka@artmail.jp',       'Kenji Tanaka',        'Paris',        1969),
(5,  'elena.volkov@brush.ru',         'Elena Volkov',        'Bordeaux',     1990),
(6,  'marc.dupont@photoart.fr',       'Marc Dupont',         'Nantes',       1975),
(7,  'isabelle.noir@sculpt.fr',       'Isabelle Noir',       'Toulouse',     1983),
(8,  'carlos.reyes@arte.mx',          'Carlos Reyes',        'Paris',        1988),
(9,  'fatima.benali@creation.ma',     'Fatima Benali',       'Lille',        1995),
(10, 'pierre.leroy@gravure.fr',       'Pierre Leroy',        'Strasbourg',   1971),
(11, 'nadia.schmidt@kunst.de',        'Nadia Schmidt',       'Paris',        1980),
(12, 'thomas.girard@digital.fr',      'Thomas Girard',       'Rennes',       1993),
(13, 'yuki.nakamura@washi.jp',        'Yuki Nakamura',       'Nice',         1987),
(14, 'omar.diallo@pigment.sn',        'Omar Diallo',         'Paris',        1976),
(15, 'chloe.petit@aquarelle.fr',      'Chloé Petit',         'Grenoble',     1998);

-- ============================================================
-- TABLE : Gallery  (8 galeries)
-- ============================================================
INSERT INTO Gallery (IdGallery, Name, Rating, StreetName, City) VALUES
(1, 'Galerie Lumière',       4.80, '12 Rue de Rivoli',          'Paris'),
(2, 'Espace Contemporain',   4.50, '34 Avenue des Arts',        'Lyon'),
(3, 'La Friche Sud',         4.20, '7 Boulevard Michelet',      'Marseille'),
(4, 'Galerie du Nord',       4.65, '89 Rue Faidherbe',          'Lille'),
(5, 'Maison des Arts',       4.90, '3 Place du Capitole',       'Toulouse'),
(6, 'Atelier Atlantique',    4.10, '22 Quai de la Fosse',       'Nantes'),
(7, 'Galerie Alsace',        4.70, '15 Rue des Dentelles',      'Strasbourg'),
(8, 'Studio Azur',           4.35, '55 Promenade des Anglais',  'Nice');

-- ============================================================
-- TABLE : Artwork  (30 œuvres)
-- ============================================================
INSERT INTO Artwork (IdArtwork, Title, Price, Status, Type, IdArtist) VALUES
-- Sophie Martin (peinture)
(1,  'Reflets sur la Seine',        3500.00,  'FOR_SALE',  'Painting',      1),
(2,  'Lumière d''Automne',          2800.00,  'SOLD',      'Painting',      1),
(3,  'Paris Brumeux',               4200.00,  'FOR_SALE',  'Painting',      1),
-- Lucas Bernard (sculpture)
(4,  'L''Équilibre Fragile',        8500.00,  'FOR_SALE',  'Sculpture',     2),
(5,  'Mémoire de Pierre',          12000.00,  'SOLD',      'Sculpture',     2),
-- Amira Hassan (mixed media)
(6,  'Fusion Méditerranée',         1900.00,  'FOR_SALE',  'Mixed Media',   3),
(7,  'Origines',                    2200.00,  'FOR_SALE',  'Mixed Media',   3),
-- Kenji Tanaka (gravure / estampe)
(8,  'Le Mont au Crépuscule',       5500.00,  'FOR_SALE',  'Printmaking',   4),
(9,  'Vagues de Nuit',              4800.00,  'SOLD',      'Printmaking',   4),
(10, 'Silence Japonais',            6200.00,  'FOR_SALE',  'Printmaking',   4),
-- Elena Volkov (peinture)
(11, 'Forêt Boréale',               3100.00,  'FOR_SALE',  'Painting',      5),
(12, 'Épopée Blanche',              2700.00,  'FOR_SALE',  'Painting',      5),
-- Marc Dupont (photographie)
(13, 'Visages de Nantes',            950.00,  'FOR_SALE',  'Photography',   6),
(14, 'Nuit Industrielle',           1400.00,  'SOLD',      'Photography',   6),
(15, 'Brume sur l''Estuaire',       1100.00,  'FOR_SALE',  'Photography',   6),
-- Isabelle Noir (sculpture)
(16, 'Torsion',                     7200.00,  'FOR_SALE',  'Sculpture',     7),
(17, 'Racines',                     9500.00,  'SOLD',      'Sculpture',     7),
-- Carlos Reyes (peinture muraliste)
(18, 'Couleurs du Mexique',         3800.00,  'FOR_SALE',  'Painting',      8),
(19, 'La Ville Invisible',          4100.00,  'FOR_SALE',  'Painting',      8),
-- Fatima Benali (illustration numérique)
(20, 'Géométries Berbères',         1200.00,  'FOR_SALE',  'Digital Art',   9),
(21, 'Atlas en Couleurs',            980.00,  'FOR_SALE',  'Digital Art',   9),
-- Pierre Leroy (gravure)
(22, 'Cathédrale de Traits',        3300.00,  'FOR_SALE',  'Printmaking',  10),
(23, 'Les Quais',                   2600.00,  'SOLD',      'Printmaking',  10),
-- Nadia Schmidt (installation)
(24, 'Résonance',                  15000.00,  'FOR_SALE',  'Installation', 11),
(25, 'Transparences',              11000.00,  'SOLD',      'Installation', 11),
-- Thomas Girard (art digital)
(26, 'Data Flowers',                 750.00,  'FOR_SALE',  'Digital Art',  12),
(27, 'Algorithme#7',                 900.00,  'FOR_SALE',  'Digital Art',  12),
-- Yuki Nakamura (aquarelle)
(28, 'Jardin de Pluie',             2100.00,  'FOR_SALE',  'Watercolor',   13),
(29, 'Sakura sur Mer',              1850.00,  'SOLD',      'Watercolor',   13),
-- Omar Diallo (peinture)
(30, 'Savane Dorée',                4600.00,  'FOR_SALE',  'Painting',     14);

-- ============================================================
-- TABLE : Exhibition  (12 expositions)
-- ============================================================
INSERT INTO Exhibition (IdExhibition, Title, StartDate, Theme, IdGallery) VALUES
(1,  'Lumières de Paris',           '2025-09-15', 'Impressionnisme Urbain',       1),
(2,  'Formes & Matières',           '2025-10-03', 'Sculpture Contemporaine',      2),
(3,  'Méditerranée Plurielle',      '2025-11-20', 'Art Multiculturel',            3),
(4,  'L''Estampe Moderne',          '2026-01-10', 'Gravure et Impression',        7),
(5,  'Corps & Espace',              '2026-01-25', 'Installation et Performance',  5),
(6,  'La Ville Photographiée',      '2026-02-14', 'Photographie Urbaine',         6),
(7,  'Numérique & Sensible',        '2026-02-28', 'Art Numérique',                1),
(8,  'Racines du Monde',            '2026-03-12', 'Art Africain et Diaspora',     4),
(9,  'Aquarelles en Liberté',       '2026-03-30', 'Aquarelle Contemporaine',      8),
(10, 'Printemps des Sculptures',    '2026-04-05', 'Sculpture en Plein Air',       2),
(11, 'Géométries Sensibles',        '2026-05-01', 'Art Abstrait et Géométrique',  3),
(12, 'Regards Croisés',             '2026-06-15', 'Pluridisciplinaire',           1);

-- ============================================================
-- TABLE : Workshop  (15 ateliers)
-- ============================================================
INSERT INTO Workshop (IdWorkshop, Title, Date_, Price, Level, IdArtist) VALUES
(1,  'Initiation à l''Aquarelle',          '2025-10-05 10:00:00',  45.00,  'Beginner',     15),
(2,  'Peinture à l''Huile – Bases',        '2025-10-18 14:00:00',  60.00,  'Beginner',      1),
(3,  'Sculpture sur Argile',               '2025-11-08 09:30:00',  80.00,  'Intermediate',  7),
(4,  'Photographie de Rue',                '2025-11-22 13:00:00',  55.00,  'Beginner',      6),
(5,  'Gravure sur Bois',                   '2025-12-06 10:00:00',  70.00,  'Intermediate', 10),
(6,  'Art Numérique – Tablette Graphique', '2025-12-13 14:00:00',  65.00,  'Beginner',     12),
(7,  'Mixed Media – Collage Avancé',       '2026-01-17 10:00:00',  75.00,  'Advanced',      3),
(8,  'Estampe Japonaise',                  '2026-01-24 09:00:00',  90.00,  'Intermediate',  4),
(9,  'Portrait au Fusain',                 '2026-02-07 14:00:00',  50.00,  'Beginner',      5),
(10, 'Installation Artistique',            '2026-02-21 10:00:00', 120.00,  'Advanced',     11),
(11, 'Aquarelle Botanique',                '2026-03-07 10:00:00',  55.00,  'Intermediate', 13),
(12, 'Peinture Murale – Techniques',       '2026-03-21 09:00:00',  85.00,  'Advanced',      8),
(13, 'Photographie Argentique',            '2026-04-04 14:00:00',  70.00,  'Intermediate',  6),
(14, 'Sculpture Métal Récupéré',           '2026-04-18 10:00:00', 100.00,  'Advanced',      2),
(15, 'Illustration Numérique',             '2026-05-02 14:00:00',  60.00,  'Intermediate',  9);

-- ============================================================
-- TABLE : CommunityMember  (20 membres)
-- ============================================================
INSERT INTO CommunityMember (IdMember, Name, Email, City) VALUES
(1,  'Alice Moreau',       'alice.moreau@mail.fr',      'Paris'),
(2,  'Baptiste Renard',    'baptiste.renard@mail.fr',   'Lyon'),
(3,  'Camille Fontaine',   'camille.f@webmail.fr',      'Marseille'),
(4,  'David Chevalier',    'david.chev@inbox.fr',       'Paris'),
(5,  'Emma Leclerc',       'emma.leclerc@mail.fr',      'Bordeaux'),
(6,  'Florian Moulin',     'florian.m@artfan.fr',       'Nantes'),
(7,  'Gaëlle Simon',       'gaelle.simon@mail.fr',      'Toulouse'),
(8,  'Hugo Mercier',       'hugo.merc@webmail.fr',      'Lille'),
(9,  'Inès Blanc',         'ines.blanc@inbox.fr',       'Paris'),
(10, 'Julien Garnier',     'julien.g@mail.fr',          'Strasbourg'),
(11, 'Karine Dubois',      'karine.d@artlover.fr',      'Paris'),
(12, 'Léo Perrin',         'leo.perrin@mail.fr',        'Nice'),
(13, 'Manon Clement',      'manon.clem@webmail.fr',     'Rennes'),
(14, 'Nicolas Masson',     'nicolas.m@inbox.fr',        'Grenoble'),
(15, 'Océane Picard',      'oceane.p@mail.fr',          'Paris'),
(16, 'Paul Giraud',        'paul.giraud@artfan.fr',     'Lyon'),
(17, 'Quentin Roy',        'quentin.roy@mail.fr',       'Nantes'),
(18, 'Rachel Bonnet',      'rachel.b@webmail.fr',       'Marseille'),
(19, 'Samuel Faure',       'samuel.f@inbox.fr',         'Paris'),
(20, 'Théa Laurent',       'thea.laurent@mail.fr',      'Bordeaux');

-- ============================================================
-- TABLE : RegisterExhibition  (inscriptions membres → expositions)
-- Chaque exposition a entre 3 et 8 inscrits pour varier les cas
-- ============================================================
INSERT INTO RegisterExhibition (IdExhibition, IdMember) VALUES
-- Expo 1 : Lumières de Paris
(1,  1), (1,  4), (1,  9), (1, 11), (1, 15), (1, 19),
-- Expo 2 : Formes & Matières
(2,  2), (2,  6), (2, 16), (2,  7),
-- Expo 3 : Méditerranée Plurielle
(3,  3), (3,  8), (3, 18), (3,  5), (3, 12),
-- Expo 4 : L'Estampe Moderne
(4, 10), (4, 14), (4,  2),
-- Expo 5 : Corps & Espace
(5,  7), (5, 11), (5, 15), (5, 20), (5,  1), (5,  9), (5, 13),
-- Expo 6 : La Ville Photographiée
(6,  6), (6, 17), (6,  3), (6, 12),
-- Expo 7 : Numérique & Sensible
(7, 13), (7, 19), (7,  4), (7,  9), (7, 20),
-- Expo 8 : Racines du Monde
(8,  8), (8, 11), (8, 14), (8, 18), (8,  5), (8,  2),
-- Expo 9 : Aquarelles en Liberté
(9, 12), (9, 15), (9,  1), (9, 16),
-- Expo 10 : Printemps des Sculptures
(10,  4), (10,  6), (10, 10), (10, 17), (10, 19),
-- Expo 11 : Géométries Sensibles
(11,  3), (11,  7), (11, 13), (11, 20),
-- Expo 12 : Regards Croisés
(12,  1), (12,  5), (12,  9), (12, 11), (12, 14), (12, 16), (12, 19), (12, 20);

-- ============================================================
-- TABLE : Registerworkshop  (inscriptions membres → ateliers)
-- ============================================================
INSERT INTO Registerworkshop (IdMember, IdWorkshop) VALUES
-- Atelier 1 : Initiation Aquarelle
(1, 1), (5, 1), (12, 1), (14, 1), (20, 1),
-- Atelier 2 : Peinture à l'Huile Bases
(2, 2), (7, 2), (11, 2), (16, 2),
-- Atelier 3 : Sculpture sur Argile
(3, 3), (6, 3), (15, 3),
-- Atelier 4 : Photographie de Rue
(4, 4), (8, 4), (13, 4), (17, 4), (19, 4),
-- Atelier 5 : Gravure sur Bois
(10, 5), (14, 5), (2, 5),
-- Atelier 6 : Art Numérique Tablette
(9, 6), (13, 6), (20, 6), (4, 6),
-- Atelier 7 : Mixed Media Avancé
(11, 7), (15, 7), (19, 7),
-- Atelier 8 : Estampe Japonaise
(10, 8), (12, 8), (16, 8), (18, 8),
-- Atelier 9 : Portrait au Fusain
(1, 9), (5, 9), (7, 9), (8, 9), (14, 9), (17, 9),
-- Atelier 10 : Installation Artistique
(11, 10), (15, 10), (19, 10),
-- Atelier 11 : Aquarelle Botanique
(1, 11), (12, 11), (20, 11), (5, 11),
-- Atelier 12 : Peinture Murale
(3, 12), (6, 12), (18, 12),
-- Atelier 13 : Photographie Argentique
(4, 13), (8, 13), (13, 13), (17, 13),
-- Atelier 14 : Sculpture Métal
(2, 14), (6, 14), (9, 14),
-- Atelier 15 : Illustration Numérique
(9, 15), (13, 15), (16, 15), (20, 15);

-- ============================================================
-- FIN DU SCRIPT D'INSERTION
-- ============================================================