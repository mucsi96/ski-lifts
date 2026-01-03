-- Swiss Ski Resorts Data
INSERT INTO ski_resorts (name, region, canton, drive_time_from_zurich_minutes, min_elevation, max_elevation, blue_slopes, red_slopes, black_slopes, total_slope_km, latitude, longitude, website_url, osm_relation_id) VALUES
('Arosa Lenzerheide', 'Graubünden', 'GR', 90, 1230, 2865, 42, 38, 12, 225.0, 46.7833, 9.6833, 'https://arosalenzerheide.swiss', 2191358),
('Zermatt', 'Valais', 'VS', 210, 1620, 3883, 21, 40, 20, 360.0, 46.0207, 7.7491, 'https://www.zermatt.ch', 1234567),
('St. Moritz', 'Engadin', 'GR', 150, 1750, 3303, 25, 35, 18, 350.0, 46.4908, 9.8355, 'https://www.stmoritz.com', 2345678),
('Verbier', 'Valais', 'VS', 180, 1500, 3330, 35, 50, 25, 410.0, 46.0964, 7.2286, 'https://www.verbier.ch', 3456789),
('Davos Klosters', 'Graubünden', 'GR', 120, 1124, 2844, 38, 42, 10, 300.0, 46.8027, 9.8360, 'https://www.davos.ch', 4567890),
('Laax', 'Graubünden', 'GR', 100, 1100, 3018, 30, 40, 15, 224.0, 46.8079, 9.2593, 'https://www.laax.com', 5678901),
('Engelberg-Titlis', 'Central Switzerland', 'OW', 60, 1000, 3020, 15, 18, 8, 82.0, 46.8210, 8.4054, 'https://www.engelberg.ch', 6789012),
('Jungfrau Region', 'Bernese Oberland', 'BE', 120, 944, 2970, 45, 50, 20, 213.0, 46.5869, 7.9081, 'https://www.jungfrau.ch', 7890123),
('Saas-Fee', 'Valais', 'VS', 200, 1800, 3600, 20, 30, 10, 100.0, 46.1088, 7.9278, 'https://www.saas-fee.ch', 8901234),
('Adelboden-Lenk', 'Bernese Oberland', 'BE', 100, 1068, 2362, 35, 45, 10, 210.0, 46.4920, 7.5610, 'https://www.adelboden-lenk.ch', 9012345),
('Crans-Montana', 'Valais', 'VS', 180, 1500, 3000, 30, 35, 15, 140.0, 46.3113, 7.4797, 'https://www.crans-montana.ch', 1122334),
('Flims Laax Falera', 'Graubünden', 'GR', 100, 1100, 3018, 28, 35, 12, 224.0, 46.8360, 9.2870, 'https://www.flimslaax.com', 2233445);

-- Lifts for Arosa Lenzerheide (based on the ski map shown)
INSERT INTO lifts (name, lift_type, start_elevation, end_elevation, length_meters, capacity_per_hour, start_latitude, start_longitude, end_latitude, end_longitude, ski_resort_id, osm_way_id) VALUES
-- Valbella area lifts
('Valbella-Stätzerhorn', 'GONDOLA', 1500, 2575, 2800, 2400, 46.7850, 9.6950, 46.7920, 9.7200, 1, 123456),
('Scharmoin', 'CHAIRLIFT', 1540, 2200, 1500, 2000, 46.7880, 9.6900, 46.7950, 9.6850, 1, 234567),
('Panoramabahn', 'GONDOLA', 1935, 2653, 2100, 2800, 46.7700, 9.7100, 46.7620, 9.7250, 1, 345678),
('Stätzerföli', 'CHAIRLIFT', 2200, 2575, 900, 1800, 46.7920, 9.7150, 46.7950, 9.7200, 1, 456789),

-- Parpan area lifts
('Parpaner Rothorn', 'CABLE_CAR', 1493, 2865, 3200, 1500, 46.7600, 9.6700, 46.7550, 9.6850, 1, 567890),
('Prodaschier', 'CHAIRLIFT', 1493, 1950, 1100, 2000, 46.7600, 9.6650, 46.7580, 9.6750, 1, 678901),
('West', 'CHAIRLIFT', 1420, 1800, 950, 1600, 46.7570, 9.6550, 46.7600, 9.6600, 1, 789012),
('Alpina', 'DRAG_LIFT', 1460, 1620, 400, 800, 46.7550, 9.6580, 46.7570, 9.6620, 1, 890123),

-- Arosa side lifts
('Weisshorn', 'CABLE_CAR', 1850, 2653, 2500, 1200, 46.7900, 9.6600, 46.7950, 9.6700, 1, 901234),
('Hörnli', 'GONDOLA', 1850, 2511, 1800, 2000, 46.7850, 9.6650, 46.7900, 9.6750, 1, 112345),
('Brüggerhorn', 'CHAIRLIFT', 1750, 2269, 1400, 1800, 46.7950, 9.6550, 46.8000, 9.6600, 1, 223456),
('Carmenna', 'CHAIRLIFT', 2000, 2585, 1500, 2000, 46.7820, 9.6800, 46.7900, 9.6850, 1, 334567),

