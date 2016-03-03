DROP DATABASE IF EXISTS Playing;
CREATE DATABASE Playing;
USE Playing;

CREATE TABLE Users(
	user_id	INT(10) NOT NULL AUTO_INCREMENT,
	username    VARCHAR(50) NOT NULL,
	password	VARCHAR(50) NOT NULL,
	state	TINYINT(5) NOT NULL,
	sex TINYINT(5),
	age	INT(10),
	phone	VARCHAR(20),
	head_pic	VARCHAR(50),
	reg_time	TIMESTAMP	DEFAULT CURRENT_TIMESTAMP,
	mood	VARCHAR(255),
	UNIQUE(username),
	primary key (user_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Shows(
	show_id	INT(10)	NOT NULL AUTO_INCREMENT,
	user_id	INT(10) NOT NULL,
	show_time	TIMESTAMP	DEFAULT CURRENT_TIMESTAMP,
	content    VARCHAR(255),
	images	VARCHAR(255),
	audios	VARCHAR(255),
	is_anonymous  INT(2)  NOT NULL DEFAULT 0,
	primary key (show_id),
	foreign key(user_id) references Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Locations(
	location_id	INT(10) NOT NULL AUTO_INCREMENT,
    address VARCHAR(100) NOT NULL,
    longitude   DOUBLE(10,6) NOT NULL,
    latitude    DOUBLE(10,6) NOT NULL,
	description TEXT,
	UNIQUE(address),
	primary key (location_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE WhereUpload(
    show_id	INT(10),
    location_id	INT(10),
	primary key (show_id, location_id),
	foreign key(show_id) references Shows(show_id) ON DELETE CASCADE ON UPDATE CASCADE,
	foreign key(location_id) references Locations(location_id) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE Comments(
	comment_id	INT(10) NOT NULL AUTO_INCREMENT,
	comment_time	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    content VARCHAR(255),
	is_anonymous  INT(1)  NOT NULL DEFAULT 0,
	primary key (comment_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE CommentShows(
    from_user_id	INT(10),
    to_show_id	INT(10),
    comment_id	INT(10),
	primary key (from_user_id, to_show_id, comment_id),
	foreign key(from_user_id) references Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
	foreign key(to_show_id) references Shows(show_id) ON DELETE CASCADE ON UPDATE CASCADE,
	foreign key(comment_id) references Comments(comment_id) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE ReplyComments(
    from_user_id	INT(10),
    to_comment_id	INT(10),
    comment_id	INT(10),
	primary key (from_user_id, to_comment_id, comment_id),
	foreign key(from_user_id) references Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
	foreign key(to_comment_id) references Comments(comment_id) ON DELETE CASCADE ON UPDATE CASCADE,
	foreign key(comment_id) references Comments(comment_id) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE LikeShows(
    from_user_id	INT(10),
    to_show_id	INT(10),
    like_time	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	primary key (from_user_id, to_show_id),
	foreign key(from_user_id) references Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
	foreign key(to_show_id) references Shows(show_id) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE LikeComments(
    from_user_id	INT(10),
    to_comment_id	INT(10),
    like_time	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	primary key (from_user_id, to_comment_id),
	foreign key(from_user_id) references Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
	foreign key(to_comment_id) references Comments(comment_id) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Friends(
    my_id	INT(10),
    friend_id	INT(10),
    make_time	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	primary key (my_id, friend_id),
	foreign key(my_id) references Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
	foreign key(friend_id) references Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE Messages(
    from_user_id	INT(10),
    to_user_id	INT(10),
    send_time	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    message	VARCHAR(255),
	primary key (from_user_id, to_user_id),
	foreign key(from_user_id) references Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
	foreign key(to_user_id) references Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;










