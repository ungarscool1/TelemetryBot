package tk.ungarscool1.bot.telemetry;

import java.awt.Color;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.Activity;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserStatus;

import tk.ungarscool1.bot.telemetry.connector.AutoSaver;
import tk.ungarscool1.bot.telemetry.connector.Connector;
import tk.ungarscool1.bot.telemetry.connector.SendStats;
import tk.ungarscool1.bot.telemetry.connector.Socket;
import tk.ungarscool1.bot.telemetry.entities.Permissions;
import tk.ungarscool1.bot.telemetry.entities.Person;
import tk.ungarscool1.bot.telemetry.logging.Logging;
import tk.ungarscool1.cachet2bot.Stats;
import tk.ungarscool1.cachet2bot.Status;

public class Main {

	private static String version = "";
	public static List<User> users = new ArrayList<>();
	public static Server[] servers;
	public static HashMap<User, Person> persons = new HashMap<>();
	public long betweenStartAndNow = ChronoUnit.DAYS.between(LocalDate.of(2018, Month.DECEMBER, 20), LocalDate.now());
	public static Logging logs = new Logging();
	
	public static void main(String[] args) {
		logs.addEvent("Lancement du bot");
		Socket socket = new Socket();
		socket.start();
		String[] statsCred = {"true", "YOUR_API_KEY", "YOUR_METRIC_URL", "3", "3"};
		Stats cachet = new Stats(statsCred);
		SendStats sendStats = new SendStats();
		sendStats.start();
		AutoSaver autoSaver = new AutoSaver();
		autoSaver.start();
		new DiscordApiBuilder().setToken("YOUR_TOKEN").login().thenAccept((DiscordApi api) -> {
			cachet.setBotStatus(Status.OPERATIONAL);
			
			api.addServerMemberJoinListener(event -> {
				logs.addEvent(event.getUser().getName() + " vient de rejoindre le serveur " + event.getServer().getName());
				cachet.sendStats();
				users.add(event.getUser());
				persons.put(event.getUser(), new Person(event.getUser()));
			});
			
			api.addUserStartTypingListener(event -> {
				logs.addEvent(event.getUser().getName() + " a commencé à écrire...");
				if (persons.get(event.getUser()).doesAcceptTelemetry()&&persons.get(event.getUser()).doesAcceptPerm(Permissions.TYPING_TIME)) {
					cachet.sendStats();
					if (event.getServerTextChannel().isPresent()) {
						persons.get(event.getUser()).startType();
					}
					
				}
				
			});	
			
			api.addUserChangeStatusListener(event -> {
				cachet.sendStats();
				if (persons.get(event.getUser()).doesAcceptTelemetry()) {
					User user = event.getUser();
					long timestamp = System.currentTimeMillis() / 1000L;
					if(persons.get(user).doesAcceptPerm(Permissions.AVERAGE_CONNECTED_TIME)) {
						if (event.getOldStatus().equals(UserStatus.OFFLINE)) {
							persons.get(user).addNumberOfConnection();
							persons.get(user).setOnlineTime(timestamp);
						}
						if (event.getNewStatus().equals(UserStatus.OFFLINE)) {
							persons.get(user).setOfflineTime(timestamp);
						}
					}
					persons.get(user).update();
				}
				
				logs.addEvent(event.getUser().getName() + " est passer de "+event.getOldStatus().getStatusString() + " à " + event.getNewStatus().getStatusString());
				
			});
			
			
			api.addUserChangeActivityListener(event -> {
				cachet.sendStats();
				User user = event.getUser();
				long timestamp = System.currentTimeMillis() / 1000L;
				Optional<Activity> old = event.getOldActivity();
				Optional<Activity> newest = event.getNewActivity();

				if (persons.get(user).doesAcceptTelemetry()) {
					if (persons.get(user).doesAcceptPerm(Permissions.AVERAGE_PLAY_TIME)||persons.get(user).doesAcceptPerm(Permissions.FAVORITE_GAME)) {
						if (old.isPresent()) {
							if (old.get().getType().equals(ActivityType.PLAYING)) {
								persons.get(user).setEndPlay(timestamp, old.get().getName());
								logs.addEvent(user.getName() + " joué à " + newest.get().getName());
							}
							
						}
						if (newest.isPresent()) {
							if (newest.get().getType().equals(ActivityType.PLAYING)) {
								persons.get(user).setStartPlay(timestamp);
								logs.addEvent(user.getName() + " joue à " + newest.get().getName());
							}
						}
					}
					if (persons.get(user).doesAcceptPerm(Permissions.AVERAGE_LISTEN_TIME)) {
						if (old.isPresent()) {
							if (old.get().getType().equals(ActivityType.LISTENING)) {
								logs.addEvent(user.getName() + " n'écoute plus de musique !");
								persons.get(user).stopListen();
							}
						}
						if (newest.isPresent()) {
							if (newest.get().getType().equals(ActivityType.LISTENING)) {
								logs.addEvent(user.getName() + " écoute " + newest.get().getDetails().get() + " de " + newest.get().getState().get() + " sur Spotify");
								persons.get(user).startListen(newest.get().getDetails().get() + " , " + newest.get().getState().get());
							}
						}
					}
					persons.get(user).update();
				}
			});
			
			
			api.addMessageCreateListener(event -> {
				cachet.sendStats();
				if (event.getMessageAuthor().isYourself()) return;
				Server msgSrv = event.getServer().get();
				String message = event.getMessageContent();
				EmbedBuilder embed = new EmbedBuilder().setFooter("Telemetry 2.0 - "+version);
				User author = event.getMessageAuthor().asUser().get();
				
				
				if (persons.get(author).doesAcceptTelemetry()) {
					if (persons.get(author).doesAcceptPerm(Permissions.TYPING_TIME)) {
						persons.get(author).stopType();
					}
					if (persons.get(author).doesAcceptPerm(Permissions.NUMBER_OF_MESSAGE)) {
						persons.get(author).addMessageToCount(msgSrv.getChannelById(event.getChannel().getIdAsString()).get().getName());
					}
				}
				
				
				logs.addEvent(author.getName() + " a écrit un message sur le serveur " + msgSrv.getName() + " -> " + msgSrv.getChannelById(event.getChannel().getIdAsString()).get().getName());
				
				
				if (message.contains(".telemetry")) {
					if (persons.get(author).doesAcceptTelemetry()) {
						if(message.contains("off")) {
							embed.setTitle("Télémetrie désactivée").setDescription("Vous venez de désactivée le système de télémetrie, pour vous. \n"
									+ "Le bot ne récoltera plus aucune information de vous a part les messages ainsi que les modifications").setColor(Color.GREEN);
							persons.get(author).setTelemetry(false);
						} else if (message.contains("on")) {
							embed.setTitle("Oppération impossible").setDescription("L'oppération demandé ne peut être effectué.\n"
									+ "Vous ne pouvez pas activer une option déjà active.").setColor(Color.RED);
						} else {
							embed.setTitle("Vos données télémétrique").addField("Votre identifiant", author.getIdAsString()).setColor(Color.GREEN);
							if (persons.get(author).doesAcceptPerm(Permissions.AVERAGE_CONNECTED_TIME)) {
								embed.addField("Nombre de connexion", persons.get(author).getNumberOfConnection()+"");
								embed.addField("Temps de connexion", persons.get(author).getTimeOfConnection());
							}
							if (persons.get(author).doesAcceptPerm(Permissions.AVERAGE_PLAY_TIME)) {
								embed.addField("Temps de jeu", persons.get(author).getPlayTime());
							}
							if (persons.get(author).doesAcceptPerm(Permissions.AVERAGE_LISTEN_TIME)) {
								embed.addField("Temps d'écoute", persons.get(author).getListenTime());
								if (persons.get(author).doesAcceptPerm(Permissions.FAVORITE_MUSIC)) {
									embed.addField("Votre musique préféré", persons.get(author).getFavoriteMusic());
								}
								if (persons.get(author).doesAcceptPerm(Permissions.FAVORITE_MUSIC_ALBUM)) {
									embed.addField("Votre album préféré", "Non implémenté !");
								}
								if (persons.get(author).doesAcceptPerm(Permissions.FAVORITE_MUSIC_ARTIST)) {
									embed.addField("Votre artiste préféré", "Non implémenté !");
								}
							}
							if (persons.get(author).doesAcceptPerm(Permissions.FAVORITE_CHANNEL)) {
								embed.addField("Votre cannal préféré", persons.get(author).getFavoriteChannel());
							}
							if (persons.get(author).doesAcceptPerm(Permissions.FAVORITE_GAME)) {
								embed.addField("Jeu préféré", persons.get(author).getFavoriteGame());
							}
							if (persons.get(author).doesAcceptPerm(Permissions.NUMBER_OF_MESSAGE)) {
								embed.addField("Nombre de messages envoyé", persons.get(author).getNumbreOfMessage()+"");
							}
							if (persons.get(author).doesAcceptPerm(Permissions.TYPING_TIME)) {
								embed.addField("Temps total d'écriture", persons.get(author).getTypingTime());
								embed.addField("Temps d'écriture moyen", persons.get(author).getAverageTypingTime());
							}
							embed.addField(api.getYourself().getName() + " peut récolter", persons.get(author).getPerms());
							embed.addField("Dernière synchronisation avec la base de donnée", persons.get(author).getLastSync().toString());
							embed.addField("Paramètres", "https://telemetry.ungarscool1.tk/connect/?user="+author.getId());
						}
					} else {
						if(message.contains("on")) {
							embed.setTitle("Télémetrie activée").setDescription("Vous venez de activer le système de télémetrie, pour vous. \n"
									+ "Le bot récoltera les informations que vous avez accorder à la récolte.\n"
									+ "Si vous n'avez jamais activer la télémétrie vous pouvez régler vos paramètres sur\n"
									+ "https://telemetry.ungarscool1.tk/account/?user="+author.getId()).setColor(Color.GREEN);
							persons.get(author).setTelemetry(true);
						} else if (message.contains("off")) {
							embed.setTitle("Oppération impossible").setDescription("L'oppération demandé ne peut être effectué.\n"
									+ "Vous ne pouvez pas désactiver une option déjà innactive.").setColor(Color.RED);
						} else {
							embed.setTitle("Vos données télémétrique").setColor(Color.GREEN);
							embed.addField("Votre identifiant", author.getIdAsString());
							embed.addField("Etat de la télémétrie", "Désactiver");
							embed.addField("Paramètres", "https://telemetry.ungarscool1.tk/connect/?user="+author.getId());
						}
					}
					event.getChannel().sendMessage(embed);
				}			
				
				if (message.contains("..update")&&author.getId()==113616829481484288L) {
					if (message.length()==8) {
						for (int i = 0; i < users.size(); i++) {
							persons.get(users.get(i)).close();
							persons.remove(users.get(i));
							persons.put(users.get(i), new Person(users.get(i)));
							logs.addEvent("Le profile de " + users.get(i).getDiscriminatedName() + " a été mis à jour");
						}
					} else {
						if (message.contains(" ")) {
							String id = message.substring(message.indexOf(" "), message.length());
							for (int i = 0; i < users.size(); i++) {
								if (users.get(i).getIdAsString().equals(id)) {
									persons.get(users.get(i)).close();
									persons.replace(users.get(i), new Person(users.get(i)));
								}
							}
						}
					}
				}
				
				if(message.equals("..sendStats")) {
					Connector.sendStats();
				}
				
				
			});
			
			servers = api.getServers().toArray(new Server[0]);
			for (int i = 0; i < servers.length; i++) {
				User[] tempUser = servers[i].getMembers().toArray(new User[0]);
				logs.addEvent("Je parcours le serveur "+servers[i].getName());
				for (int j = 0; j < tempUser.length; j++) {
					if (!users.contains(tempUser[j])) {
						long timestamp = System.currentTimeMillis() / 1000L;
						users.add(tempUser[j]);
						persons.put(tempUser[j], new Person(tempUser[j]));
						if (!tempUser[j].getStatus().equals(UserStatus.OFFLINE)) {
							persons.get(tempUser[j]).setOnlineTime(timestamp);
						}
						if (tempUser[j].getActivity().isPresent()) {
							Activity activity = tempUser[j].getActivity().get();
							if (activity.equals(ActivityType.PLAYING)) {
								persons.get(tempUser[j]).setStartPlay(timestamp);
							} else if (activity.equals(ActivityType.LISTENING)) {
								persons.get(tempUser[j]).startListen(activity.getDetails().get() + " , " + activity.getState().get());
							}
						}
						logs.addEvent(tempUser[j].getDiscriminatedName()+" a été sync au bot");
					} else {
						logs.addEvent(tempUser[j].getDiscriminatedName()+" a été ignoré car il est déjà présent");
					}
				}
			}
			
		});
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			
			public void run() {
				for (int i = 0; i < users.size(); i++) {
					long timestamp = System.currentTimeMillis() / 1000L;
					if (persons.get(users.get(i)).doesAcceptPerm(Permissions.AVERAGE_CONNECTED_TIME)) {
						if (!users.get(i).getStatus().equals(UserStatus.OFFLINE)) {
							persons.get(users.get(i)).setOfflineTime(timestamp);
						}
					}
					persons.get(users.get(i)).update();
					persons.get(users.get(i)).close();
					logs.addEvent(users.get(i).getName() + " vient d'être synchroniser à la DB");
				}
				logs.addEvent("Extinction du bot");
				cachet.setBotStatus(Status.MAJOR_OUTAGE);
			}
			
		});
		
	}
}