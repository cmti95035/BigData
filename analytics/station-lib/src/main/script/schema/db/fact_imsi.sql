
DROP TABLE IF EXISTS fact_imsi_sig;

CREATE TABLE `fact_imsi_sig` (
  `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `date_id` int(11) NOT NULL,
  `imsi_count` int(11) DEFAULT NULL,
  `period_type` int(11) NOT NULL,
  `result_type_id` int(11) NOT NULL,  
  FOREIGN KEY (`date_id`) REFERENCES `dim_date` (`id`),
  FOREIGN KEY (`result_type_id`) REFERENCES `dim_result_type_sig` (`id`),
  unique key uk_fact_imsi_sig (date_id, period_type, result_type_id)
);
 