-- Heidbüel area
('Heidbüel', 'CHAIRLIFT', 1700, 1935, 700, 1400, 46.7650, 9.7050, 46.7700, 9.7100, 1, 445678),
('Stätz-Damiez', 'DRAG_LIFT', 1750, 1900, 500, 600, 46.7680, 9.7000, 46.7700, 9.7020, 1, 556789),
('Chilihütta', 'DRAG_LIFT', 1780, 1950, 450, 500, 46.7690, 9.6980, 46.7720, 9.7000, 1, 667890);

-- Lifts for Zermatt
INSERT INTO lifts (name, lift_type, start_elevation, end_elevation, length_meters, capacity_per_hour, start_latitude, start_longitude, end_latitude, end_longitude, ski_resort_id, osm_way_id) VALUES
('Matterhorn Glacier Paradise', 'CABLE_CAR', 2939, 3883, 3200, 2000, 46.0150, 7.7400, 45.9380, 7.7300, 2, 778901),
('Sunnegga', 'FUNICULAR', 1620, 2288, 1400, 3000, 46.0207, 7.7491, 46.0300, 7.7600, 2, 889012),
('Rothorn', 'GONDOLA', 2288, 3103, 2100, 2400, 46.0300, 7.7600, 46.0450, 7.7700, 2, 990123),
('Gornergrat Bahn', 'FUNICULAR', 1620, 3089, 9340, 1800, 46.0207, 7.7491, 45.9840, 7.7860, 2, 101234),
('Schwarzsee', 'GONDOLA', 1867, 2583, 1800, 2200, 46.0100, 7.7350, 45.9900, 7.7250, 2, 112346);

-- Lifts for St. Moritz
INSERT INTO lifts (name, lift_type, start_elevation, end_elevation, length_meters, capacity_per_hour, start_latitude, start_longitude, end_latitude, end_longitude, ski_resort_id, osm_way_id) VALUES
('Corviglia', 'FUNICULAR', 1775, 2486, 2200, 2500, 46.4908, 9.8355, 46.5050, 9.8400, 3, 123457),
('Piz Nair', 'CABLE_CAR', 2486, 3057, 1500, 1800, 46.5050, 9.8400, 46.5100, 9.8500, 3, 234568),
('Suvretta', 'CHAIRLIFT', 2100, 2700, 1400, 2000, 46.4850, 9.8200, 46.4900, 9.8300, 3, 345679),
('Signal', 'GONDOLA', 1800, 2150, 900, 2200, 46.4950, 9.8380, 46.5000, 9.8350, 3, 456780),
('Corvatsch', 'CABLE_CAR', 1870, 3303, 4600, 2000, 46.4600, 9.8100, 46.4200, 9.8200, 3, 567891);

-- Lifts for Verbier
INSERT INTO lifts (name, lift_type, start_elevation, end_elevation, length_meters, capacity_per_hour, start_latitude, start_longitude, end_latitude, end_longitude, ski_resort_id, osm_way_id) VALUES
('Mont Fort', 'CABLE_CAR', 2727, 3330, 1500, 1200, 46.0800, 7.2800, 46.0700, 7.2900, 4, 678902),
('Jumbo', 'GONDOLA', 1500, 2200, 2000, 2800, 46.0964, 7.2286, 46.1000, 7.2400, 4, 789013),
('Funispace', 'GONDOLA', 2200, 2727, 1600, 2400, 46.1000, 7.2400, 46.0800, 7.2800, 4, 890124),
('Attelas', 'CHAIRLIFT', 2200, 2730, 1400, 2000, 46.0950, 7.2500, 46.0850, 7.2600, 4, 901235),
('Lac des Vaux', 'CHAIRLIFT', 2550, 2960, 1100, 1800, 46.0750, 7.2750, 46.0650, 7.2850, 4, 112347);

-- Lifts for Davos Klosters
INSERT INTO lifts (name, lift_type, start_elevation, end_elevation, length_meters, capacity_per_hour, start_latitude, start_longitude, end_latitude, end_longitude, ski_resort_id, osm_way_id) VALUES
('Jakobshorn', 'GONDOLA', 1540, 2590, 2800, 2400, 46.7900, 9.8200, 46.7700, 9.8350, 5, 223457),
('Parsenn', 'FUNICULAR', 1560, 2662, 3100, 2800, 46.8027, 9.8360, 46.8200, 9.8100, 5, 334568),
('Weissfluhjoch', 'CABLE_CAR', 2662, 2844, 600, 2000, 46.8200, 9.8100, 46.8250, 9.8050, 5, 445679),
('Rinerhorn', 'GONDOLA', 1430, 2490, 2600, 2200, 46.7600, 9.8500, 46.7400, 9.8600, 5, 556780),
('Madrisa', 'GONDOLA', 1190, 1900, 1900, 2000, 46.8400, 9.8800, 46.8500, 9.8700, 5, 667891);

