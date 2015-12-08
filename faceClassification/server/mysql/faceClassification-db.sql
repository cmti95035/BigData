CREATE DATABASE IF NOT EXISTS `fClassification_schema`;
USE `fClassification_schema`;

DROP TABLE IF EXISTS `profiles`;

CREATE TABLE `profiles` (
  `profileId` int(8) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `phone` varchar(45) NOT NULL,
  `title` varchar(45) DEFAULT NULL,
  `lastaccesstime` bigint NOT NULL,
  PRIMARY KEY (`profileId`)
) AUTO_INCREMENT=10001, ENGINE=InnoDB DEFAULT CHARSET=utf8;

