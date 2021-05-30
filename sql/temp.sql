DROP TABLE IF EXISTS `temp_user`;
CREATE TABLE `temp_user`  (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_code` bigint(20) NULL DEFAULT NULL,
  `user_name` varchar(255)  NULL DEFAULT NULL,
  `user_password` varchar(255)  NULL DEFAULT NULL,
  `user_desc` varchar(255)  NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB  ROW_FORMAT = Compact;