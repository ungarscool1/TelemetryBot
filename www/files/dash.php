<?php
    if ($_POST) {
        if ($_POST['modifyTelemetry']!=null) {
            $db->exec("UPDATE users set acceptTelemetry = ".$_POST['modifyTelemetry']." WHERE discordId = '".$_SESSION['user']['discordId']."'");
            $_SESSION['user']['acceptTelemetry'] = $_POST['modifyTelemetry'];
    }
        if ($_POST['modifyTelemetryAvgConnectedTime']!=null) {
            $db->exec("UPDATE users set `Perm.AVERAGE_CONNECTED_TIME` = ".$_POST['modifyTelemetryAvgConnectedTime']." WHERE discordId = '".$_SESSION['user']['discordId']."'");
            $_SESSION['user']['Perm.AVERAGE_CONNECTED_TIME'] = $_POST['modifyTelemetryAvgConnectedTime'];
        }
        if ($_POST['modifyTelemetryAvgPlayTime']!=null) {
            $db->exec("UPDATE users set `Perm.AVERAGE_PLAY_TIME` = ".$_POST['modifyTelemetryAvgPlayTime']." WHERE discordId = '".$_SESSION['user']['discordId']."'");
            $_SESSION['user']['Perm.AVERAGE_PLAY_TIME'] = $_POST['modifyTelemetryAvgPlayTime'];
        }
        if ($_POST['modifyTelemetryFavChannel']!=null) {
            $db->exec("UPDATE users set `Perm.FAVORITE_CHANNEL` = ".$_POST['modifyTelemetryFavChannel']." WHERE discordId = '".$_SESSION['user']['discordId']."'");
            $_SESSION['user']['Perm.FAVORITE_CHANNEL'] = $_POST['modifyTelemetryFavChannel'];
        }
        if ($_POST['modifyTelemetryFavGame']!=null) {
            $db->exec("UPDATE users set `Perm.FAVORITE_GAME` = ".$_POST['modifyTelemetryFavGame']." WHERE discordId = '".$_SESSION['user']['discordId']."'");
            $_SESSION['user']['Perm.FAVORITE_GAME'] = $_POST['modifyTelemetryFavGame'];
        }
        if ($_POST['modifyTelemetryNumberOfMessage']!=null) {
            $db->exec("UPDATE users set `Perm.NUMBER_OF_MESSAGE` = ".$_POST['modifyTelemetryNumberOfMessage']." WHERE discordId = '".$_SESSION['user']['discordId']."'");
            $_SESSION['user']['Perm.NUMBER_OF_MESSAGE'] = $_POST['modifyTelemetryNumberOfMessage'];
        }
        if ($_POST['modifyTelemetryWriteTime']!=null) {
            $db->exec("UPDATE users set `Perm.TYPING_TIME` = ".$_POST['modifyTelemetryWriteTime']." WHERE discordId = '".$_SESSION['user']['discordId']."'");
            $_SESSION['user']['Perm.TYPING_TIME'] = $_POST['modifyTelemetryWriteTime'];
        }
        $ch = curl_init();
        $url = "127.0.0.1:4567/update/" . $_SESSION['user']['discordId'];
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        curl_exec($ch);
        curl_close($ch);
        header("Location: index.php");
    }
?>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Panneau de configuration</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
</head>
<body style="background-color: #2c3e50; min-height: 100%; max-height: 100%;">
<center>
    <?php
    include "dashbord_include/home.php";
    include "dashbord_include/perms.php";
    include "dashbord_include/data.php";
    ?>
</center>
</body>
</html>