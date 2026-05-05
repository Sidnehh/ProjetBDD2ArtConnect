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