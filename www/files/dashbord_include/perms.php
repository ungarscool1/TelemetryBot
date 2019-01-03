<div id="perms" class="thumbnail" style="position: absolute; height: 99vh; width: 100vw; display: none;  border-radius: 0px;">
    <div class="caption">

        <h2>Permissions accordées à la récolte</h2>
        <button onclick="document.getElementById('perms').style.display = 'none'; document.getElementById('home').style.display = 'block';" class="btn" style="position: absolute; top: 15px; left: 15px; width: 50px; height: 50px; border-radius: 100%"><span class="glyphicon glyphicon-arrow-left" style="position:relative; top: -1px; right: 10px; font-size: 40px; padding-bottom: 25px;" aria-hidden="true"></span></button>
        <div id="permsTable" class="table-responsive">
            <table class="table">
                <thead>
                <tr>
                    <th>Nom de la permission</th>
                    <th>Active</th>
                    <th>Modifier</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>Télémétrie</td>
                    <td><?php
                        if ($_SESSION['user']['acceptTelemetry']==1) {
                            echo "<span class=\"glyphicon glyphicon-ok\" aria-hidden=\"true\"></span>";
                        } else {
                            echo "<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\"></span>";
                        }
                        ?></td>
                    <td>
                        <form action="#" method="post">
                            <input type="text" style="display: none;" class="form-control" name="modifyTelemetry" value="<?php
                            if ($_SESSION['user']['acceptTelemetry']==1) {
                                echo "0";
                            } else {
                                echo "1";
                            }
                            ?>">
                            <button type="submit" class="btn btn-success"><?php
                                if ($_SESSION['user']['acceptTelemetry']==1) {
                                    echo "Désactiver";
                                } else {
                                    echo "Activer";
                                }
                                ?></button>
                        </form>
                    </td>
                </tr>
                <tr>
                    <td>Votre temps de connexion par jours</td>
                    <td><?php
                        if ($_SESSION['user']['Perm.AVERAGE_CONNECTED_TIME']==1) {
                            echo "<span class=\"glyphicon glyphicon-ok\" aria-hidden=\"true\"></span>";
                        } else {
                            echo "<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\"></span>";
                        }
                        ?></td>
                    <td>
                        <form action="#" method="post">
                            <input type="text" style="display: none;" class="form-control" name="modifyTelemetryAvgConnectedTime" value="<?php
                            if ($_SESSION['user']['Perm.AVERAGE_CONNECTED_TIME']==1) {
                                echo "0";
                            } else {
                                echo "1";
                            }
                            ?>">
                            <button type="submit" class="btn btn-success"><?php
                                if ($_SESSION['user']['Perm.AVERAGE_CONNECTED_TIME']==1) {
                                    echo "Désactiver";
                                } else {
                                    echo "Activer";
                                }
                                ?></button>
                        </form>
                    </td>
                </tr>
                <tr>
                    <td>Votre temps de jeu en moyenne par jours</td>
                    <td><?php
                        if ($_SESSION['user']['Perm.AVERAGE_PLAY_TIME']==1) {
                            echo "<span class=\"glyphicon glyphicon-ok\" aria-hidden=\"true\"></span>";
                        } else {
                            echo "<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\"></span>";
                        }
                        ?></td>
                    <td>
                        <form action="#" method="post">
                            <input type="text" style="display: none;" class="form-control" name="modifyTelemetryAvgPlayTime" value="<?php
                            if ($_SESSION['user']['Perm.AVERAGE_PLAY_TIME']==1) {
                                echo "0";
                            } else {
                                echo "1";
                            }
                            ?>">
                            <button type="submit" class="btn btn-success"><?php
                                if ($_SESSION['user']['Perm.AVERAGE_PLAY_TIME']==1) {
                                    echo "Désactiver";
                                } else {
                                    echo "Activer";
                                }
                                ?></button>
                        </form>
                    </td>
                </tr>
                <tr>
                    <td>Votre cannal préféré</td>
                    <td><?php
                        if ($_SESSION['user']['Perm.FAVORITE_CHANNEL']==1) {
                            echo "<span class=\"glyphicon glyphicon-ok\" aria-hidden=\"true\"></span>";
                        } else {
                            echo "<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\"></span>";
                        }
                        ?></td>
                    <td>
                        <form action="#" method="post">
                            <input type="text" style="display: none;" class="form-control" name="modifyTelemetryFavChannel" value="<?php
                            if ($_SESSION['user']['Perm.FAVORITE_CHANNEL']==1) {
                                echo "0";
                            } else {
                                echo "1";
                            }
                            ?>">
                            <button type="submit" class="btn btn-success"><?php
                                if ($_SESSION['user']['Perm.FAVORITE_CHANNEL']==1) {
                                    echo "Désactiver";
                                } else {
                                    echo "Activer";
                                }
                                ?></button>
                        </form>
                    </td>
                </tr>
                <tr>
                    <td>Votre jeu préféré</td>
                    <td><?php
                        if ($_SESSION['user']['Perm.FAVORITE_GAME']==1) {
                            echo "<span class=\"glyphicon glyphicon-ok\" aria-hidden=\"true\"></span>";
                        } else {
                            echo "<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\"></span>";
                        }
                        ?></td>
                    <td>
                        <form action="#" method="post">
                            <input type="text" style="display: none;" class="form-control" name="modifyTelemetryFavGame" value="<?php
                            if ($_SESSION['user']['Perm.FAVORITE_GAME']==1) {
                                echo "0";
                            } else {
                                echo "1";
                            }
                            ?>">
                            <button type="submit" class="btn btn-success"><?php
                                if ($_SESSION['user']['Perm.FAVORITE_GAME']==1) {
                                    echo "Désactiver";
                                } else {
                                    echo "Activer";
                                }
                                ?></button>
                        </form>
                    </td>
                </tr>
                <tr>
                    <td>Votre nombre de messages postés</td>
                    <td><?php
                        if ($_SESSION['user']['Perm.NUMBER_OF_MESSAGE']==1) {
                            echo "<span class=\"glyphicon glyphicon-ok\" aria-hidden=\"true\"></span>";
                        } else {
                            echo "<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\"></span>";
                        }
                        ?></td>
                    <td>
                        <form action="#" method="post">
                            <input type="text" style="display: none;" class="form-control" name="modifyTelemetryNumberOfMessage" value="<?php
                            if ($_SESSION['user']['Perm.NUMBER_OF_MESSAGE']==1) {
                                echo "0";
                            } else {
                                echo "1";
                            }
                            ?>">
                            <button type="submit" class="btn btn-success"><?php
                                if ($_SESSION['user']['Perm.NUMBER_OF_MESSAGE']==1) {
                                    echo "Désactiver";
                                } else {
                                    echo "Activer";
                                }
                                ?></button>
                        </form>
                    </td>
                </tr>
                <tr>
                    <td>Votre temps d'écriture de message</td>
                    <td><?php
                        if ($_SESSION['user']['Perm.TYPING_TIME']==1) {
                            echo "<span class=\"glyphicon glyphicon-ok\" aria-hidden=\"true\"></span>";
                        } else {
                            echo "<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\"></span>";
                        }
                        ?></td>
                    <td>
                        <form action="#" method="post">
                            <input type="text" style="display: none;" class="form-control" name="modifyTelemetryWriteTime" value="<?php
                            if ($_SESSION['user']['Perm.TYPING_TIME']==1) {
                                echo "0";
                            } else {
                                echo "1";
                            }
                            ?>">
                            <button type="submit" class="btn btn-success"><?php
                                if ($_SESSION['user']['Perm.TYPING_TIME']==1) {
                                    echo "Désactiver";
                                } else {
                                    echo "Activer";
                                }
                                ?></button>
                        </form>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>