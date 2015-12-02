CREATE DATABASE IF NOT EXISTS `faceClassification_schema`;
USE `faceClassification_schema`;

DROP TABLE IF EXISTS `profiles`;

CREATE TABLE `profiles` (
  `profileId` int(8) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `phone` varchar(45) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `lastaccesstime` bigint NOT NULL,
  PRIMARY KEY (`profileId`)
) AUTO_INCREMENT=10001, ENGINE=InnoDB DEFAULT CHARSET=utf8;

