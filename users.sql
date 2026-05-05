DROP USER IF EXISTS 'artiste'@'localhost';
CREATE USER 'artiste'@'localhost';
DROP USER IF EXISTS 'membre'@'localhost';
CREATE USER 'membre'@'localhost';
DROP USER IF EXISTS 'admin'@'localhost';
CREATE USER 'admin'@'localhost';

GRANT SELECT ON ArtConnect.view_artist_portfolio TO 'artiste'@'localhost';
GRANT SELECT ON ArtConnect.view_artist_workshop_members TO 'artiste'@'localhost';

GRANT SELECT ON ArtConnect.view_upcoming_events TO 'membre'@'localhost';
GRANT SELECT ON ArtConnect.view_member_registrations TO 'membre'@'localhost';
GRANT SELECT ON ArtConnect.view_artworks_for_sale TO 'membre'@'localhost';

GRANT SELECT ON ArtConnect.view_admin_members TO 'admin'@'localhost';
GRANT SELECT ON ArtConnect.view_activity_summary TO 'admin'@'localhost';
GRANT SELECT ON ArtConnect.view_upcoming_events TO 'admin'@'localhost';
GRANT SELECT ON ArtConnect.view_member_registrations TO 'admin'@'localhost';
GRANT SELECT ON ArtConnect.view_artworks_for_sale TO 'admin'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON ArtConnect.* TO 'admin'@'localhost';

FLUSH PRIVILEGES;