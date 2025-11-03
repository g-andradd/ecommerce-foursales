-- MySQL dump 10.13  Distrib 8.0.44, for Linux (x86_64)
--
-- Host: localhost    Database: ecommerce
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `script` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','init','SQL','V1__init.sql',-1245496255,'app','2025-11-02 19:33:48',947,1),(2,'2','seed','SQL','V2__seed.sql',-1957828105,'app','2025-11-02 19:33:49',22,1),(3,'3','ajustes ecommerce','SQL','V3__ajustes_ecommerce.sql',1090321121,'app','2025-11-02 19:33:50',1125,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `itens_pedido`
--

DROP TABLE IF EXISTS `itens_pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `itens_pedido` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pedido_id` binary(16) NOT NULL,
  `produto_id` binary(16) NOT NULL,
  `quantidade` int NOT NULL,
  `preco_unitario` decimal(12,2) NOT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_itens_pedido` (`pedido_id`),
  KEY `idx_itens_produto` (`produto_id`),
  CONSTRAINT `fk_itens_pedido_pedido_id` FOREIGN KEY (`pedido_id`) REFERENCES `pedidos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_itens_produto` FOREIGN KEY (`produto_id`) REFERENCES `produtos` (`id`),
  CONSTRAINT `chk_itens_preco_unit_nonneg` CHECK ((`preco_unitario` >= 0)),
  CONSTRAINT `chk_itens_quantidade_pos` CHECK ((`quantidade` > 0)),
  CONSTRAINT `chk_itens_subtotal_nonneg` CHECK ((`subtotal` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `itens_pedido`
--

LOCK TABLES `itens_pedido` WRITE;
/*!40000 ALTER TABLE `itens_pedido` DISABLE KEYS */;
INSERT INTO `itens_pedido` VALUES (1,_binary ':\’\"\ÃFÜfÍ¨âô¶/',_binary '\⁄\Ïz∏\"\∏|B¨\0',2,59.90,119.80),(2,_binary ':\’\"\ÃFÜfÍ¨âô¶/',_binary '\⁄\ÓM\∏\"\∏|B¨\0',1,179.90,179.90),(3,_binary 'fº£¨tëCèC∂\¬#≤f\Ô',_binary '\⁄\Ïz∏\"\∏|B¨\0',1,50.00,50.00),(4,_binary 'fº£¨tëCèC∂\¬#≤f\Ô',_binary 'ó¶\›\∆\œF¨¨Gôë\nT]',4,99.99,399.96),(5,_binary 'yo∞n\›C\rî\ıù£\Ôä',_binary '\⁄\Ïz∏\"\∏|B¨\0',1,50.00,50.00),(6,_binary 'yo∞n\›C\rî\ıù£\Ôä',_binary 'ó¶\›\∆\œF¨¨Gôë\nT]',4,99.99,399.96),(7,_binary '´<]\È7\ÀCèÑT∑\'\ÊH—£',_binary '\⁄\Ïz∏\"\∏|B¨\0',1,59.90,59.90),(8,_binary '´<]\È7\ÀCèÑT∑\'\ÊH—£',_binary 'ó¶\›\∆\œF¨¨Gôë\nT]',4,99.99,399.96),(9,_binary '\ÕG>%¯CHKéQä\≈cPo',_binary '\⁄\Ïz∏\"\∏|B¨\0',1,59.90,59.90),(10,_binary '\ÕG>%¯CHKéQä\≈cPo',_binary 'ó¶\›\∆\œF¨¨Gôë\nT]',4,99.99,399.96);
/*!40000 ALTER TABLE `itens_pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedidos`
--

DROP TABLE IF EXISTS `pedidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedidos` (
  `id` binary(16) NOT NULL,
  `usuario_id` binary(16) NOT NULL,
  `status` enum('PENDENTE','PAGO','CANCELADO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `total` decimal(12,2) NOT NULL DEFAULT '0.00',
  `criado_em` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `pago_em` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_pedidos_usuario` (`usuario_id`),
  KEY `idx_pedidos_status_criado` (`status`,`criado_em`),
  KEY `idx_pedidos_pago_em` (`pago_em`),
  KEY `idx_pedidos_status` (`status`),
  KEY `idx_pedidos_status_pagoem` (`status`,`pago_em`),
  CONSTRAINT `fk_pedidos_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos`
--

