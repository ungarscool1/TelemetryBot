<?php
require 'config.php';
if (empty($_SESSION['user']['discordId'])) {
    header("Location: connect/");
} else {
    include 'files/dash.php';
}