-- MySQL dump 10.13  Distrib 5.6.28, for Win64 (x86_64)
--
-- Host: localhost    Database: ljsa
-- ------------------------------------------------------
-- Server version	5.6.28-log

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
-- Table structure for table `ljsa_attivita`
--

DROP TABLE IF EXISTS `ljsa_attivita`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ljsa_attivita` (
  `ID_SERVIZIO` varchar(50) NOT NULL,
  `ID_ATTIVITA` varchar(50) NOT NULL,
  `DESCRIZIONE` varchar(255) NOT NULL,
  `CLASSE` varchar(255) NOT NULL,
  `ATTIVO` varchar(1) NOT NULL,
  `ID_CREDENZIALE_INS` varchar(50) NOT NULL,
  `DATA_INSERIMENTO` int(11) NOT NULL,
  `ORA_INSERIMENTO` int(11) NOT NULL,
  `ID_CREDENZIALE_AGG` varchar(50) NOT NULL,
  `DATA_AGGIORNAMENTO` int(11) NOT NULL,
  `ORA_AGGIORNAMENTO` int(11) NOT NULL,
  PRIMARY KEY (`ID_SERVIZIO`,`ID_ATTIVITA`),
  KEY `FK_LJSA_LOG_ATT_CLASSE` (`CLASSE`),
  KEY `FK_LJSA_LOG_ATT_CR_INS` (`ID_SERVIZIO`,`ID_CREDENZIALE_INS`),
  KEY `FK_LJSA_LOG_ATT_CR_AGG` (`ID_SERVIZIO`,`ID_CREDENZIALE_AGG`),
  CONSTRAINT `FK_LJSA_LOG_ATT_CLASSE` FOREIGN KEY (`CLASSE`) REFERENCES `ljsa_classi` (`CLASSE`),
  CONSTRAINT `FK_LJSA_LOG_ATT_CR_AGG` FOREIGN KEY (`ID_SERVIZIO`, `ID_CREDENZIALE_AGG`) REFERENCES `ljsa_credenziali` (`ID_SERVIZIO`, `ID_CREDENZIALE`),
  CONSTRAINT `FK_LJSA_LOG_ATT_CR_INS` FOREIGN KEY (`ID_SERVIZIO`, `ID_CREDENZIALE_INS`) REFERENCES `ljsa_credenziali` (`ID_SERVIZIO`, `ID_CREDENZIALE`),
  CONSTRAINT `FK_LJSA_LOG_ATT_ID_SER` FOREIGN KEY (`ID_SERVIZIO`) REFERENCES `ljsa_servizi` (`ID_SERVIZIO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ljsa_attivita`
--

LOCK TABLES `ljsa_attivita` WRITE;
/*!40000 ALTER TABLE `ljsa_attivita` DISABLE KEYS */;
/*!40000 ALTER TABLE `ljsa_attivita` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ljsa_attivita_conf`
--

