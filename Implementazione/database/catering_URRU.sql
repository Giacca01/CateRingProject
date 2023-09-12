-- MySQL dump 10.13  Distrib 5.7.26, for osx10.10 (x86_64)
--
-- Host: 127.0.0.1    Database: catering
-- ------------------------------------------------------
-- Server version	5.7.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Events`
--

DROP TABLE IF EXISTS `Events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Events` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `state` varchar(128) DEFAULT NULL,
  `duration` int(11),
  `servicesNumber` int(11),
  `documentation` varchar(128) DEFAULT NULL,
  `description` varchar(128) DEFAULT NULL,
  `initial_date` varchar(128) DEFAULT NULL,
  `final_notes` varchar(128) DEFAULT NULL,
  `organizer_id` int NOT NULL,
  `chef_id` int not null,
  `client_id` int not null,
  `num_occurences` int default null,
  `dateEndRec` varchar(128) DEFAULT NULL,
  `period` int default null,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Events`
--

LOCK TABLES `Events` WRITE;
/*!40000 ALTER TABLE `Events` DISABLE KEYS */;
INSERT INTO `Events` VALUES (1,'state', 1, 0, 'documentation', 'evento1', '2020-09-25', 'Note Evento 1', 3, 2, 1, null, null, null),(2,'state', 1, 0, 'documentation2', 'evento2', '2020-09-29', 'Note Evento 2', 7, 6, 2, null, null, null),(3,'state', 1, 0, 'documentation3', 'evento3', '2021-10-04', 'Note Evento 3', 3, 10, 1, null, null, null);
/*!40000 ALTER TABLE `Events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Clients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Clients` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(128) NOT NULL DEFAULT '',
  `cognome` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Users`
--

LOCK TABLES `Clients` WRITE;
/*!40000 ALTER TABLE `Clients` DISABLE KEYS */;
INSERT INTO `Clients` VALUES (1,'Michael', 'Urru'),(2,'Federico', 'Giacardi');
/*!40000 ALTER TABLE `Clients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MenuFeatures`
--

DROP TABLE IF EXISTS `MenuFeatures`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MenuFeatures` (
  `menu_id` int(11) NOT NULL,
  `name` varchar(128) NOT NULL DEFAULT '',
  `value` boolean DEFAULT false
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MenuFeatures`
--

LOCK TABLES `MenuFeatures` WRITE;
/*!40000 ALTER TABLE `MenuFeatures` DISABLE KEYS */;
INSERT INTO `MenuFeatures` VALUES (1,'Richiede cuoco',false),(2,'Buffet',false),(3,'Richiede cucina',false),(4,'Finger food',false),(5,'Piatti caldi',false),(6,'Richiede cuoco',false),(7,'Buffet',false),(8,'Richiede cucina',false),(9,'Finger food',false),(10,'Piatti caldi',false),(11,'Richiede cuoco',false),(12,'Buffet',false),(13,'Richiede cucina',false),(14,'Finger food',false),(15,'Piatti caldi',false);
/*!40000 ALTER TABLE `MenuFeatures` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MenuItems`
--

DROP TABLE IF EXISTS `MenuItems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MenuItems` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `menu_id` int(11) NOT NULL,
  `section_id` int(11) DEFAULT NULL,
  `description` tinytext,
  `recipe_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MenuItems`
--

LOCK TABLES `MenuItems` WRITE;
/*!40000 ALTER TABLE `MenuItems` DISABLE KEYS */;
INSERT INTO `MenuItems` VALUES (1,1, 1, 'Croissant vuoti',9),(2,2,2,'Croissant alla marmellata',9),(3,3,3,'Pane al cioccolato mignon',10),(4,1, 4,'Panini al latte con prosciutto crudo',12),(5,2, 5,'Panini al latte con prosciutto cotto',12),(6,3,1,'Panini al latte con formaggio spalmabile alle erbe',12),(7,3, 2,'Girelle all\'uvetta mignon',11),(8,2,3,'Biscotti',13),(9,3,4,'Lingue di gatto',14),(10,5, 1,'Bigné alla crema',15);
/*!40000 ALTER TABLE `MenuItems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MenuSections`
--

DROP TABLE IF EXISTS `MenuSections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MenuSections` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `menu_id` int(11) NOT NULL,
  `name` tinytext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MenuSections`
--

LOCK TABLES `MenuSections` WRITE;
/*!40000 ALTER TABLE `MenuSections` DISABLE KEYS */;
INSERT INTO `MenuSections` VALUES (1, 1,'Antipasti'),(2, 2,'Primi'),(3, 3,'Secondi'),(4, 1,'Dessert'),(5, 2,'Antipasti');
/*!40000 ALTER TABLE `MenuSections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Menus`
--

DROP TABLE IF EXISTS `Menus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Menus` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` tinytext,
  `owner_id` int(11) DEFAULT NULL,
  `inUse` boolean not null,
  `published` boolean not null default false,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Menus`
--

LOCK TABLES `Menus` WRITE;
/*!40000 ALTER TABLE `Menus` DISABLE KEYS */;
INSERT INTO `Menus` VALUES (1, 'Coffee break mattutino',2,true, true),(2, 'Coffee break pomeridiano',1,true, true),(3, 'Cena di compleanno pesce',2,true, true);
/*!40000 ALTER TABLE `Menus` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `KitchenTasks`
--

DROP TABLE IF EXISTS `KitchenTasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `KitchenTasks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` tinytext,
  `type` varchar(1) NOT NULL, /* r recipe - p preparation */
  `recipe_id` int,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `KitchenTasks`
