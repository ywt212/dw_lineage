/*
 Navicat MySQL Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 50718
 Source Host           : localhost:3306
 Source Schema         : meta_data

 Target Server Type    : MySQL
 Target Server Version : 50718
 File Encoding         : 65001

 Date: 25/06/2017 00:50:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for r_data
-- ----------------------------
DROP TABLE IF EXISTS `r_data`;
CREATE TABLE `r_data` (
  `data_id` varchar(0) DEFAULT '',
  `data_name` varchar(0) DEFAULT NULL,
  `datastorage_name` varchar(0) DEFAULT NULL,
  `is_effective` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for r_data_column
-- ----------------------------
DROP TABLE IF EXISTS `r_data_column`;
CREATE TABLE `r_data_column` (
  `column_id` varchar(0) DEFAULT NULL,
  `column_name` varchar(0) DEFAULT NULL,
  `data_id` varchar(0) DEFAULT NULL,
  `column_position` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