DROP TABLE IF EXISTS `ljsa_attivita_conf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ljsa_attivita_conf` (
  `ID_SERVIZIO` varchar(50) NOT NULL,
  `ID_ATTIVITA` varchar(50) NOT NULL,
  `OPZIONE` varchar(255) NOT NULL,
  `DESCRIZIONE` varchar(255) NOT NULL,
  `VALORI` varchar(255) DEFAULT NULL,
  `PREDEFINITO` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID_SERVIZIO`,`ID_ATTIVITA`,`OPZIONE`),
  CONSTRAINT `FK_LJSA_ATT_CON_ID_ATT` FOREIGN KEY (`ID_SERVIZIO`, `ID_ATTIVITA`) REFERENCES `ljsa_attivita` (`ID_SERVIZIO`, `ID_ATTIVITA`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ljsa_attivita_conf`
--

LOCK TABLES `ljsa_attivita_conf` WRITE;
/*!40000 ALTER TABLE `ljsa_attivita_conf` DISABLE KEYS */;
/*!40000 ALTER TABLE `ljsa_attivita_conf` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ljsa_attivita_notifica`
--

DROP TABLE IF EXISTS `ljsa_attivita_notifica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ljsa_attivita_notifica` (
  `ID_SERVIZIO` varchar(50) NOT NULL,
  `ID_ATTIVITA` varchar(50) NOT NULL,
  `EVENTO` varchar(1) NOT NULL,
  `DESTINAZIONE` varchar(255) NOT NULL,
  PRIMARY KEY (`ID_SERVIZIO`,`ID_ATTIVITA`,`EVENTO`,`DESTINAZIONE`),
  CONSTRAINT `FK_LJSA_ATT_NOT_ID_ATT` FOREIGN KEY (`ID_SERVIZIO`, `ID_ATTIVITA`) REFERENCES `ljsa_attivita` (`ID_SERVIZIO`, `ID_ATTIVITA`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ljsa_attivita_notifica`
--

LOCK TABLES `ljsa_attivita_notifica` WRITE;
/*!40000 ALTER TABLE `ljsa_attivita_notifica` DISABLE KEYS */;
/*!40000 ALTER TABLE `ljsa_attivita_notifica` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ljsa_attivita_parametri`
--

DROP TABLE IF EXISTS `ljsa_attivita_parametri`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ljsa_attivita_parametri` (
  `ID_SERVIZIO` varchar(50) NOT NULL,
  `ID_ATTIVITA` varchar(50) NOT NULL,
  `PARAMETRO` varchar(255) NOT NULL,
  `DESCRIZIONE` varchar(255) NOT NULL,
  `VALORI` varchar(255) DEFAULT NULL,
  `PREDEFINITO` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID_SERVIZIO`,`ID_ATTIVITA`,`PARAMETRO`),
  CONSTRAINT `FK_LJSA_ATT_PAR_ID_ATT` FOREIGN KEY (`ID_SERVIZIO`, `ID_ATTIVITA`) REFERENCES `ljsa_attivita` (`ID_SERVIZIO`, `ID_ATTIVITA`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ljsa_attivita_parametri`
--

LOCK TABLES `ljsa_attivita_parametri` WRITE;
/*!40000 ALTER TABLE `ljsa_attivita_parametri` DISABLE KEYS */;
/*!40000 ALTER TABLE `ljsa_attivita_parametri` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ljsa_classi`
--

DROP TABLE IF EXISTS `ljsa_classi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ljsa_classi` (
  `CLASSE` varchar(255) NOT NULL,
  `DESCRIZIONE` varchar(255) NOT NULL,
  PRIMARY KEY (`CLASSE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ljsa_classi`
--

LOCK TABLES `ljsa_classi` WRITE;
/*!40000 ALTER TABLE `ljsa_classi` DISABLE KEYS */;
INSERT INTO `ljsa_classi` VALUES ('org.dew.ljsa.LJSQL','SQL command executor'),('org.dew.ljsa.LJTest','Test implementation');
/*!40000 ALTER TABLE `ljsa_classi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ljsa_credenziali`
--

DROP TABLE IF EXISTS `ljsa_credenziali`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ljsa_credenziali` (
  `ID_SERVIZIO` varchar(50) NOT NULL,
  `ID_CREDENZIALE` varchar(50) NOT NULL,
  `CREDENZIALI` varchar(100) NOT NULL,
  `EMAIL` varchar(100) DEFAULT NULL,
  `ATTIVO` varchar(1) NOT NULL,
  PRIMARY KEY (`ID_SERVIZIO`,`ID_CREDENZIALE`),
  KEY `IDX_LJSA_CRED_ID_CRED` (`ID_CREDENZIALE`),
  KEY `IDX_LJSA_CRED_EMAIL` (`EMAIL`),
  CONSTRAINT `FK_LJSA_CRED_ID_SER` FOREIGN KEY (`ID_SERVIZIO`) REFERENCES `ljsa_servizi` (`ID_SERVIZIO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ljsa_credenziali`
--

LOCK TABLES `ljsa_credenziali` WRITE;
/*!40000 ALTER TABLE `ljsa_credenziali` DISABLE KEYS */;
INSERT INTO `ljsa_credenziali` VALUES ('LJSA','admin','1559035354','admin@dew.org','S');
/*!40000 ALTER TABLE `ljsa_credenziali` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ljsa_log`
--

DROP TABLE IF EXISTS `ljsa_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ljsa_log` (
  `ID_LOG` int(11) NOT NULL,
  `ID_SCHEDULAZIONE` int(11) NOT NULL,
  `DATA_INIZIO` int(11) NOT NULL,
  `ORA_INIZIO` int(11) NOT NULL,
  `DATA_FINE` int(11) DEFAULT NULL,
  `ORA_FINE` int(11) DEFAULT NULL,
  `RAPPORTO` varchar(255) DEFAULT NULL,
  `STATO` varchar(1) NOT NULL,
  PRIMARY KEY (`ID_LOG`),
  KEY `FK_LJSA_LOG_ID_SCH` (`ID_SCHEDULAZIONE`),
  CONSTRAINT `FK_LJSA_LOG_ID_SCH` FOREIGN KEY (`ID_SCHEDULAZIONE`) REFERENCES `ljsa_schedulazioni` (`ID_SCHEDULAZIONE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ljsa_log`
--

LOCK TABLES `ljsa_log` WRITE;
/*!40000 ALTER TABLE `ljsa_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `ljsa_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ljsa_log_files`
--

DROP TABLE IF EXISTS `ljsa_log_files`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ljsa_log_files` (
  `ID_LOG` int(11) NOT NULL,
  `NOME_FILE` varchar(255) NOT NULL,
  `TIPOLOGIA` varchar(1) NOT NULL,
  `URL_FILE` varchar(255) NOT NULL,
  PRIMARY KEY (`ID_LOG`,`NOME_FILE`),
  CONSTRAINT `FK_LJSA_LOG_FILES_ID_LOG` FOREIGN KEY (`ID_LOG`) REFERENCES `ljsa_log` (`ID_LOG`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ljsa_log_files`
--

LOCK TABLES `ljsa_log_files` WRITE;
/*!40000 ALTER TABLE `ljsa_log_files` DISABLE KEYS */;
/*!40000 ALTER TABLE `ljsa_log_files` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ljsa_log_schedulatore`
--

DROP TABLE IF EXISTS `ljsa_log_schedulatore`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ljsa_log_schedulatore` (
  `ID_SCHEDULATORE` varchar(50) NOT NULL,
  `DATA_SCHEDULAZIONE` int(11) NOT NULL,
  `ORA_SCHEDULAZIONE` int(11) NOT NULL,
  `DATA_AGGIORNAMENTO` int(11) NOT NULL,
  `ORA_AGGIORNAMENTO` int(11) NOT NULL,
  PRIMARY KEY (`ID_SCHEDULATORE`,`DATA_SCHEDULAZIONE`,`ORA_SCHEDULAZIONE`),
  CONSTRAINT `FK_LJSA_LOG_SCHED_ID_SCH` FOREIGN KEY (`ID_SCHEDULATORE`) REFERENCES `ljsa_schedulatori` (`ID_SCHEDULATORE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ljsa_log_schedulatore`
--

LOCK TABLES `ljsa_log_schedulatore` WRITE;
/*!40000 ALTER TABLE `ljsa_log_schedulatore` DISABLE KEYS */;
/*!40000 ALTER TABLE `ljsa_log_schedulatore` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ljsa_progressivi`
--

DROP TABLE IF EXISTS `ljsa_progressivi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ljsa_progressivi` (
  `CODICE` varchar(50) NOT NULL,
  `VALORE` int(11) NOT NULL,
  PRIMARY KEY (`CODICE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ljsa_progressivi`
--

LOCK TABLES `ljsa_progressivi` WRITE;
/*!40000 ALTER TABLE `ljsa_progressivi` DISABLE KEYS */;
/*!40000 ALTER TABLE `ljsa_progressivi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ljsa_schedulatori`
--

DROP TABLE IF EXISTS `ljsa_schedulatori`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ljsa_schedulatori` (
  `ID_SCHEDULATORE` varchar(50) NOT NULL,
  `URL_SERVIZIO` varchar(255) NOT NULL,
  `ATTIVO` varchar(1) NOT NULL,
  PRIMARY KEY (`ID_SCHEDULATORE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ljsa_schedulatori`
--

LOCK TABLES `ljsa_schedulatori` WRITE;
/*!40000 ALTER TABLE `ljsa_schedulatori` DISABLE KEYS */;
/*!40000 ALTER TABLE `ljsa_schedulatori` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ljsa_schedulazioni`
--

DROP TABLE IF EXISTS `ljsa_schedulazioni`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ljsa_schedulazioni` (
  `ID_SCHEDULAZIONE` int(11) NOT NULL,
  `ID_SERVIZIO` varchar(50) NOT NULL,
  `ID_ATTIVITA` varchar(50) NOT NULL,
  `DESCRIZIONE` varchar(255) NOT NULL,
  `SCHEDULAZIONE` varchar(50) NOT NULL,
  `ID_CREDENZIALE_INS` varchar(50) NOT NULL,
  `DATA_INSERIMENTO` int(11) NOT NULL,
  `ORA_INSERIMENTO` int(11) NOT NULL,
  `ID_CREDENZIALE_AGG` varchar(50) NOT NULL,
  `DATA_AGGIORNAMENTO` int(11) NOT NULL,
  `ORA_AGGIORNAMENTO` int(11) NOT NULL,
  `STATO` varchar(1) NOT NULL,
  `INIZIOVALIDITA` int(11) NOT NULL,
  `FINEVALIDITA` int(11) NOT NULL,
  `ESECUZIONI_COMPLETATE` int(11) NOT NULL,
  `ESECUZIONI_INTERROTTE` int(11) NOT NULL,
  PRIMARY KEY (`ID_SCHEDULAZIONE`),
  KEY `FK_LJSA_SCHED_ID_ATT` (`ID_SERVIZIO`,`ID_ATTIVITA`),
  KEY `FK_LJSA_SCHED_CR_INS` (`ID_SERVIZIO`,`ID_CREDENZIALE_INS`),
  KEY `FK_LJSA_SCHED_CR_UPD` (`ID_SERVIZIO`,`ID_CREDENZIALE_AGG`),
  KEY `IDX_LJSA_SCHED_VALIDITA` (`INIZIOVALIDITA`,`FINEVALIDITA`),
  CONSTRAINT `FK_LJSA_SCHED_CR_INS` FOREIGN KEY (`ID_SERVIZIO`, `ID_CREDENZIALE_INS`) REFERENCES `ljsa_credenziali` (`ID_SERVIZIO`, `ID_CREDENZIALE`),
  CONSTRAINT `FK_LJSA_SCHED_CR_UPD` FOREIGN KEY (`ID_SERVIZIO`, `ID_CREDENZIALE_AGG`) REFERENCES `ljsa_credenziali` (`ID_SERVIZIO`, `ID_CREDENZIALE`),
  CONSTRAINT `FK_LJSA_SCHED_ID_ATT` FOREIGN KEY (`ID_SERVIZIO`, `ID_ATTIVITA`) REFERENCES `ljsa_attivita` (`ID_SERVIZIO`, `ID_ATTIVITA`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ljsa_schedulazioni`
--

LOCK TABLES `ljsa_schedulazioni` WRITE;
/*!40000 ALTER TABLE `ljsa_schedulazioni` DISABLE KEYS */;
/*!40000 ALTER TABLE `ljsa_schedulazioni` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ljsa_schedulazioni_conf`
--

DROP TABLE IF EXISTS `ljsa_schedulazioni_conf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ljsa_schedulazioni_conf` (
  `ID_SCHEDULAZIONE` int(11) NOT NULL,
  `OPZIONE` varchar(255) NOT NULL,
  `VALORE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID_SCHEDULAZIONE`,`OPZIONE`),
  CONSTRAINT `FK_LJSA_SCH_CON_ID_SCH` FOREIGN KEY (`ID_SCHEDULAZIONE`) REFERENCES `ljsa_schedulazioni` (`ID_SCHEDULAZIONE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ljsa_schedulazioni_conf`
--

LOCK TABLES `ljsa_schedulazioni_conf` WRITE;
/*!40000 ALTER TABLE `ljsa_schedulazioni_conf` DISABLE KEYS */;
/*!40000 ALTER TABLE `ljsa_schedulazioni_conf` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ljsa_schedulazioni_notifica`
--

DROP TABLE IF EXISTS `ljsa_schedulazioni_notifica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ljsa_schedulazioni_notifica` (
  `ID_SCHEDULAZIONE` int(11) NOT NULL,
  `EVENTO` varchar(1) NOT NULL,
  `DESTINAZIONE` varchar(255) NOT NULL,
  PRIMARY KEY (`ID_SCHEDULAZIONE`,`EVENTO`,`DESTINAZIONE`),
  CONSTRAINT `FK_LJSA_SCH_NOT_ID_SCH` FOREIGN KEY (`ID_SCHEDULAZIONE`) REFERENCES `ljsa_schedulazioni` (`ID_SCHEDULAZIONE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ljsa_schedulazioni_notifica`
--

LOCK TABLES `ljsa_schedulazioni_notifica` WRITE;
/*!40000 ALTER TABLE `ljsa_schedulazioni_notifica` DISABLE KEYS */;
/*!40000 ALTER TABLE `ljsa_schedulazioni_notifica` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ljsa_schedulazioni_parametri`
--

DROP TABLE IF EXISTS `ljsa_schedulazioni_parametri`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ljsa_schedulazioni_parametri` (
  `ID_SCHEDULAZIONE` int(11) NOT NULL,
  `PARAMETRO` varchar(255) NOT NULL,
  `VALORE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID_SCHEDULAZIONE`,`PARAMETRO`),
  CONSTRAINT `FK_LJSA_SCH_PAR_ID_SCH` FOREIGN KEY (`ID_SCHEDULAZIONE`) REFERENCES `ljsa_schedulazioni` (`ID_SCHEDULAZIONE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ljsa_schedulazioni_parametri`
--

LOCK TABLES `ljsa_schedulazioni_parametri` WRITE;
/*!40000 ALTER TABLE `ljsa_schedulazioni_parametri` DISABLE KEYS */;
/*!40000 ALTER TABLE `ljsa_schedulazioni_parametri` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ljsa_servizi`
--

DROP TABLE IF EXISTS `ljsa_servizi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ljsa_servizi` (
  `ID_SERVIZIO` varchar(50) NOT NULL,
  `DESCRIZIONE` varchar(100) NOT NULL,
  `ATTIVO` varchar(1) NOT NULL,
  PRIMARY KEY (`ID_SERVIZIO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ljsa_servizi`
--

LOCK TABLES `ljsa_servizi` WRITE;
/*!40000 ALTER TABLE `ljsa_servizi` DISABLE KEYS */;
INSERT INTO `ljsa_servizi` VALUES ('LJSA','DEFAULT LJSA SERVICE','S');
/*!40000 ALTER TABLE `ljsa_servizi` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-10-21 18:16:23
