-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Jan 03, 2019 at 11:28 PM
-- Server version: 5.7.23
-- PHP Version: 7.2.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `telemetry`
--
CREATE DATABASE IF NOT EXISTS `telemetry` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;
USE `telemetry`;
-- --------------------------------------------------------

--
-- Table structure for table `channels`
--

DROP TABLE IF EXISTS `channels`;
CREATE TABLE IF NOT EXISTS `channels` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `discordId` varchar(255) COLLATE utf8_bin NOT NULL,
  `channelName` varchar(255) COLLATE utf8_bin NOT NULL,
  `nombre` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=27 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- --------------------------------------------------------

--
-- Table structure for table `games`
--

DROP TABLE IF EXISTS `games`;
CREATE TABLE IF NOT EXISTS `games` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `discordId` varchar(255) COLLATE utf8_bin NOT NULL,
  `gameName` varchar(255) COLLATE utf8_bin NOT NULL,
  `playTime` bigint(255) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `logs`
--

DROP TABLE IF EXISTS `logs`;
CREATE TABLE IF NOT EXISTS `logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `temps` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `de` varchar(255) COLLATE utf8_bin NOT NULL,
  `action` varchar(255) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `discordId` varchar(255) COLLATE utf8_bin NOT NULL,
  `username` varchar(255) COLLATE utf8_bin NOT NULL,
  `acceptTelemetry` tinyint(1) NOT NULL DEFAULT '0',
  `Perm.AVERAGE_CONNECTED_TIME` tinyint(1) NOT NULL DEFAULT '1',
  `Perm.AVERAGE_PLAY_TIME` tinyint(1) NOT NULL DEFAULT '1',
  `Perm.FAVORITE_GAME` tinyint(1) NOT NULL DEFAULT '1',
  `Perm.FAVORITE_CHANNEL` tinyint(1) NOT NULL DEFAULT '1',
  `Perm.NUMBER_OF_MESSAGE` tinyint(1) NOT NULL DEFAULT '1',
  `Perm.TYPING_TIME` tinyint(1) NOT NULL DEFAULT '1',
  `numberOfMessage` bigint(255) NOT NULL DEFAULT '0',
  `nombreDeCo` bigint(20) NOT NULL DEFAULT '0',
  `TempsDeCo` bigint(20) NOT NULL DEFAULT '0',
  `playTime` bigint(255) NOT NULL DEFAULT '0',
  `games` varchar(2048) COLLATE utf8_bin NOT NULL DEFAULT '{}',
  `typingTime` bigint(255) NOT NULL,
  `lastSync` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `discordId` (`discordId`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
