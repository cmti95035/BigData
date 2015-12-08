drop user 'fClassification'@'localhost';
create user 'fClassification'@'localhost' identified by 'some_pass';
grant all privileges on *.* to 'fClassification'@'localhost' with grant option;
set password for 'fClassification'@'localhost' = password('fClassification');
