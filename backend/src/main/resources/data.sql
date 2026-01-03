-- Swiss Ski Resorts - Sample Data
--
-- This file contains sample data for demonstration purposes.
-- To import REAL data from OpenStreetMap:
--
-- 1. Start the application
-- 2. Call POST /api/import/all to import all Swiss ski resorts
-- 3. This will fetch:
--    - Ski lifts from OpenStreetMap (Overpass API)
--    - Ski pistes/slopes from OpenStreetMap
--    - Drive times from Zurich (OSRM routing)
--    - Elevation data (Open Elevation API)
--
-- Or import individual resorts:
--   POST /api/import/resort?name=Zermatt
--   POST /api/import/resort/{id}/lifts
--   POST /api/import/resort/{id}/elevations

-- Sample data (will be replaced when you run the import)
INSERT INTO ski_resorts (name, region, canton, drive_time_from_zurich_minutes, min_elevation, max_elevation, blue_slopes, red_slopes, black_slopes, total_slope_km, latitude, longitude, website_url) VALUES
('Zermatt', 'Valais', 'VS', 210, 1620, 3883, 21, 40, 20, 360.0, 46.0207, 7.7491, 'https://www.zermatt.ch'),
('Arosa Lenzerheide', 'Graubünden', 'GR', 90, 1230, 2865, 42, 38, 12, 225.0, 46.7833, 9.6833, 'https://arosalenzerheide.swiss'),
('St. Moritz', 'Engadin', 'GR', 150, 1750, 3303, 25, 35, 18, 350.0, 46.4908, 9.8355, 'https://www.stmoritz.com'),
('Verbier', 'Valais', 'VS', 180, 1500, 3330, 35, 50, 25, 410.0, 46.0964, 7.2286, 'https://www.verbier.ch'),
('Davos Klosters', 'Graubünden', 'GR', 120, 1124, 2844, 38, 42, 10, 300.0, 46.8027, 9.8360, 'https://www.davos.ch'),
('Laax', 'Graubünden', 'GR', 100, 1100, 3018, 30, 40, 15, 224.0, 46.8079, 9.2593, 'https://www.laax.com'),
('Engelberg-Titlis', 'Central Switzerland', 'OW', 60, 1000, 3020, 15, 18, 8, 82.0, 46.8210, 8.4054, 'https://www.engelberg.ch'),
('Grindelwald', 'Bernese Oberland', 'BE', 120, 944, 2970, 45, 50, 20, 213.0, 46.6244, 8.0413, 'https://www.grindelwald.swiss'),
('Saas-Fee', 'Valais', 'VS', 200, 1800, 3600, 20, 30, 10, 100.0, 46.1088, 7.9278, 'https://www.saas-fee.ch'),
('Adelboden', 'Bernese Oberland', 'BE', 100, 1068, 2362, 35, 45, 10, 210.0, 46.4920, 7.5610, 'https://www.adelboden.ch'),
('Crans-Montana', 'Valais', 'VS', 180, 1500, 3000, 30, 35, 15, 140.0, 46.3113, 7.4797, 'https://www.crans-montana.ch'),
('Andermatt', 'Central Switzerland', 'UR', 90, 1444, 2961, 22, 28, 8, 120.0, 46.6333, 8.5936, 'https://www.andermatt-swissalps.ch');

-- Sample lifts for Zermatt (import real data with POST /api/import/resort/1/lifts)
INSERT INTO lifts (name, lift_type, start_elevation, end_elevation, length_meters, capacity_per_hour, start_latitude, start_longitude, end_latitude, end_longitude, ski_resort_id) VALUES
('Matterhorn Glacier Paradise', 'CABLE_CAR', 2939, 3883, 3200, 2000, 46.0150, 7.7400, 45.9380, 7.7300, 1),
('Sunnegga Express', 'FUNICULAR', 1620, 2288, 1400, 3000, 46.0207, 7.7491, 46.0300, 7.7600, 1),
('Rothorn', 'GONDOLA', 2288, 3103, 2100, 2400, 46.0300, 7.7600, 46.0450, 7.7700, 1),
('Gornergrat Bahn', 'FUNICULAR', 1620, 3089, 9340, 1800, 46.0207, 7.7491, 45.9840, 7.7860, 1),
('Furi-Schwarzsee', 'GONDOLA', 1867, 2583, 1800, 2200, 46.0100, 7.7350, 45.9900, 7.7250, 1);

-- Sample lifts for Arosa Lenzerheide
INSERT INTO lifts (name, lift_type, start_elevation, end_elevation, length_meters, capacity_per_hour, start_latitude, start_longitude, end_latitude, end_longitude, ski_resort_id) VALUES
('Weisshorn', 'CABLE_CAR', 1850, 2653, 2500, 1200, 46.7900, 9.6600, 46.7950, 9.6700, 2),
('Hörnli Express', 'GONDOLA', 1850, 2511, 1800, 2000, 46.7850, 9.6650, 46.7900, 9.6750, 2),
('Urdenbahn', 'GONDOLA', 1850, 2200, 1200, 2400, 46.7800, 9.6700, 46.7750, 9.6900, 2),
('Carmenna', 'CHAIRLIFT', 2000, 2585, 1500, 2000, 46.7820, 9.6800, 46.7900, 9.6850, 2);

-- Sample elevation points for Matterhorn Glacier Paradise
INSERT INTO elevation_points (sequence_number, latitude, longitude, elevation, distance_from_start, lift_id) VALUES
(0, 46.0150, 7.7400, 2939, 0, 1),
(1, 46.0070, 7.7380, 3100, 400, 1),
(2, 45.9990, 7.7360, 3280, 800, 1),
(3, 45.9910, 7.7340, 3450, 1200, 1),
(4, 45.9830, 7.7330, 3600, 1600, 1),
(5, 45.9750, 7.7320, 3720, 2000, 1),
(6, 45.9670, 7.7315, 3790, 2400, 1),
(7, 45.9590, 7.7310, 3840, 2800, 1),
(8, 45.9510, 7.7305, 3870, 3000, 1),
(9, 45.9380, 7.7300, 3883, 3200, 1);
