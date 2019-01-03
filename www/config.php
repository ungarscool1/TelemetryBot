<?php
// Database configuration

$host = "127.0.0.1";
$user = "root";
$pass = "";
$dbName = "telemetry";

// do not touch !
session_start();
$db = new PDO('mysql:host='.$host.';dbname='.$dbName, $user, $pass);