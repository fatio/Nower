USE Playing;

insert into Users(username, password, state, sex, age, phone, head_pic, mood)VALUES
	('user1', '7C6A180B36896A0A8C02787EEAFB0E4C', '0', '0', '21', '11111111', 'head1.jpg', 'mood1'),
	('user2', '6CB75F652A9B52798EB6CF2201057C73', '1', '1', '22', '22222222', 'head2.png', 'mood2'),
	('user3', '819B0643D6B89DC9B579FDFC9094F28E', '0', '1', '23', '33333333', 'head3.png', 'mood3'),
	('user4', '34CC93ECE0BA9E3F6F235D4AF979B16C', '1', '0', '24', '44444444', 'head4.jpg', 'mood4'),
	('user5', 'DB0EDD04AAAC4506F7EDAB03AC855D56', '2', '2', '25', '55555555', 'head5.jpg', 'mood5');


insert into Shows(user_id, content, images, audios, is_anonymous) VALUES
	('1', 'content1', 'show1.jpg', 'audio1.wav', '0'),
	('2', 'content2', 'show2.jpg', 'audio2.wav', '1'),
	('2', 'content3', 'show3.jpg', 'audio3.wav', '0'),
	('4', 'content4', 'show4.jpg', 'audio4.wav', '1'),
	('2', 'content5', 'show5.jpg', 'audio5.wav', '0');


INSERT INTO Locations (address, longitude, latitude, description)VALUES
	('广东', '100', '200', '描述1'),
	('珠海', '200', '200', '描述2'),
	('北京', '300', '300', '描述3');
	
insert into WhereUpload(show_id, location_id) VALUES
	(1, 1),(2,2), (3,3), (4,2), (5,1);

insert into Comments(content, is_anonymous) VALUES
	('comment12', 0), ('comment23', 1), ('comment21', 1), ('comment33', 0);

insert into CommentShows(from_user_id, to_show_id, comment_id)VALUES
	(1, 2, 1), (2, 3, 2);

insert into ReplyComments(from_user_id, to_comment_id, comment_id) VALUES
	(2, 1, 3), (3, 3, 4);

insert into LikeShows(from_user_id, to_show_id)VALUES
	(1, 2), (1, 3), (1, 4), (1, 5),(3, 2), (4, 2), (5, 2);
	
insert into LikeComments(from_user_id, to_comment_id)VALUES
	(1, 1), (1, 2), (1, 3), (1, 4), (2, 1), (3, 1), (4, 1);

insert into Friends(my_id, friend_id) VALUES
	(1, 2), (2, 3), (1, 3), (1, 4), (1, 5);

insert into Messages(from_user_id, to_user_id, message)VALUES
	(1, 2, 'msg12'), (1, 3, 'msg13'), (2, 1, 'msg21'), (3, 4, 'msg34');

	
	
	