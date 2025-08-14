-- Tạo database
CREATE DATABASE kicap_clone;
GO
USE kicap_clone;
GO

-- Bảng Users (admin/editor)
CREATE TABLE Users(
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password_hash VARCHAR(200) NOT NULL,
  full_name NVARCHAR(100) NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'EDITOR', -- ADMIN | EDITOR
  active BIT NOT NULL DEFAULT 1,
  created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);

-- Bảng log đổi quyền
CREATE TABLE UserRoleAudit(
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  actor_id BIGINT NOT NULL,
  target_id BIGINT NOT NULL,
  old_role VARCHAR(20) NOT NULL,
  new_role VARCHAR(20) NOT NULL,
  changed_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);

-- Bảng thương hiệu
CREATE TABLE Brands(
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  name NVARCHAR(100) NOT NULL UNIQUE
);

-- Bảng danh mục
CREATE TABLE Categories(
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  name NVARCHAR(100) NOT NULL,
  slug VARCHAR(150) NOT NULL UNIQUE
);

-- Bảng sản phẩm
CREATE TABLE Products(
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  category_id BIGINT NOT NULL,
  brand_id BIGINT NULL,
  name NVARCHAR(200) NOT NULL,
  slug VARCHAR(200) NOT NULL UNIQUE,
  sku NVARCHAR(100) NULL,
  description NVARCHAR(MAX) NULL,
  base_price DECIMAL(18,2) NOT NULL,
  sale_price DECIMAL(18,2) NULL,
  thumbnail_url NVARCHAR(500) NULL,
  active BIT NOT NULL DEFAULT 1,
  created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  updated_at DATETIME2 NULL,
  CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES Categories(id),
  CONSTRAINT fk_products_brand FOREIGN KEY (brand_id) REFERENCES Brands(id)
);

-- Bảng biến thể sản phẩm
CREATE TABLE ProductVariants(
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  product_id BIGINT NOT NULL,
  name NVARCHAR(120) NOT NULL,
  price DECIMAL(18,2) NOT NULL,
  stock INT NOT NULL DEFAULT 0,
  CONSTRAINT fk_variants_product FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE
);

-- Bảng đơn hàng
CREATE TABLE Orders(
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  code VARCHAR(20) NOT NULL UNIQUE,
  customer_name NVARCHAR(100) NOT NULL,
  phone VARCHAR(20) NOT NULL,
  address NVARCHAR(255) NOT NULL,
  note NVARCHAR(500) NULL,
  total DECIMAL(18,2) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);

-- Bảng chi tiết đơn hàng
CREATE TABLE OrderItems(
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  variant_id BIGINT NULL,
  qty INT NOT NULL,
  price DECIMAL(18,2) NOT NULL,
  CONSTRAINT fk_orderitems_order FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE,
  CONSTRAINT fk_orderitems_product FOREIGN KEY (product_id) REFERENCES Products(id),
  CONSTRAINT fk_orderitems_variant FOREIGN KEY (variant_id) REFERENCES ProductVariants(id)
);

-- Seed Users (password_hash cần generate BCrypt)
INSERT INTO Users(username, password_hash, full_name, role, active)
VALUES ('admin', '$2a$10$PLACEHOLDER_HASH', N'Quản trị viên', 'ADMIN', 1);

-- Seed Brands
INSERT INTO Brands(name) VALUES (N'Mchose'), (N'AULA'), (N'Leobog');

-- Seed Categories
INSERT INTO Categories(name, slug) VALUES
 (N'Bàn phím cơ', 'ban-phim-co'),
 (N'Keycap bộ', 'keycap-bo'),
 (N'Phụ kiện bàn phím', 'phu-kien-ban-phim'),
 (N'Màn hình PC', 'man-hinh-pc');

-- Seed Products
INSERT INTO Products(category_id, brand_id, name, slug, sku, description, base_price, sale_price, thumbnail_url)
VALUES
 ((SELECT id FROM Categories WHERE slug='ban-phim-co'),
  (SELECT id FROM Brands WHERE name=N'Mchose'),
  N'Bàn phím cơ Mchose G75 Pro',
  'ban-phim-co-mchose-g75-pro',
  N'PVN1327',
  N'Layout 75%, LED RGB, gasket mounted, hotswap 5 pin...',
  1080000, NULL,
  'https://via.placeholder.com/600x400.png?text=Mchose+G75+Pro'),

 ((SELECT id FROM Categories WHERE slug='ban-phim-co'),
  (SELECT id FROM Brands WHERE name=N'AULA'),
  N'Bàn phím cơ AULA F75',
  'ban-phim-co-aula-f75',
  N'PVN9999',
  N'Bàn phím 75% giá tốt.',
  900000, NULL,
  'https://via.placeholder.com/600x400.png?text=AULA+F75'),

 ((SELECT id FROM Categories WHERE slug='keycap-bo'),
  (SELECT id FROM Brands WHERE name=N'Leobog'),
  N'Keycap Leobog Theme Ocean',
  'keycap-leobog-ocean',
  N'KC001',
  N'Keycap PBT dye-sub profile XDA',
  350000, NULL,
  'https://via.placeholder.com/600x400.png?text=Leobog+Ocean');

-- Seed ProductVariants cho Mchose G75 Pro
DECLARE @pid1 BIGINT = (SELECT id FROM Products WHERE slug='ban-phim-co-mchose-g75-pro');
INSERT INTO ProductVariants(product_id, name, price, stock) VALUES
 (@pid1, N'Island', 1080000, 10),
 (@pid1, N'Pink', 1080000, 8),
 (@pid1, N'Snow', 1190000, 5),
 (@pid1, N'Black Matcha', 1150000, 5),
 (@pid1, N'Black Pink', 980000, 0);

-- Seed ProductVariants cho AULA F75
DECLARE @pid2 BIGINT = (SELECT id FROM Products WHERE slug='ban-phim-co-aula-f75');
INSERT INTO ProductVariants(product_id, name, price, stock) VALUES
 (@pid2, N'Trắng', 900000, 15),
 (@pid2, N'Đen', 900000, 12);

-- Seed ProductVariants cho Keycap Ocean
DECLARE @pid3 BIGINT = (SELECT id FROM Products WHERE slug='keycap-leobog-ocean');
INSERT INTO ProductVariants(product_id, name, price, stock) VALUES
 (@pid3, N'Ocean Blue', 350000, 20);
