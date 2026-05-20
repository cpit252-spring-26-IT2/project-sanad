insert into users (name, email, password_hash, role) values
('Demo Customer', 'customer@sanad.sa', '$2y$10$8pkdZt.skKO7uifNIJlQD.CLoB9Fg6//XPsJ3s.TqsfbRcg0galOu', 'CUSTOMER'),
('Demo Reviewer', 'reviewer@sanad.sa', '$2y$10$8pkdZt.skKO7uifNIJlQD.CLoB9Fg6//XPsJ3s.TqsfbRcg0galOu', 'CUSTOMER'),
('Toney Owner', 'toney@sanad.sa', '$2y$10$Lr8NL1EXe5r6A3pn/xZZs.gGk3pUW1NbVpkXmY86FUZJRf37WzwVK', 'SHOP_OWNER'),
('Jeddah Plumbing Owner', 'jeddah@sanad.sa', '$2y$10$Lr8NL1EXe5r6A3pn/xZZs.gGk3pUW1NbVpkXmY86FUZJRf37WzwVK', 'SHOP_OWNER'),
('Al Noor Owner', 'alnoor@sanad.sa', '$2y$10$Lr8NL1EXe5r6A3pn/xZZs.gGk3pUW1NbVpkXmY86FUZJRf37WzwVK', 'SHOP_OWNER'),
('BuildPro Owner', 'buildpro@sanad.sa', '$2y$10$Lr8NL1EXe5r6A3pn/xZZs.gGk3pUW1NbVpkXmY86FUZJRf37WzwVK', 'SHOP_OWNER'),
('Red Sea Owner', 'redsea@sanad.sa', '$2y$10$Lr8NL1EXe5r6A3pn/xZZs.gGk3pUW1NbVpkXmY86FUZJRf37WzwVK', 'SHOP_OWNER');

insert into shops (owner_id, name, description, contact_info, category) values
((select id from users where email = 'toney@sanad.sa'), 'Toney Flooring', 'Specialized flooring materials and fixtures', '+966-50-111-2222', 'Flooring'),
((select id from users where email = 'jeddah@sanad.sa'), 'Jeddah Plumbing Supplies', 'Pipes and plumbing components', '+966-50-333-4444', 'Plumbing'),
((select id from users where email = 'alnoor@sanad.sa'), 'Al Noor Electrical', 'Electrical tools and cables', '+966-50-555-6666', 'Electrical'),
((select id from users where email = 'buildpro@sanad.sa'), 'BuildPro Materials', 'General building materials and hand tools', '+966-50-777-8888', 'Building Materials'),
((select id from users where email = 'redsea@sanad.sa'), 'Red Sea Paints', 'Paints and finishes for projects', '+966-50-999-0000', 'Paint');

insert into categories (name, parent_id, slug) values
('Flooring', null, 'flooring'),
('Plumbing', null, 'plumbing'),
('Electrical', null, 'electrical'),
('Paint', null, 'paint'),
('Tools', null, 'tools'),
('Bathroom Fixtures', null, 'bathroom-fixtures'),
('Building Materials', null, 'building-materials');

insert into products (name, variant, description, category_id, image_url) values
('Ceramic Floor Tile', '60x60 Matte', 'Durable ceramic floor tile for homes and offices.', (select id from categories where slug = 'flooring'), '/images/circular-saw.png'),
('Copper Pipe', '3/4 inch', 'Corrosion-resistant copper pipe for plumbing systems.', (select id from categories where slug = 'plumbing'), '/images/copper-pipe.png'),
('Plastic Pipe', '1/2 inch', 'PVC pipe suitable for water distribution.', (select id from categories where slug = 'plumbing'), '/images/steel-pipe.png'),
('Cement Bag', '50 KG', 'General-purpose cement bag for construction.', (select id from categories where slug = 'building-materials'), '/images/nails.png'),
('Paint Bucket', '20L White', 'Interior/exterior paint with strong coverage.', (select id from categories where slug = 'paint'), '/images/power-drill.png'),
('Hammer', 'Wooden Handle', 'Reliable hammer for construction and maintenance.', (select id from categories where slug = 'tools'), '/images/hammer.png'),
('Bathroom Sink', 'Porcelain', 'Modern sink with durable porcelain finish.', (select id from categories where slug = 'bathroom-fixtures'), '/images/circular-saw.png'),
('Electrical Cable', '10m Roll', 'Insulated cable suitable for residential wiring.', (select id from categories where slug = 'electrical'), '/images/power-drill.png');

insert into product_offers (product_id, shop_id, price, available, stock_quantity) values
((select id from products where name = 'Ceramic Floor Tile'), (select id from shops where name = 'Toney Flooring'), 37.50, true, 120),
((select id from products where name = 'Ceramic Floor Tile'), (select id from shops where name = 'BuildPro Materials'), 39.00, true, 90),

((select id from products where name = 'Copper Pipe'), (select id from shops where name = 'Jeddah Plumbing Supplies'), 26.75, true, 250),
((select id from products where name = 'Copper Pipe'), (select id from shops where name = 'BuildPro Materials'), 28.00, true, 120),

((select id from products where name = 'Plastic Pipe'), (select id from shops where name = 'Jeddah Plumbing Supplies'), 12.50, true, 300),
((select id from products where name = 'Plastic Pipe'), (select id from shops where name = 'BuildPro Materials'), 13.40, true, 200),

((select id from products where name = 'Cement Bag'), (select id from shops where name = 'BuildPro Materials'), 19.90, true, 500),

((select id from products where name = 'Paint Bucket'), (select id from shops where name = 'Red Sea Paints'), 79.00, false, 0),
((select id from products where name = 'Paint Bucket'), (select id from shops where name = 'BuildPro Materials'), 82.00, true, 40),

((select id from products where name = 'Hammer'), (select id from shops where name = 'BuildPro Materials'), 24.00, true, 175),
((select id from products where name = 'Hammer'), (select id from shops where name = 'Toney Flooring'), 25.50, true, 80),

((select id from products where name = 'Bathroom Sink'), (select id from shops where name = 'Toney Flooring'), 255.00, true, 35),
((select id from products where name = 'Bathroom Sink'), (select id from shops where name = 'BuildPro Materials'), 262.00, true, 22),

((select id from products where name = 'Electrical Cable'), (select id from shops where name = 'Al Noor Electrical'), 64.50, true, 140),
((select id from products where name = 'Electrical Cable'), (select id from shops where name = 'BuildPro Materials'), 67.00, true, 95);

insert into reviews (customer_id, target_type, target_id, rating) values
((select id from users where email = 'customer@sanad.sa'), 'PRODUCT', (select id from products where name = 'Copper Pipe'), 5),
((select id from users where email = 'reviewer@sanad.sa'), 'PRODUCT', (select id from products where name = 'Copper Pipe'), 4),
((select id from users where email = 'customer@sanad.sa'), 'PRODUCT', (select id from products where name = 'Plastic Pipe'), 4),
((select id from users where email = 'reviewer@sanad.sa'), 'PRODUCT', (select id from products where name = 'Ceramic Floor Tile'), 5),
((select id from users where email = 'customer@sanad.sa'), 'PRODUCT', (select id from products where name = 'Hammer'), 4),

((select id from users where email = 'customer@sanad.sa'), 'SHOP', (select id from shops where name = 'Toney Flooring'), 5),
((select id from users where email = 'reviewer@sanad.sa'), 'SHOP', (select id from shops where name = 'Jeddah Plumbing Supplies'), 4),
((select id from users where email = 'customer@sanad.sa'), 'SHOP', (select id from shops where name = 'BuildPro Materials'), 4);
