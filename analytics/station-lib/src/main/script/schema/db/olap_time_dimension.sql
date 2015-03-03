
# DROP DATABASE IF EXISTS holap;
# create DATABASE holap;

use holap;

# based on http://www.dwhworld.com/2010/08/date-dimension-sql-scripts-mysql/

#Small-numbers table
DROP TABLE IF EXISTS numbers_small;
CREATE TABLE numbers_small (number INT);
INSERT INTO numbers_small VALUES (0),(1),(2),(3),(4),(5),(6),(7),(8),(9);

#Main-numbers table
DROP TABLE IF EXISTS numbers;
CREATE TABLE numbers (number BIGINT);
INSERT INTO numbers
SELECT thousands.number * 1000 + hundreds.number * 100 + tens.number * 10 + ones.number
FROM numbers_small thousands, numbers_small hundreds, numbers_small tens, numbers_small ones
LIMIT 1000000;

#Create Date Dimension table
DROP TABLE IF EXISTS dim_date;
CREATE TABLE dim_date (
id          INT PRIMARY KEY,
year             INT,
quarter          INT,
month             INT,
month_name        CHAR(10),
week              INT,
day               INT,
day_of_week      INT,
day_of_week_name  CHAR(10),
date             DATE NOT NULL,
weekend          CHAR(10) NOT NULL DEFAULT "Weekday"
);

#First populate with ids and Date
INSERT INTO dim_date (id, date)
SELECT number, DATE_ADD( '2013-01-01', INTERVAL number DAY )
FROM numbers
WHERE DATE_ADD( '2013-01-01', INTERVAL number DAY ) BETWEEN '2013-01-01' AND '2016-12-31'
ORDER BY number;

#Change year start and end to match your needs. The above sql creates records for year 2010.
#Update other columns based on the date.
UPDATE dim_date SET
id         = DATE_FORMAT( date, "%Y%m%d" ),
day_of_week     = DAYOFWEEK(date),
day_of_week_name= DATE_FORMAT( date, "%W" ),
day             = DATE_FORMAT( date, "%d" ),
weekend         = IF( DATE_FORMAT( date, "%W" ) IN ('Saturday','Sunday'), 'Weekend', 'Weekday'),
week            = DATE_FORMAT( date, "%V" ),
month_name      = DATE_FORMAT( date, "%M"),
month           = DATE_FORMAT( date, "%m"),
quarter         = QUARTER(date),
year            = DATE_FORMAT( date, "%Y" );


DROP TABLE IF EXISTS numbers_small;
DROP TABLE IF EXISTS numbers;