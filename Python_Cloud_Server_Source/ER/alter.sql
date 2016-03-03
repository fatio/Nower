use Playing;
alter table Shows modify column images text;

use Playing;
drop table Messages;

use Playing;
CREATE TABLE Messages(
	message_id	INT(10) NOT NULL AUTO_INCREMENT,
	msg_type	int not null,
	has_read	int not null default 0,
    from_user_id	INT(10),
    to_user_id	INT(10),
    send_time	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    message		VARCHAR(255),
    msg_obj		varchar(255),
	primary key (message_id),
	foreign key(from_user_id) references Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
	foreign key(to_user_id) references Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


alter table Messages add column msg_obj varchar not;
alter table Messages add column msg_type int not null;
alter table Messages add column message_id INT(10) NOT NULL AUTO_INCREMENT;

alter table Messages add column has_read int not null default 0;



use Playing;
ALTER TABLE Users ADD INDEX index_username (username);
ALTER TABLE Locations ADD INDEX index_address (address);
show index from Users;
show index from Locations;



0 system msg
1 like msg
2 comment msg