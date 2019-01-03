<?php
require '../config.php';
if (empty($_SESSION['user']['discordId'])) {
    include '../files/login.php';
} else {
    header("Location: ../");
}