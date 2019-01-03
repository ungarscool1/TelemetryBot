<?php
    if ($_POST) {
        $password = true;
        if (empty($_POST['password'])) {
            $ch = curl_init();
            $url = "127.0.0.1:8081/setPass/" . $_POST['username'];
            curl_setopt($ch, CURLOPT_URL, $url);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
            curl_exec($ch);
            curl_close($ch);
        } else {
            $ch = curl_init();
            $url = "127.0.0.1:8081/auth/" . $_POST['username'];
            curl_setopt($ch, CURLOPT_URL, $url);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
            curl_setopt($ch, CURLOPT_POSTFIELDS, "password=".$_POST['password']);
            $req = curl_exec($ch);
            curl_close($ch);
            if ($req=="success") {
                $query = "
            SELECT * FROM users
            WHERE
                discordId = :username
        ";
                $query_params = array(
                    ':username' => $_POST['username']
                );
                try{
                    $stmt = $db->prepare($query);
                    $result = $stmt->execute($query_params);
                }
                catch(PDOException $ex){ die("Failed to run query: " . $ex->getMessage()); }
                $row = $stmt->fetch();
                $_SESSION['user'] = $row;
                header("Location: index.php");
            } else {
                if ($req=="wrong") {
                    echo '<center><div class="alert alert-danger" role="alert"><strong>Erreur :</strong>Votre mot de passe est faux</div></center>';
                } else {
                    echo '<center><div class="alert alert-danger" role="alert"><strong>Erreur :</strong>Votre mot de passe est expiré</div></center>';
                }
            }
        }
    } else {
        $password = false;
    }
?>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Connexion</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
</head>
<body style="background-color: #2c3e50;">
<center>
    <div id="all" class="thumbnail" style="position: absolute; top: 35%; width: 100%;">
        <div class="caption">
            <h1>Se connecter</h1>
            <form action="#" method="post">
                <div class="input-group">
                    <?php
                    if ($_POST && $_POST['username']!=null) {
                        ?>
                        <input type="text" class="form-control" name="username" placeholder="Identifiant" value="<?= $_POST['username'] ?>">
                        <?php
                    }  else {
                        if ($_GET && $_GET['user']!=null) {
                            ?>
                            <input type="text" class="form-control" name="username" placeholder="Identifiant" value="<?= $_GET['user'] ?>">
                            <?php
                        } else {
                            ?>
                            <input type="text" class="form-control" name="username" placeholder="Identifiant">
                            <?php
                        }
                    }



                        if ($password) {
                            ?>
                            <input type="password" class="form-control" name="password" placeholder="Mot de passe">
                            <?php
                        }
                        ?>
                </div>
                <br>
                <button type="submit" class="btn btn-success"><?php
                    if ($password) {
                        echo "Se connecter";
                    } else {
                        echo "Continuer";
                    }
                    ?></button>
            </form>
        </div>
    </div>
</center>



</body>
</html>