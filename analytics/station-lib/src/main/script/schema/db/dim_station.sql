DROP TABLE IF EXISTS dim_station;

CREATE TABLE `dim_station` (
  `id` int(11) NOT NULL,
  `last_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `BSC` varchar(20) DEFAULT NULL,
  `CGI` varchar(20) DEFAULT NULL,
  `longitude` double NOT NULL,
  `latitude` double NOT NULL,
  `type` varchar(20) DEFAULT NULL,
  `ccch` int(11) DEFAULT NULL,
  `lac` int(11) DEFAULT NULL,
  `ci` int(11) DEFAULT NULL,
  `angle` double DEFAULT NULL,
  `roomstr` varchar(20) DEFAULT NULL,
  `grid` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312;
