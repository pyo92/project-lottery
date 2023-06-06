-- MariaDB dump 10.19  Distrib 10.11.2-MariaDB, for debian-linux-gnu (aarch64)
--
-- Host: localhost    Database: lottery
-- ------------------------------------------------------
-- Server version	10.11.2-MariaDB-1:10.11.2+maria~ubu2204

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `user_combination`
--

DROP TABLE IF EXISTS `user_combination`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_combination` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `modified_at` datetime(6) NOT NULL,
  `combination_type` varchar(255) DEFAULT NULL,
  `draw_no` bigint(20) DEFAULT NULL,
  `number1` int(11) DEFAULT NULL,
  `number2` int(11) DEFAULT NULL,
  `number3` int(11) DEFAULT NULL,
  `number4` int(11) DEFAULT NULL,
  `number5` int(11) DEFAULT NULL,
  `number6` int(11) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX5vlh9arjyf8mbic2vy0os12y7` (`user_id`,`draw_no`),
  KEY `IDXg598ekw7ungmbcpak3c0ehncw` (`user_id`,`draw_no`,`combination_type`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_combination`
--

LOCK TABLES `user_combination` WRITE;
/*!40000 ALTER TABLE `user_combination` DISABLE KEYS */;
INSERT INTO `user_combination` VALUES
(1,'2023-06-04 21:06:31.345552','2023-06-04 21:06:31.345552','RECOMMEND',1071,1,6,7,15,17,33,NULL,'k_2811950709'),
(2,'2023-06-04 21:06:31.362443','2023-06-04 21:06:31.362443','RECOMMEND',1071,3,16,17,24,27,35,NULL,'k_2811950709'),
(3,'2023-06-04 21:06:31.375672','2023-06-04 21:06:31.375672','RECOMMEND',1071,2,7,16,23,25,43,NULL,'k_2811950709'),
(4,'2023-06-04 21:06:31.380233','2023-06-04 21:06:31.380233','RECOMMEND',1071,15,20,25,32,36,41,NULL,'k_2811950709'),
(5,'2023-06-04 21:06:31.399221','2023-06-04 21:06:31.399221','RECOMMEND',1071,18,31,35,42,43,45,NULL,'k_2811950709'),
(6,'2023-06-05 23:38:23.318456','2023-06-05 23:38:23.318456','RECOMMEND',1071,8,10,15,17,21,33,NULL,'k_2811950709'),
(7,'2023-06-05 23:38:24.285197','2023-06-05 23:38:24.285197','RECOMMEND',1071,3,7,23,30,32,34,NULL,'k_2811950709'),
(8,'2023-06-05 23:38:25.535617','2023-06-05 23:38:25.535617','RECOMMEND',1071,3,9,16,19,29,35,NULL,'k_2811950709'),
(9,'2023-06-05 23:38:26.055639','2023-06-05 23:38:26.055639','RECOMMEND',1071,7,9,13,15,21,41,NULL,'k_2811950709'),
(10,'2023-06-05 23:38:27.045382','2023-06-05 23:38:27.045382','RECOMMEND',1071,1,5,13,28,36,43,NULL,'k_2811950709');
/*!40000 ALTER TABLE `user_combination` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-06-06 15:48:20