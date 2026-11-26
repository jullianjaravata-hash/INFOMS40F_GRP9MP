-- OPTIONAL test data so the Champion Stats tab has something to show.
-- Run in MySQL Workbench / the mysql client AFTER selecting the database:  USE leaguechampreport;
-- Numbers are made up (not real Riot data). Needs the champions Ahri, Yasuo and Thresh to exist.

-- Patches (skipped if the patch number already exists)
INSERT IGNORE INTO Patch (patch_number, release_date, total_games_analyzed, patch_notes_summary) VALUES
('26.19', '2026-10-01', 480000, 'Sample patch'),
('26.20', '2026-10-15', 495000, 'Sample patch'),
('26.21', '2026-10-29', 470000, 'Sample patch');

-- Stats rows
INSERT INTO Champion_Patch_Stats (champion_code, patch_number, lane, games_played, wins, bans)
SELECT champion_code, '26.19', 'Mid', 38000, 19500, 14000 FROM Champion WHERE champion_name = 'Ahri';

INSERT INTO Champion_Patch_Stats (champion_code, patch_number, lane, games_played, wins, bans)
SELECT champion_code, '26.20', 'Mid', 36500, 18100, 15500 FROM Champion WHERE champion_name = 'Ahri';

INSERT INTO Champion_Patch_Stats (champion_code, patch_number, lane, games_played, wins, bans)
SELECT champion_code, '26.19', 'Top', 21000, 10300, 30000 FROM Champion WHERE champion_name = 'Yasuo';

INSERT INTO Champion_Patch_Stats (champion_code, patch_number, lane, games_played, wins, bans)
SELECT champion_code, '26.19', 'Support', 33000, 16400, 19000 FROM Champion WHERE champion_name = 'Thresh';
