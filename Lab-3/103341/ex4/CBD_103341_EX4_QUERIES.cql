USE cbd_103341_ex4;

-- insert some
INSERT INTO attacks (name, type, power, accuracy, description, generation) VALUES ('Eruption', 'fire', 150, 100, 'The higher the users HP, the more powerful this attack becomes.', 3);
INSERT INTO attacks (name, type, power, accuracy, description, generation) VALUES ('Petal Dance', 'grass', 120, 100, 'The user attacks the target by scattering petals, then becomes fixated on using this move.', 1);
INSERT INTO pokemon (id, name, types, generation, stats, moves) VALUES (115, 'Kangaskhan', {'fighting'}, 1, [105, 95, 80, 40, 80, 90], {'Mega Punch'});
INSERT INTO routes (name, connections, generation) VALUES ('Lavender Town', {'north': 'Route 50', 'west': 'Route 8'}, 2);

-- 2 indexes
DROP INDEX IF EXISTS pkmn_names;
CREATE INDEX pkmn_names ON pokemon(name);

DROP INDEX IF EXISTS attacks_power;
CREATE INDEX attacks_power ON attacks(power);

-- 5 updates
UPDATE pokemon SET moves = moves + {'Eruption'} WHERE generation = 2 and id = 157;
UPDATE pokemon SET moves = moves + {'Petal Dance'} WHERE generation = 1 and id = 3;
UPDATE pokemon SET types = types - {'fighting'} WHERE id = 115 and generation = 1;
UPDATE pokemon SET types = types + {'normal'} WHERE id = 115 and generation = 1;
UPDATE routes SET connections['north'] = 'Route 10' WHERE name = 'Lavender Town' and generation = 1;
UPDATE routes SET connections = {'south': 'Route 12'} WHERE name = 'Lavender Town' and generation = 1;

-- 5 deletes
DELETE FROM routes WHERE name = 'Azalea Town' and generation = 2;
DELETE FROM attacks WHERE name = 'Thunder' and generation = 1;
DELETE FROM attacks WHERE name = 'Venoshock' and generation = 5;
DELETE FROM items WHERE name = 'Focus Sash' and generation = 4;
DELETE FROM items WHERE name = 'Choice Specs' and generation = 3;

-- 10 queries (uga-se)
-- 1. Saber toda a informação sobre o Pokémon com o id 1 -- ta mal
SELECT * FROM pokemon WHERE generation = 1 and id = 1;

-- 2. Saber o poder e precisão dos ataques de 1ª geração.
SELECT power, accuracy FROM attacks WHERE generation = 1;

-- 3. Saber todos os Pokémon de 9ª geração.
SELECT id, name FROM pokemon WHERE generation = 9;

-- 4. Saber os 5 ataques mais fortes. -- ta mal
SELECT * FROM attacks LIMIT 5;

-- 5. Saber todos os Ataques que o Quaquaval consegue aprender.
SELECT moves FROM pokemon WHERE name = 'Quaquaval';

-- 6. Todos os items introduzidos na primeira geração.
SELECT * FROM items WHERE generation = 1;

-- 7. Saber as stats do Pokémon com id 1.
SELECT stats FROM pokemon WHERE generation = 1 and id = 1;

-- 8. Saber todos os ataques de 9a geração com power igual a 80.
SELECT name, power, description FROM attacks WHERE generation = 9 and power = 80;

-- 9. Todos os items de 4a geração.
SELECT * FROM items WHERE generation = 4;

-- 10. Todos as routes.
SELECT * FROM routes;