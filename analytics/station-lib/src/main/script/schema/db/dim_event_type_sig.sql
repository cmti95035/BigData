
DROP TABLE IF EXISTS dim_event_type_sig;

CREATE TABLE dim_event_type_sig (
   id INT NOT NULL PRIMARY KEY,
   name VARCHAR(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=gb2312;


INSERT INTO dim_event_type_sig(id, name) VALUES (0, 'Attach');

INSERT INTO dim_event_type_sig(id, name) VALUES (1, 'PDP激活');

INSERT INTO dim_event_type_sig(id, name) VALUES (2, '路由更新');
INSERT INTO dim_event_type_sig(id, name) VALUES (3, 'PDP去激活');
INSERT INTO dim_event_type_sig(id, name) VALUES (4, 'Detach');