LOCK TABLES `pedidos` WRITE;
/*!40000 ALTER TABLE `pedidos` DISABLE KEYS */;
INSERT INTO `pedidos` VALUES (_binary ':\’\"\ÃFÜfÍ¨âô¶/',_binary '∂Xkô\r\\Iä∂@öÇZ\Á1\…','PAGO',299.70,'2025-11-02 20:10:24',NULL),(_binary 'fº£¨tëCèC∂\¬#≤f\Ô',_binary '∂Xkô\r\\Iä∂@öÇZ\Á1\…','PENDENTE',449.96,'2025-11-02 22:11:04',NULL),(_binary 'yo∞n\›C\rî\ıù£\Ôä',_binary '∂Xkô\r\\Iä∂@öÇZ\Á1\…','PENDENTE',449.96,'2025-11-02 22:29:39',NULL),(_binary '´<]\È7\ÀCèÑT∑\'\ÊH—£',_binary '∂Xkô\r\\Iä∂@öÇZ\Á1\…','CANCELADO',459.86,'2025-11-02 22:31:28',NULL),(_binary '\ÕG>%¯CHKéQä\≈cPo',_binary '∂Xkô\r\\Iä∂@öÇZ\Á1\…','PAGO',459.86,'2025-11-02 22:32:01',NULL);
/*!40000 ALTER TABLE `pedidos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produtos`
--

DROP TABLE IF EXISTS `produtos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produtos` (
  `id` binary(16) NOT NULL,
  `nome` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descricao` text COLLATE utf8mb4_unicode_ci,
  `preco` decimal(12,2) NOT NULL,
  `categoria` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quantidade_estoque` int NOT NULL,
  `criado_em` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `atualizado_em` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_produtos_nome` (`nome`),
  KEY `idx_produtos_categoria` (`categoria`),
  CONSTRAINT `chk_produtos_preco_nonneg` CHECK ((`preco` >= 0)),
  CONSTRAINT `chk_produtos_qtd_nonneg` CHECK ((`quantidade_estoque` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produtos`
--

LOCK TABLES `produtos` WRITE;
/*!40000 ALTER TABLE `produtos` DISABLE KEYS */;
INSERT INTO `produtos` VALUES (_binary 'ó¶\›\∆\œF¨¨Gôë\nT]','Massageador','Massageasdor com bateria de at√© 16 horas de uso',99.99,'Eletr√¥nicos',0,'2025-11-02 21:49:59','2025-11-02 22:35:32'),(_binary '\⁄\Ïz∏\"\∏|B¨\0','Camiseta Basica','Camiseta 100% algodao, unissex, varias cores',50.00,'Vestu√°rio',117,'2025-11-02 19:33:48','2025-11-02 23:52:14'),(_binary '\⁄\ÓM\∏\"\∏|B¨\0','Cal√ßa Jeans Slim','Modelagem slim com elastano, lavagem escura',179.90,'Vestu√°rio',79,'2025-11-02 19:33:48','2025-11-02 20:16:41'),(_binary '\⁄\ÓO!∏\"\∏|B¨\0','Tenis Confort','Tenis esportivo leve e respiravel, varias cores',299.90,'Cal√ßados',50,'2025-11-02 19:33:48','2025-11-02 19:33:48'),(_binary '\⁄\ÓOÑ∏\"\∏|B¨\0','Relogio Digital','Relogio digital a prova d‚Äô√°gua com cronometro e luz de fundo',249.90,'Acess√≥rios',30,'2025-11-02 19:33:48','2025-11-02 19:33:48'),(_binary '\⁄\ÓOœ∏\"\∏|B¨\0','Mochila Casual','Mochila com compartimento para notebook de at√© 15 polegadas',199.90,'Acess√≥rios',40,'2025-11-02 19:33:48','2025-11-02 19:33:48'),(_binary '\⁄\ÓP∏\"\∏|B¨\0','Fone Bluetooth','Fone sem fio com cancelamento de ruido e bateria de longa dura√ß√£o',349.90,'Eletr√¥nicos',25,'2025-11-02 19:33:48','2025-11-02 19:33:48'),(_binary '\⁄\ÓPS∏\"\∏|B¨\0','Mouse Gamer','Mouse com 6 botoes programaveis e sensor de alta precis√£o',159.90,'Eletr√¥nicos',60,'2025-11-02 19:33:48','2025-11-02 19:33:48'),(_binary '\⁄\ÓPí∏\"\∏|B¨\0','Cadeira Ergonomica','Cadeira giratoria com apoio lombar e regulagem de altura',899.90,'M√≥veis',15,'2025-11-02 19:33:48','2025-11-02 19:33:48');
/*!40000 ALTER TABLE `produtos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` binary(16) NOT NULL,
  `nome` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(160) COLLATE utf8mb4_unicode_ci NOT NULL,
  `senha` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `perfil` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `criado_em` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `atualizado_em` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ativo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_usuarios_email` (`email`),
  KEY `idx_usuarios_perfil` (`perfil`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (_binary 'E\∆\·‹ÖîLô•\Ù¥Zå¥më','Teste Gabriel','teste.gabriel@example.com','$2a$10$3Nf4OwH0Spsj91/FQeEOUuNz/kNS3ul364rf/fqkd6zZbn5Z7Md4.','ADMIN','2025-11-02 19:34:40','2025-11-02 19:34:40',1),(_binary '∂Xkô\r\\Iä∂@öÇZ\Á1\…','User Gabriel','user.gabriel@example.com','$2a$10$wmFUYkY.cVdtB8d5QEsN7uiL9l4Kz60QxoRGDZ7VALNI0fDmWaoY6','USER','2025-11-02 19:40:00','2025-11-02 19:40:00',1),(_binary '\⁄6\ZxZ∞DvÉÖ.åZ\¬1','Teste Swagger','teste.swagger@example.com','$2a$10$RUgmsM7qvOzS09bt2bi7Ke.2Sg0xQWKGIOCeyfW2Jun9aTzmcyI.2','ADMIN','2025-11-03 03:24:11','2025-11-03 03:24:11',1),(_binary '\⁄\Ï,⁄∏\"\∏|B¨\0','Administrador','admin@demo.com','$2a$10$2bPZ/5p9ZUd6s0b2Y0F7puQmS6b1v8q0o7L9J0QmT0y7r3mS1b0H6','ADMIN','2025-11-02 19:33:48','2025-11-02 19:33:48',1),(_binary '\⁄\Ï/:∏\"\∏|B¨\0','Jo√£o Silva','joao@demo.com','$2a$10$2bPZ/5p9ZUd6s0b2Y0F7puQmS6b1v8q0o7L9J0QmT0y7r3mS1b0H6','USER','2025-11-02 19:33:48','2025-11-02 19:33:48',1),(_binary '\⁄\Ï0ø∏\"\∏|B¨\0','Maria Souza','maria@demo.com','$2a$10$2bPZ/5p9ZUd6s0b2Y0F7puQmS6b1v8q0o7L9J0QmT0y7r3mS1b0H6','USER','2025-11-02 19:33:48','2025-11-02 19:33:48',1);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-03 18:23:33
