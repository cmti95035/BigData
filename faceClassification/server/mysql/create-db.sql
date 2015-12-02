drop user 'faceClassification'@'localhost';
create user 'faceClassification'@'localhost' identified by 'some_pass';
grant all privileges on *.* to 'faceClassification'@'localhost' with grant option;
set password for 'faceClassification'@'localhost' = password('faceClassification');
