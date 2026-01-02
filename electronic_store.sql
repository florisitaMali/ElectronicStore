-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 02, 2026 at 02:49 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.4.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `electronic_store`
--

-- --------------------------------------------------------

--
-- Table structure for table `bills`
--

CREATE TABLE `bills` (
  `id` int(11) NOT NULL,
  `bill_number` bigint(20) NOT NULL,
  `sale_date` datetime NOT NULL,
  `employee_id` int(11) NOT NULL,
  `total` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bills`
--

INSERT INTO `bills` (`id`, `bill_number`, `sale_date`, `employee_id`, `total`) VALUES
(1, 2, '2025-12-28 20:49:09', 2, 750),
(3, 1, '2026-01-01 23:30:01', 2, 120);

-- --------------------------------------------------------

--
-- Table structure for table `bill_items`
--

CREATE TABLE `bill_items` (
  `bill_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `price` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bill_items`
--

INSERT INTO `bill_items` (`bill_id`, `item_id`, `quantity`, `price`) VALUES
(1, 9, 1, 750),
(3, 12, 1, 120);

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `sector` int(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `name`, `sector`) VALUES
(1, 'Laptops', 1),
(2, 'Smartphones', 2),
(3, 'Consoles', 6),
(4, 'Headphones', 4),
(5, 'Televisions', 3);

-- --------------------------------------------------------

--
-- Table structure for table `employees`
--

CREATE TABLE `employees` (
  `id` int(11) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(150) DEFAULT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `salary` double DEFAULT NULL,
  `role_id` int(11) NOT NULL,
  `sector_id` int(11) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `employees`
--

INSERT INTO `employees` (`id`, `first_name`, `last_name`, `username`, `password`, `email`, `phone`, `birth_date`, `salary`, `role_id`, `sector_id`, `deleted`) VALUES
(2, 'Anna', 'Cashier', 'cashier1', 'cash123', 'anna@mail.com', '0692222222', '1998-03-22', 700, 2, 1, 0),
(5, 'Klea', 'Mani', 'mana1', 'man1', 'man@empl.com', '0682356789', '1998-12-03', 800, 3, NULL, 0),
(6, 'Mik', 'Adi', 'admin1', 'admin1', 'ad@exmpl.com', '0653214569', '1998-12-03', 1000, 1, 4, 0),
(8, 'cashier2', 'cashier2', 'cash2', 'cash2', 'mail@mail.com', '0653201466', '1998-10-08', 600, 2, 4, 1),
(9, 'manager2', 'manger2', 'man2', 'man2', 'mail@mail.com', '0652312456', '1998-10-08', 600, 3, NULL, 0),
(11, 'cashier2', 'cash', 'cash23', 'cash23', 'mail@mail.coom', '0653514258', '1998-10-08', 1200, 2, 4, 0);

-- --------------------------------------------------------

--
-- Table structure for table `employee_permissions`
--

CREATE TABLE `employee_permissions` (
  `employee_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `employee_permissions`
--

INSERT INTO `employee_permissions` (`employee_id`, `permission_id`) VALUES
(2, 1),
(2, 2),
(2, 3),
(5, 1),
(5, 2),
(5, 3),
(5, 4),
(5, 5),
(5, 6),
(5, 7),
(5, 8),
(8, 1),
(8, 2),
(8, 3),
(9, 1),
(9, 2),
(9, 3),
(9, 4),
(9, 5),
(9, 6),
(11, 1),
(11, 2),
(11, 3);

-- --------------------------------------------------------

--
-- Table structure for table `employee_sectors`
--

CREATE TABLE `employee_sectors` (
  `employee_id` int(11) NOT NULL,
  `sector_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `employee_sectors`
--

INSERT INTO `employee_sectors` (`employee_id`, `sector_id`) VALUES
(2, 1),
(5, 1),
(5, 2),
(5, 3),
(5, 4),
(5, 5),
(5, 6),
(9, 3),
(9, 4),
(9, 5);

-- --------------------------------------------------------

--
-- Table structure for table `items`
--

CREATE TABLE `items` (
  `id` int(11) NOT NULL,
  `name` varchar(150) NOT NULL,
  `quantity` int(11) NOT NULL,
  `selling_price` double NOT NULL,
  `purchased_date` date NOT NULL,
  `purchased_price` double NOT NULL,
  `category_id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `stock_limit` int(11) NOT NULL,
  `deleted` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `items`
--

INSERT INTO `items` (`id`, `name`, `quantity`, `selling_price`, `purchased_date`, `purchased_price`, `category_id`, `supplier_id`, `stock_limit`, `deleted`) VALUES
(9, 'PlayStation 5', 10, 750, '2025-12-28', 500, 2, 3, 3, 1),
(12, 'Sony Headsets', 9, 120, '2026-01-01', 100, 4, 1, 2, 0);

-- --------------------------------------------------------

--
-- Table structure for table `permissions`
--

CREATE TABLE `permissions` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `permissions`
--

INSERT INTO `permissions` (`id`, `name`) VALUES
(11, 'ACCESS_STATISTICS_ABOUT_SOLD_AND_PURCHASED_ITEMS'),
(7, 'ADD_ITEMS_TO_STOCK'),
(6, 'ENTER_NEW_ITEM_CATEGORIES'),
(1, 'GENERATE_PRINTABLE_BILL'),
(10, 'GENERATE_TOTAL_COST_INCOME'),
(9, 'MANAGE_EMPLOYEES'),
(8, 'MONITOR_CASHIER_PERFORMANCE'),
(4, 'OVERSEE_SALES_PERFORMANCE'),
(2, 'RECEIVE_ALERTS_IF_ITEM_NOT_EXIST_OR_OUT_OF_STOCK'),
(5, 'SUPPLY_SECTOR_WITH_NEEDED_ITEMS'),
(3, 'VIEW_BILLS_AND_TOTAL_FOR_CURRENT_DAY');

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

CREATE TABLE `roles` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`id`, `name`) VALUES
(1, 'ADMINISTRATOR'),
(2, 'CASHIER'),
(3, 'MANAGER');

-- --------------------------------------------------------

--
-- Table structure for table `sectors`
--

CREATE TABLE `sectors` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sectors`
--

INSERT INTO `sectors` (`id`, `name`) VALUES
(4, 'AUDIO_EQUIPMENT'),
(5, 'CAMERA'),
(1, 'COMPUTERS'),
(6, 'GAMING'),
(3, 'HOME_ENTERTAINMENT'),
(2, 'MOBILE_DEVICES');

-- --------------------------------------------------------

--
-- Table structure for table `suppliers`
--

CREATE TABLE `suppliers` (
  `id` int(11) NOT NULL,
  `name` varchar(150) NOT NULL,
  `address` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `suppliers`
--

INSERT INTO `suppliers` (`id`, `name`, `address`) VALUES
(1, 'TechSupplier Ltd', 'Tirana'),
(2, 'Global Electronics', 'Durres'),
(3, 'Digital World', 'Vlore'),
(4, 'Shpresa AL', 'Unknown Address');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bills`
--
ALTER TABLE `bills`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `bill_number` (`bill_number`),
  ADD KEY `employee_id` (`employee_id`);

--
-- Indexes for table `bill_items`
--
ALTER TABLE `bill_items`
  ADD PRIMARY KEY (`bill_id`,`item_id`),
  ADD KEY `item_id` (`item_id`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `employees`
--
ALTER TABLE `employees`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD KEY `role_id` (`role_id`),
  ADD KEY `sector_id` (`sector_id`);

--
-- Indexes for table `employee_permissions`
--
ALTER TABLE `employee_permissions`
  ADD PRIMARY KEY (`employee_id`,`permission_id`),
  ADD KEY `permission_id` (`permission_id`);

--
-- Indexes for table `employee_sectors`
--
ALTER TABLE `employee_sectors`
  ADD PRIMARY KEY (`employee_id`,`sector_id`),
  ADD KEY `sector` (`sector_id`);

--
-- Indexes for table `items`
--
ALTER TABLE `items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `category_id` (`category_id`),
  ADD KEY `supplier_id` (`supplier_id`);

--
-- Indexes for table `permissions`
--
ALTER TABLE `permissions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `sectors`
--
ALTER TABLE `sectors`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `suppliers`
--
ALTER TABLE `suppliers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bills`
--
ALTER TABLE `bills`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `employees`
--
ALTER TABLE `employees`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `items`
--
ALTER TABLE `items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `permissions`
--
ALTER TABLE `permissions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `roles`
--
ALTER TABLE `roles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `sectors`
--
ALTER TABLE `sectors`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `suppliers`
--
ALTER TABLE `suppliers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bills`
--
ALTER TABLE `bills`
  ADD CONSTRAINT `bills_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`);

--
-- Constraints for table `bill_items`
--
ALTER TABLE `bill_items`
  ADD CONSTRAINT `bill_items_ibfk_1` FOREIGN KEY (`bill_id`) REFERENCES `bills` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `bill_items_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`);

--
-- Constraints for table `employees`
--
ALTER TABLE `employees`
  ADD CONSTRAINT `employees_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  ADD CONSTRAINT `employees_ibfk_2` FOREIGN KEY (`sector_id`) REFERENCES `sectors` (`id`);

--
-- Constraints for table `employee_permissions`
--
ALTER TABLE `employee_permissions`
  ADD CONSTRAINT `employee_permissions_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `employee_permissions_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `employee_sectors`
--
ALTER TABLE `employee_sectors`
  ADD CONSTRAINT `empl` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`),
  ADD CONSTRAINT `sector` FOREIGN KEY (`sector_id`) REFERENCES `sectors` (`id`);

--
-- Constraints for table `items`
--
ALTER TABLE `items`
  ADD CONSTRAINT `items_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  ADD CONSTRAINT `items_ibfk_2` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
