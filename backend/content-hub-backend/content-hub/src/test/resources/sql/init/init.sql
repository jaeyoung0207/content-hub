
DELETE FROM content.comment;
DELETE FROM content.wishlist;
DELETE FROM content.content;
DELETE FROM content.user;

-- USER
INSERT INTO content.user(user_id,create_time,update_time,nickname,provider,email,provider_id,status) VALUES (1,TIMESTAMP '2025-11-17 14:22:03.895',TIMESTAMP '2025-12-12 01:08:27.717','달님방긋','NAVER','skwodud8@naver.com','b-hHGk5x0qveo0GBMQ7KVwlypvHg4i0VSJ0E-Ma0AjU','0');
INSERT INTO content.user(user_id,create_time,update_time,nickname,provider,email,provider_id,status) VALUES (2,TIMESTAMP '2025-11-17 14:25:51.371',TIMESTAMP '2025-12-12 01:09:05.535','최재영','KAKAO',NULL,'4362711205','0');

-- CONTENT
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (1,TIMESTAMP '2025-11-17 14:22:08.117',TIMESTAMP '2025-11-17 14:22:08.117','정령왕좌','408355','1201','1','/1KRfupPMh0Gq31ZImuTi3ibSa0q.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (2,TIMESTAMP '2025-11-17 14:22:13.322',TIMESTAMP '2025-11-17 14:22:13.322','왕좌의 게임','1399','1102','2','/zZqpAXxVSBtxV9qPBcscfXBcL2w.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (3,TIMESTAMP '2025-11-17 14:22:16.658',TIMESTAMP '2025-11-17 14:22:16.658','왕좌의 게임: 라스트 워치','591278','1201','3','/6hfmHRaZNKzazyDDLli5CbT6L8H.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (4,TIMESTAMP '2025-11-17 14:22:18.398',TIMESTAMP '2025-11-17 14:22:18.398','바이킹: 왕좌의 게임','369698','1201','3','/5Mfbh6kW4akjjfCZJLwt6Tbfe2G.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (5,TIMESTAMP '2025-11-17 14:22:50.791',TIMESTAMP '2025-11-17 14:22:50.791','드래곤볼 Z','12971','1101','1','/ydf1CeiBLfdxiyNTpskM0802TKl.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (6,TIMESTAMP '2025-11-17 14:22:59.884',TIMESTAMP '2025-11-17 14:22:59.884','드래곤 퀘스트: 타이의 대모험','38324','1101','1','/taqD0yjyXEQ58HGHJVG1sxJBGQO.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (7,TIMESTAMP '2025-11-17 14:23:01.706',TIMESTAMP '2025-11-17 14:23:01.706','코바야시네 메이드래곤','69291','1101','1','/f1yRwsEWO8gehrVKNtZbeuKXKAh.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (8,TIMESTAMP '2025-11-17 14:23:33.622',TIMESTAMP '2025-11-17 14:23:33.622','드래곤볼 GT','12697','1101','1','/aJOlYXjxb5IvnTsO4I1tmFpC7GH.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (9,TIMESTAMP '2025-11-17 14:23:44.188',TIMESTAMP '2025-11-17 14:23:44.188','악마는 프라다를 입는다','350','1201','3','/3tWw50B1xXlCnJ9A7NX4nNzZF4j.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (10,TIMESTAMP '2025-11-17 14:24:16.554',TIMESTAMP '2025-11-17 14:24:16.554','그 비스크 돌은 사랑을 한다','123249','1101','1','/gWPK2RIVJ6i3myf7Xdw8DqlznT8.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (11,TIMESTAMP '2025-11-17 14:24:21.403',TIMESTAMP '2025-11-17 14:24:21.403','스파이 패밀리','120089','1101','1','/lysUnU6V0VfcthDbviuVlIqgHOR.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (12,TIMESTAMP '2025-11-17 14:24:22.906',TIMESTAMP '2025-11-17 14:24:22.906','빈란드 사가','88803','1101','1','/xamCBQePUy9xI42GvtphLuGqd09.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (13,TIMESTAMP '2025-11-17 15:48:00.235',TIMESTAMP '2025-11-17 15:48:00.235','바이킹스','44217','1102','2','/lHe8iwM4Cdm6RSEiara4PN8ZcBd.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (14,TIMESTAMP '2025-11-17 15:48:10.679',TIMESTAMP '2025-11-17 15:48:10.679','전생했더니 슬라임이었던 건에 대하여','82684','1101','1','/eJOy7YWAHgOS3V477sdTsq4v9jp.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (15,TIMESTAMP '2025-11-17 15:48:32.126',TIMESTAMP '2025-11-17 15:48:32.126','스위트홈','96648','1102','2','/mceCXNTny6a5F3rQgShLoyARw4l.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (16,TIMESTAMP '2025-11-18 14:39:32.579',TIMESTAMP '2025-11-18 14:39:32.579','오버로드','64196','1101','1','/q5WJxYTXNNJdEHxFCqd3S8pO4VA.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (17,TIMESTAMP '2025-11-18 14:44:27.688',TIMESTAMP '2025-11-18 14:44:27.688','워킹 데드','1402','1102','2','/rAOjnEFTuNysY7bot8zonhImGMh.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (18,TIMESTAMP '2025-11-18 15:01:05.566',TIMESTAMP '2025-11-18 15:01:05.566','고블린 슬레이어','82591','1101','1','/65XyAN0PrUkkfqvGbFRLjk5x3wZ.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (19,TIMESTAMP '2025-11-18 15:02:02.229',TIMESTAMP '2025-11-18 15:02:02.229','진격의 거인','1429','1101','1','/wg0GsFpeHAFPbbcfsntTQBggWCo.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (20,TIMESTAMP '2025-12-06 20:11:31.654',TIMESTAMP '2025-12-06 20:11:31.654','원피스','37854','1101','1','/oVfucXvhutTpYExG9k06NJqnpT9.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (21,TIMESTAMP '2025-12-06 20:11:52.770',TIMESTAMP '2025-12-06 20:11:52.770','귀멸의 칼날','85937','1101','1','/3GQKYh6Trm8pxd2AypovoYQf4Ay.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (22,TIMESTAMP '2025-12-08 00:26:08.533',TIMESTAMP '2025-12-08 00:26:08.533','어벤져스','24428','1201','3','/9BBTo63ANSmhC4e6r62OJFuK2GL.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (23,TIMESTAMP '2025-12-18 00:08:28.055',TIMESTAMP '2025-12-18 00:08:28.055','Kimetsu no Yaiba','87216','2101','21','https://s4.anilist.co/file/anilistcdn/media/manga/cover/medium/bx87216-c9bSNVD10UuD.png');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (24,TIMESTAMP '2025-12-18 00:54:55.473',TIMESTAMP '2025-12-18 00:54:55.473','어벤져스: 인피니티 워','299536','1201','3','/mDfJG3LC3Dqb67AZ52x3Z0jU0uB.jpg');
INSERT INTO content.content(content_id,create_time,update_time,title,api_id,content_media_type,display_media_type,thumbnail_image_url) VALUES (25,TIMESTAMP '2025-12-18 00:59:15.778',TIMESTAMP '2025-12-18 00:59:15.778','Yuu☆Yuu☆Hakusho','30053','2101','21','https://s4.anilist.co/file/anilistcdn/media/manga/cover/medium/bx30053-wCR6xyGzeUYo.png');

-- COMMENT
INSERT INTO content.comment(comment_id,star_rating,bad,content_id,create_time,good,update_time,user_id,comment) VALUES (1,5.0,NULL,8,TIMESTAMP '2025-11-17 14:24:02.902',NULL,TIMESTAMP '2025-11-17 14:24:02.902',1,'최고의 애니');
INSERT INTO content.comment(comment_id,star_rating,bad,content_id,create_time,good,update_time,user_id,comment) VALUES (2,4.5,NULL,14,TIMESTAMP '2025-11-18 14:45:27.221',NULL,TIMESTAMP '2025-11-18 14:45:27.221',1,'재밌음');
INSERT INTO content.comment(comment_id,star_rating,bad,content_id,create_time,good,update_time,user_id,comment) VALUES (3,4.0,NULL,20,TIMESTAMP '2025-12-06 20:11:31.674',NULL,TIMESTAMP '2025-12-06 20:11:31.674',1,'재밌나');
INSERT INTO content.comment(comment_id,star_rating,bad,content_id,create_time,good,update_time,user_id,comment) VALUES (4,5.0,NULL,21,TIMESTAMP '2025-12-06 20:11:52.774',NULL,TIMESTAMP '2025-12-06 20:11:52.774',1,'매우 재밌음');
INSERT INTO content.comment(comment_id,star_rating,bad,content_id,create_time,good,update_time,user_id,comment) VALUES (5,5.0,NULL,19,TIMESTAMP '2025-12-06 20:12:15.885',NULL,TIMESTAMP '2025-12-06 20:12:15.885',1,'최고의 애니');
INSERT INTO content.comment(comment_id,star_rating,bad,content_id,create_time,good,update_time,user_id,comment) VALUES (6,4.5,NULL,22,TIMESTAMP '2025-12-08 00:26:08.571',NULL,TIMESTAMP '2025-12-08 00:26:08.571',1,'재밌음');
INSERT INTO content.comment(comment_id,star_rating,bad,content_id,create_time,good,update_time,user_id,comment) VALUES (7,4.0,NULL,2,TIMESTAMP '2025-12-08 00:26:24.210',NULL,TIMESTAMP '2025-12-08 00:26:24.210',1,'용두사미');

-- WISHLIST
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (1,1,TIMESTAMP '2025-11-17 14:22:08.139',TIMESTAMP '2025-11-17 14:22:08.139',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (2,2,TIMESTAMP '2025-11-17 14:22:13.336',TIMESTAMP '2025-11-17 14:22:13.336',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (3,3,TIMESTAMP '2025-11-17 14:22:16.672',TIMESTAMP '2025-11-17 14:22:16.672',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (4,4,TIMESTAMP '2025-11-17 14:22:18.409',TIMESTAMP '2025-11-17 14:22:18.409',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (5,5,TIMESTAMP '2025-11-17 14:22:50.820',TIMESTAMP '2025-11-17 14:22:50.820',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (6,6,TIMESTAMP '2025-11-17 14:22:59.919',TIMESTAMP '2025-11-17 14:22:59.919',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (7,7,TIMESTAMP '2025-11-17 14:23:01.729',TIMESTAMP '2025-11-17 14:23:01.729',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (8,8,TIMESTAMP '2025-11-17 14:23:33.632',TIMESTAMP '2025-11-17 14:23:33.632',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (9,9,TIMESTAMP '2025-11-17 14:23:44.201',TIMESTAMP '2025-11-17 14:23:44.201',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (10,10,TIMESTAMP '2025-11-17 14:24:16.564',TIMESTAMP '2025-11-17 14:24:16.564',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (11,11,TIMESTAMP '2025-11-17 14:24:21.411',TIMESTAMP '2025-11-17 14:24:21.411',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (12,12,TIMESTAMP '2025-11-17 14:24:22.918',TIMESTAMP '2025-11-17 14:24:22.918',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (13,13,TIMESTAMP '2025-11-17 15:48:00.290',TIMESTAMP '2025-11-17 15:48:00.290',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (14,14,TIMESTAMP '2025-11-17 15:48:10.691',TIMESTAMP '2025-11-17 15:48:10.691',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (15,15,TIMESTAMP '2025-11-17 15:48:32.143',TIMESTAMP '2025-11-17 15:48:32.143',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (16,16,TIMESTAMP '2025-11-18 14:39:32.664',TIMESTAMP '2025-11-18 14:39:32.664',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (17,17,TIMESTAMP '2025-11-18 14:44:27.701',TIMESTAMP '2025-11-18 14:44:27.701',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (18,18,TIMESTAMP '2025-11-18 15:01:05.640',TIMESTAMP '2025-11-18 15:01:05.640',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (19,19,TIMESTAMP '2025-11-18 15:02:02.243',TIMESTAMP '2025-11-18 15:02:02.243',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (20,22,TIMESTAMP '2025-12-08 00:26:42.519',TIMESTAMP '2025-12-08 00:26:42.519',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (21,23,TIMESTAMP '2025-12-18 00:08:28.080',TIMESTAMP '2025-12-18 00:08:28.080',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (22,24,TIMESTAMP '2025-12-18 00:54:55.491',TIMESTAMP '2025-12-18 00:54:55.491',1);
INSERT INTO content.wishlist(wishlist_id,content_id,create_time,update_time,user_id) VALUES (23,25,TIMESTAMP '2025-12-18 00:59:15.789',TIMESTAMP '2025-12-18 00:59:15.789',1);