-- Lifts for Laax
INSERT INTO lifts (name, lift_type, start_elevation, end_elevation, length_meters, capacity_per_hour, start_latitude, start_longitude, end_latitude, end_longitude, ski_resort_id, osm_way_id) VALUES
('Crap Sogn Gion', 'GONDOLA', 1100, 2228, 3500, 2800, 46.8079, 9.2593, 46.8300, 9.2400, 6, 778902),
('Crap Masegn', 'CHAIRLIFT', 2228, 2477, 850, 2400, 46.8300, 9.2400, 46.8350, 9.2350, 6, 889013),
('Vorab Glacier', 'CABLE_CAR', 2477, 3018, 1800, 1800, 46.8350, 9.2350, 46.8500, 9.2200, 6, 990124),
('La Siala', 'CHAIRLIFT', 1485, 2086, 1600, 2200, 46.8200, 9.2500, 46.8280, 9.2450, 6, 101235),
('Foppa', 'CHAIRLIFT', 1220, 1900, 1800, 2000, 46.8100, 9.2550, 46.8180, 9.2480, 6, 112348);

-- Lifts for Engelberg-Titlis
INSERT INTO lifts (name, lift_type, start_elevation, end_elevation, length_meters, capacity_per_hour, start_latitude, start_longitude, end_latitude, end_longitude, ski_resort_id, osm_way_id) VALUES
('Titlis Rotair', 'CABLE_CAR', 2428, 3020, 1800, 1200, 46.7700, 8.4200, 46.7710, 8.3950, 7, 223458),
('Stand', 'GONDOLA', 1800, 2428, 2000, 2200, 46.8000, 8.4100, 46.7700, 8.4200, 7, 334569),
('Trübsee', 'GONDOLA', 1000, 1800, 2400, 2800, 46.8210, 8.4054, 46.8000, 8.4100, 7, 445680),
('Brunni', 'CABLE_CAR', 1000, 1860, 2200, 1500, 46.8200, 8.4200, 46.8350, 8.4350, 7, 556781),
('Jochstock', 'CHAIRLIFT', 1840, 2570, 1600, 1800, 46.7800, 8.4000, 46.7750, 8.3900, 7, 667892);

-- Lifts for Jungfrau Region
INSERT INTO lifts (name, lift_type, start_elevation, end_elevation, length_meters, capacity_per_hour, start_latitude, start_longitude, end_latitude, end_longitude, ski_resort_id, osm_way_id) VALUES
('Männlichen', 'GONDOLA', 944, 2230, 4500, 2400, 46.5960, 7.9150, 46.6120, 7.9400, 8, 778903),
('Kleine Scheidegg', 'FUNICULAR', 1287, 2061, 2100, 2000, 46.5869, 7.9081, 46.5850, 7.9620, 8, 889014),
('First', 'GONDOLA', 1050, 2168, 3200, 2800, 46.6600, 8.0400, 46.6700, 8.0600, 8, 990125),
('Schilthorn', 'CABLE_CAR', 1650, 2970, 4200, 1800, 46.5500, 7.8350, 46.5570, 7.8350, 8, 101236),
('Lauberhorn', 'CHAIRLIFT', 1400, 2472, 2800, 2200, 46.5900, 7.9200, 46.5750, 7.9100, 8, 112349);

-- Sample elevation points for one lift (Valbella-Stätzerhorn)
INSERT INTO elevation_points (sequence_number, latitude, longitude, elevation, distance_from_start, lift_id) VALUES
(0, 46.7850, 9.6950, 1500, 0, 1),
(1, 46.7860, 9.6975, 1620, 280, 1),
(2, 46.7870, 9.7000, 1750, 560, 1),
(3, 46.7880, 9.7050, 1890, 840, 1),
(4, 46.7890, 9.7100, 2030, 1120, 1),
(5, 46.7900, 9.7125, 2180, 1400, 1),
(6, 46.7905, 9.7150, 2320, 1680, 1),
(7, 46.7910, 9.7175, 2430, 1960, 1),
(8, 46.7915, 9.7190, 2510, 2240, 1),
(9, 46.7920, 9.7200, 2575, 2520, 1);

-- Sample elevation points for Panoramabahn
INSERT INTO elevation_points (sequence_number, latitude, longitude, elevation, distance_from_start, lift_id) VALUES
(0, 46.7700, 9.7100, 1935, 0, 3),
(1, 46.7690, 9.7115, 2020, 210, 3),
(2, 46.7680, 9.7135, 2115, 420, 3),
(3, 46.7670, 9.7155, 2210, 630, 3),
(4, 46.7660, 9.7175, 2305, 840, 3),
(5, 46.7650, 9.7195, 2400, 1050, 3),
(6, 46.7640, 9.7215, 2490, 1260, 3),
(7, 46.7630, 9.7235, 2570, 1470, 3),
(8, 46.7625, 9.7245, 2620, 1680, 3),
(9, 46.7620, 9.7250, 2653, 1890, 3);