--

LOCK TABLES `KitchenTasks` WRITE;
/*!40000 ALTER TABLE `KitchenTasks` DISABLE KEYS */;
INSERT INTO `KitchenTasks` VALUES (1,'Vitello tonnato', 'r', null),(2,'Carpaccio di spada', 'r', null),(3,'Alici marinate', 'r', null),(4,'Insalata di riso', 'r', null),(5,'Penne al sugo di baccalà', 'r', null),(6,'Pappa al pomodoro', 'r', null),(7,'Hamburger con bacon e cipolla caramellata', 'r', null),(8,'Salmone al forno', 'r', null),(9,'Croissant', 'r', null),(10,'Pane al cioccolato', 'r', null),(11,'Girelle all\'uvetta', 'r', null),(12,'Panini al latte', 'r', null),(13,'Biscotti di pasta frolla', 'r', null),(14,'Lingue di gatto', 'r', null),(15,'Bigné farciti', 'r', null);
/*!40000 ALTER TABLE `KitchenTasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Services`
--

DROP TABLE IF EXISTS `Services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Services` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `event_id` int(11) NOT NULL,
  `type` varchar(128) DEFAULT NULL,
  `menu_id` int not null,
  `service_date` varchar(128) DEFAULT NULL,
  `location` varchar(128) DEFAULT NULL,
  `attendance` int(11) DEFAULT NULL,
  `approved` tinyint(1) not null,
  `timeSlot` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Services`
--

LOCK TABLES `Services` WRITE;
/*!40000 ALTER TABLE `Services` DISABLE KEYS */;
INSERT INTO `Services` VALUES (1,1,'Cena',1,'2020-08-13','Torino',20, 0, '18:00-23:00'),(2,1,'Pranzo',2,'2020-08-14','Torino',20, 0, '10:00-15:00'),(3,2,'Coffee break',3,'2020-08-15','Torino',20, 0, '09:00-12:00'),(4,1,'Coffee break pomeriggio',2,'2020-09-25','Torino',20, 0, '15:00-18:00'),(5,3,'Cena sociale',3,'2020-09-25','Torino',20, 0, '18:00-23:00'),(6,2,'Pranzo giorno 1',2,'2020-10-02','Torino',20, 0, '10:00-15:00');
/*!40000 ALTER TABLE `Services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(128) NOT NULL DEFAULT '',
  `role` char NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Users`
--

LOCK TABLES `Users` WRITE;
/*!40000 ALTER TABLE `Users` DISABLE KEYS */;
INSERT INTO `Users` VALUES (1,'Carlin', 'c'),(2,'Lidia', 'h'),(3,'Tony', 'o'),(4,'Marinella', 's'),(5,'Guido', 'c'),(6,'Antonietta','h'),(7,'Paola','o'),(8,'Silvia','s'),(9,'Marco','c'),(10,'Piergiorgio','h');
/*!40000 ALTER TABLE `Users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Assignments`
--
DROP TABLE IF EXISTS `Assignments`;
CREATE TABLE `Assignments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timeEstimate` varchar(128) DEFAULT '',
  `quantity` varchar(128) DEFAULT '',
  `task_id` int,
  `shift_id` int,
  `cook_id` int,
  `service_id` int not null,
  `toBePrepared` boolean not null,
  `continuationOf` int,
  `position` int not null,
  PRIMARY KEY (`id`)
);

--
-- Table structure for table `Shift`
--
DROP TABLE IF EXISTS `Shift`;
CREATE TABLE `Shift` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `full` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
);

--
-- Dumping data for table `Shift`
--

LOCK TABLES `Shift` WRITE;
/*!40000 ALTER TABLE `Shift` DISABLE KEYS */;
INSERT INTO `Shift` VALUES (1, 0),(2, 0),(3, 0),(4, 0);
/*!40000 ALTER TABLE `Shift` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Availability`
--
DROP TABLE IF EXISTS `Availability`;
CREATE TABLE `Availability` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reserved` boolean NOT NULL,
  `role` varchar(100) NOT NULL,
  `employee_id` int not null,
  `service_id` int not null,
  `shift_id` int not null,
  `reserved_by` int not null,
  PRIMARY KEY (`id`)
);

--
-- Dumping data for table `Shift`
--

LOCK TABLES `Availability` WRITE;
/*!40000 ALTER TABLE `Availability` DISABLE KEYS */;
INSERT INTO `Availability` VALUES (1, false, '', 1, 1, 1, 3),(2, false, '', 5, 2, 2, 7);
/*!40000 ALTER TABLE `Availability` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `VariationProposal`
--
DROP TABLE IF EXISTS `VariationProposal`;
CREATE TABLE `VariationProposal` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `comment` varchar(128) NOT NULL DEFAULT '',
  `accepted` tinyint(1) NOT NULL,
  `service_id` int not null,
  `recipe_id` int not null,
  PRIMARY KEY (`id`)
);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
