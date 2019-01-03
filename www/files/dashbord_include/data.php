<div id="data" class="thumbnail" style="position: absolute; height: 99vh; width: 100vw; display: none;  border-radius: 0px;">
    <div class="caption">

        <h2>Vos données télémétrique</h2>
        <button onclick="document.getElementById('data').style.display = 'none'; document.getElementById('home').style.display = 'block';" class="btn" style="position: absolute; top: 15px; left: 15px; width: 50px; height: 50px; border-radius: 100%"><span class="glyphicon glyphicon-arrow-left" style="position:relative; top: -1px; right: 10px; font-size: 40px; padding-bottom: 25px;" aria-hidden="true"></span></button>
        <div id="dataTable" class="table-responsive">
            <table class="table">
                <thead>
                <tr>
                    <th>Nom de la donnée</th>
                    <th>Valeur</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>Votre identifiant</td>
                    <td><?= $_SESSION['user']['discordId'] ?></td>
                </tr>
                <tr>
                    <td>Nombre de connexion</td>
                    <td><?= $_SESSION['user']['nombreDeCo'] ?></td>
                </tr>
                <tr>
                    <td>Temps de connexion</td>
                    <td><?php
                        $connectTime = "";
                        $time = $_SESSION['user']['TempsDeCo'] / 3600;
                        if (intval($time)>0) {
                            $connectTime = intval($time) . "h";
                            $time = $time - intval($time);
                        }
                        $time = $time * 60;
                        if (intval($time)>0) {
                            $connectTime = $connectTime . " " . intval($time) . "m";
                            $time = $time - intval($time);
                        }
                        $time = $time * 60;
                        if (intval($time)>0) {
                            $connectTime = $connectTime . " " . intval($time) . "s";
                            $time = $time - intval($time);
                        }
                        echo $connectTime;
                        ?></td>
                </tr>
                <tr>
                    <td>Temps de jeu</td>
                    <td><?php
                            $playTime = "";
                            $time = $_SESSION['user']['playTime'] / 3600;
                            if (intval($time)>0) {
                                $playTime = intval($time) . "h";
                                $time = $time - intval($time);
                            }
                            $time = $time * 60;
                            if (intval($time)>0) {
                                $playTime = $playTime . " " . intval($time) . "m";
                                $time = $time - intval($time);
                            }
                            $time = $time * 60;
                            if (intval($time)>0) {
                                $playTime = $playTime . " " . intval($time) . "s";
                                $time = $time - intval($time);
                            }
                            echo $playTime;
                        ?></td>
                </tr>
                <tr>
                    <td>Votre jeu préféré</td>
                    <td>Nous travaillons dessus</td>
                </tr>
                <tr>
                    <td>Votre cannal préféré</td>
                    <td>Nous travaillons dessus</td>
                </tr>
                <tr>
                    <td>Nombre de message envoyé</td>
                    <td><?= $_SESSION['user']['numberOfMessage'] ?></td>
                </tr>
                <tr>
                    <td>Temps totals d'écriture (en sec)</td>
                    <td><?php
                        $writeTime = "";
                        $time = $_SESSION['user']['typingTime'] / 3600;
                        if (intval($time)>0) {
                            $writeTime = intval($time) . "h";
                            $time = $time - intval($time);
                        }
                        $time = $time * 60;
                        if (intval($time)>0) {
                            $writeTime = $writeTime . " " . intval($time) . "m";
                            $time = $time - intval($time);
                        }
                        $time = $time * 60;
                        if (intval($time)>0) {
                            $writeTime = $writeTime . " " . intval($time) . "s";
                            $time = $time - intval($time);
                        }
                        echo $writeTime;
                        ?></td>
                </tr>
                <tr>
                    <td>Temps d'écriture moyen (en sec/msg)</td>
                    <td><?= $_SESSION['user']['typingTime'] / $_SESSION['user']['numberOfMessage'] ?></td>
                </tr>
                <tr>
                    <td>Dernière synchro</td>
                    <td><?= $_SESSION['user']['lastSync']?></td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>