

drop table  IF EXISTS  fact_imsi_sig_day;
create table fact_imsi_sig_day AS select result_type_id, date_id, imsi_count from fact_imsi_sig where period_type =0;

drop table  IF EXISTS  fact_imsi_sig_week;
create table fact_imsi_sig_week AS select result_type_id, date_id, imsi_count from fact_imsi_sig where period_type =1;

drop table  IF EXISTS  fact_imsi_sig_month;
create table fact_imsi_sig_month AS select result_type_id, date_id, imsi_count from fact_imsi_sig where period_type =2;

drop table  IF EXISTS  fact_imsi_sig_year;
create table fact_imsi_sig_year AS select result_type_id, date_id, imsi_count from fact_imsi_sig where period_type =3;


ALTER TABLE fact_imsi_sig_day add FOREIGN KEY (`date_id`) REFERENCES `dim_date` (`id`);
ALTER TABLE fact_imsi_sig_day add  FOREIGN KEY (`result_type_id`) REFERENCES `dim_result_type_sig` (`id`);

ALTER TABLE fact_imsi_sig_week add FOREIGN KEY (`date_id`) REFERENCES `dim_date` (`id`);
ALTER TABLE fact_imsi_sig_week add  FOREIGN KEY (`result_type_id`) REFERENCES `dim_result_type_sig` (`id`);

ALTER TABLE fact_imsi_sig_month add FOREIGN KEY (`date_id`) REFERENCES `dim_date` (`id`);
ALTER TABLE fact_imsi_sig_month add  FOREIGN KEY (`result_type_id`) REFERENCES `dim_result_type_sig` (`id`);

ALTER TABLE fact_imsi_sig_year add FOREIGN KEY (`date_id`) REFERENCES `dim_date` (`id`);
ALTER TABLE fact_imsi_sig_year add  FOREIGN KEY (`result_type_id`) REFERENCES `dim_result_type_sig` (`id`);

















