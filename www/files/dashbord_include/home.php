<div id="home" class="thumbnail" style="position: absolute; top: 10%; width: 100vw; border-radius: 0px;">
    <div class="caption">
        <h1>Bonjour <?= $_SESSION['user']['username']?></h1>
        <h2>Acceuil</h2>
        <button onclick="document.getElementById('home').style.display = 'none'; document.getElementById('perms').style.display = 'block';" class="btn btn-success" style="width: 150px; height: 150px;">Permissions</button>
        <button onclick="document.getElementById('home').style.display = 'none'; document.getElementById('data').style.display = 'block';" class="btn btn-success" style="width: 150px; height: 150px;">Données</button>
        <button onclick="location.href='connect/logout.php';" class="btn btn-success" style="width: 150px; height: 150px;"><span class="glyphicon glyphicon-lock" style="font-size: 50px; padding-bottom: 25px;" aria-hidden="true"></span><br>Se déconnecter</button>
    </div>
</div>