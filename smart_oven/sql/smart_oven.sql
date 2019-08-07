/*
 Navicat MySQL Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80015
 Source Host           : localhost
 Source Database       : smart_oven

 Target Server Type    : MySQL
 Target Server Version : 80015
 File Encoding         : utf-8

 Date: 08/08/2019 00:00:25 AM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `code_string`
-- ----------------------------
DROP TABLE IF EXISTS `code_string`;
CREATE TABLE `code_string` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mobile_num` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `code_string` varchar(255) DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `update_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
--  Table structure for `max_device_num`
-- ----------------------------
DROP TABLE IF EXISTS `max_device_num`;
CREATE TABLE `max_device_num` (
  `max_device_num` int(11) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
--  Table structure for `mobile_detail_info`
-- ----------------------------
DROP TABLE IF EXISTS `mobile_detail_info`;
CREATE TABLE `mobile_detail_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mobile_id` varchar(20) DEFAULT NULL COMMENT '手机APPID ,使用电话号码',
  `registration_id` varchar(100) DEFAULT NULL COMMENT '极光推送的ID',
  `create_time` bigint(20) DEFAULT NULL,
  `update_time` bigint(20) DEFAULT NULL,
  `group_id` varchar(255) DEFAULT NULL COMMENT '保留字段-属于哪个企业用户',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
--  Table structure for `mobile_user`
-- ----------------------------
DROP TABLE IF EXISTS `mobile_user`;
CREATE TABLE `mobile_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
--  Table structure for `oven_detail_info`
-- ----------------------------
DROP TABLE IF EXISTS `oven_detail_info`;
CREATE TABLE `oven_detail_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `oven_id` varchar(100) DEFAULT NULL COMMENT '烤箱设备标识',
  `registration_id` varchar(100) DEFAULT NULL COMMENT '极光推送的注册ID',
  `oven_name` varchar(50) DEFAULT NULL COMMENT '设备名称',
  `create_time` bigint(20) DEFAULT NULL COMMENT '记录创建时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  `group_id` varchar(255) DEFAULT NULL COMMENT '保留字段  -属于哪个用户',
  `oven_status` smallint(6) DEFAULT '0' COMMENT '烤箱状态  1-正在工作   0-表示闲置',
  `oven_type` smallint(6) DEFAULT '0' COMMENT '设备类型  0-烤箱类型  1-微波炉类型 ',
  `oven_online` smallint(6) DEFAULT '0' COMMENT '设置在线状态  0-在线  1-离线',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
--  Table structure for `oven_mobile_relation`
-- ----------------------------
DROP TABLE IF EXISTS `oven_mobile_relation`;
CREATE TABLE `oven_mobile_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `oven_id` varchar(100) NOT NULL COMMENT '烤箱唯一ID',
  `mobile_id` varchar(20) NOT NULL COMMENT '手机端ID',
  `update_date` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
--  Table structure for `oven_status`
-- ----------------------------
DROP TABLE IF EXISTS `oven_status`;
CREATE TABLE `oven_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `oven_id` varchar(255) DEFAULT NULL COMMENT '烤箱标识',
  `update_time` bigint(20) DEFAULT NULL COMMENT '用于检测烤箱是否在线，记录上次心跳的时间',
  `is_send` smallint(6) DEFAULT '0' COMMENT '是否已经把状态推送到手机app 0-未发送 1-已发送',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
--  Table structure for `task_info`
-- ----------------------------
DROP TABLE IF EXISTS `task_info`;
CREATE TABLE `task_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_status` smallint(6) DEFAULT NULL COMMENT '任务状态',
  `task_id` varchar(100) DEFAULT NULL COMMENT '任务id',
  `create_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
