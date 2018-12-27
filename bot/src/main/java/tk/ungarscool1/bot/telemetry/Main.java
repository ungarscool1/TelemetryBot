package tk.ungarscool1.bot.telemetry;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.Activity;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.event.user.UserChangeActivityEvent;
import org.javacord.api.listener.user.UserChangeActivityListener;

import tk.ungarscool1.bot.telemetry.connector.AutoSaver;
import tk.ungarscool1.bot.telemetry.connector.SendStats;
import tk.ungarscool1.bot.telemetry.connector.Socket;
import tk.ungarscool1.bot.telemetry.entities.Permissions;
import tk.ungarscool1.bot.telemetry.entities.Person;
import tk.ungarscool1.bot.telemetry.logging.Logging;

public class Main {

	private static String version = "";
	public static User[] users;
	public static Server[] servers;
	public static HashMap<User, Person> persons = new HashMap<>();
	public long betweenStartAndNow = ChronoUnit.DAYS.between(LocalDate.of(2018, Month.DECEMBER, 20), LocalDate.now());
	
	public static void main(String[] args) {
		Socket socket = new Socket();
		socket.start();
		Logging logging = new Logging();
		SendStats sendStats = new SendStats();
		sendStats.start();
		AutoSaver autoSaver = new AutoSaver();
		autoSaver.start();
		new DiscordApiBuilder().setToken("NTI0NjM2NjExNTMxNzY3ODA5.DvrOKQ.c3Z2f5liKkrHv61TrK9gCGHPaOg").login().thenAccept((DiscordApi api) -> {
			
			
			api.addServerMemberJoinListener(event -> {
				users[users.length] = event.getUser();
				persons.put(event.getUser(), new Person(event.getUser()));
			});
						
			
			api.addUserChangeStatusListener(event -> {
				
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
				
				//logging.addEvent(event.getUser().getName(), "a est passer de "+event.getOldStatus().getStatusString()+ " � "+event.getNewStatus().getStatusString());
				
			});
			
			
			api.addUserChangeActivityListener(event -> {
				User user = event.getUser();
				long timestamp = System.currentTimeMillis() / 1000L;
				Optional<Activity> old = event.getOldActivity();
				Optional<Activity> newest = event.getNewActivity();
				if (persons.get(user).doesAcceptTelemetry()) {
					if (persons.get(user).doesAcceptPerm(Permissions.AVERAGE_PLAY_TIME)||persons.get(user).doesAcceptPerm(Permissions.FAVORITE_GAME)) {
						if (newest.isPresent()) {
							if (!newest.get().getType().equals(ActivityType.LISTENING)&&!newest.get().getType().equals(ActivityType.WATCHING)&&!newest.get().getType().equals(ActivityType.STREAMING)) {
								persons.get(user).setStartPlay(timestamp);
							}
						}
						if (old.isPresent()) {
							if (!old.get().getType().equals(ActivityType.LISTENING)&&!old.get().getType().equals(ActivityType.WATCHING)&&!old.get().getType().equals(ActivityType.STREAMING)) {
								persons.get(user).setEndPlay(timestamp, old.get().getName());
							}
							
						}
					}
					persons.get(user).update();
				}
			});
			
			
			api.addMessageCreateListener(event -> {
				Server msgSrv = event.getServer().get();
				String message = event.getMessageContent();
				EmbedBuilder embed = new EmbedBuilder().setFooter("Telemetry 2.0 - "+version);
				User author = event.getMessageAuthor().asUser().get();
				
				if (event.getMessageAuthor().isYourself()) return;
				if (persons.get(author).doesAcceptTelemetry()&&persons.get(author).doesAcceptPerm(Permissions.NUMBER_OF_MESSAGE)) {
					persons.get(author).addMessageToCount(msgSrv.getChannelById(event.getChannel().getIdAsString()).get().getName());
				}
				
				
				//logging.addEvent(author.getName(), "a �crit un message sur le serveur "+msgSrv.getName()+ " -> "+msgSrv.getChannelById(event.getChannel().getIdAsString()).get().getName());
				
				
				if (message.contains(".telemetry")) {
					if (persons.get(author).doesAcceptTelemetry()) {
						if(message.contains("off")) {
							embed.setTitle("T�l�metrie d�sactiv�e").setDescription("Vous venez de d�sactiv�e le syst�me de t�l�metrie, pour vous. \n"
									+ "Le bot ne r�coltera plus aucune information de vous a part les messages ainsi que les modifications").setColor(Color.GREEN);
							persons.get(author).setTelemetry(false);
						} else if (message.contains("on")) {
							embed.setTitle("Opp�ration impossible").setDescription("L'opp�ration demand� ne peut �tre effectu�.\n"
									+ "Vous ne pouvez pas activer une option d�j� active.").setColor(Color.RED);
						} else {
							embed.setTitle("Vos donn�es t�l�m�trique").addField("Votre identifiant", author.getIdAsString()).setColor(Color.GREEN);
							StringBuilder perms = new StringBuilder();
							if (persons.get(author).doesAcceptPerm(Permissions.AVERAGE_CONNECTED_TIME)) {
								perms.append("+ ");
								embed.addField("Nombre de connexion", persons.get(author).getNumberOfConnection()+"");
								embed.addField("Temps de connexion", persons.get(author).getTimeOfConnection());
							} else {
								perms.append("- ");
							}
							perms.append("Votre temps de connexion par jours\n");
							if (persons.get(author).doesAcceptPerm(Permissions.AVERAGE_PLAY_TIME)) {
								perms.append("+ ");
								embed.addField("Temps de jeu", persons.get(author).getPlayTime());
							} else {
								perms.append("- ");
							}
							perms.append("Votre temps de jeu en moyenne par jours\n");
							if (persons.get(author).doesAcceptPerm(Permissions.FAVORITE_CHANNEL)) {
								perms.append("+ ");
								embed.addField("Votre cannal pr�f�r�", persons.get(author).getFavoriteChannel());
							} else {
								perms.append("- ");
							}
							perms.append("Votre cannal pr�f�r�\n");
							if (persons.get(author).doesAcceptPerm(Permissions.FAVORITE_GAME)) {
								perms.append("+ ");
								embed.addField("Jeu pr�f�r�", persons.get(author).getFavoriteGame());
							} else {
								perms.append("- ");
							}
							perms.append("Votre jeu pr�f�r�\n");
							if (persons.get(author).doesAcceptPerm(Permissions.NUMBER_OF_MESSAGE)) {
								perms.append("+ ");
								embed.addField("Nombre de messages envoy�", persons.get(author).getNumbreOfMessage()+"");
							} else {
								perms.append("- ");
							}
							perms.append("Votre nombre de messages post�s\n");
							embed.addField(api.getYourself().getName() + " peut r�colter", "```diff\n"
									+ perms.toString()
									+ "\n```");
							embed.addField("Param�tres", "https://telemetry.ungarscool1.tk/connect/?user="+author.getId());
						}
					} else {
						if(message.contains("on")) {
							embed.setTitle("T�l�metrie activ�e").setDescription("Vous venez de activer le syst�me de t�l�metrie, pour vous. \n"
									+ "Le bot r�coltera les informations que vous avez accorder � la r�colte.\n"
									+ "Si vous n'avez jamais activer la t�l�m�trie vous pouvez r�gler vos param�tres sur\n"
									+ "https://telemetry.ungarscool1.tk/account/?user="+author.getId()).setColor(Color.GREEN);
							persons.get(author).setTelemetry(true);
						} else if (message.contains("off")) {
							embed.setTitle("Opp�ration impossible").setDescription("L'opp�ration demand� ne peut �tre effectu�.\n"
									+ "Vous ne pouvez pas d�sactiver une option d�j� innactive.").setColor(Color.RED);
						} else {
							embed.setTitle("Vos donn�es t�l�m�trique").setColor(Color.GREEN);
							embed.addField("Votre identifiant", author.getIdAsString());
							embed.addField("Etat de la t�l�m�trie", "D�sactiver");
							embed.addField("Param�tres", "https://telemetry.ungarscool1.tk/connect/?user="+author.getId());
						}
					}
					event.getChannel().sendMessage(embed);
				}
				
				if (message.equals("..setup")&&author.getIdAsString().equals("113616829481484288")) {
					for (int i = 0; i < users.length; i++) {
						EmbedBuilder temp = new EmbedBuilder();
						StringBuilder perms = new StringBuilder();
						temp.setTitle("Le syst�me de t�l�m�trie � �t� ajout� sur les serveurs o� vous �tes pr�sents !");
						String telemetry = "D�sactiv�";
						if (persons.get(users[i]).doesAcceptTelemetry()) {
							telemetry = "Activ�";
							if (persons.get(author).doesAcceptPerm(Permissions.AVERAGE_CONNECTED_TIME)) {
								perms.append("+ ");
							} else {
								perms.append("- ");
							}
							perms.append("Votre temps de connexion par jours\n");
							if (persons.get(author).doesAcceptPerm(Permissions.AVERAGE_PLAY_TIME)) {
								perms.append("+ ");
							} else {
								perms.append("- ");
							}
							perms.append("Votre temps de jeu en moyenne par jours\n");
							if (persons.get(author).doesAcceptPerm(Permissions.FAVORITE_CHANNEL)) {
								perms.append("+ ");
							} else {
								perms.append("- ");
							}
							perms.append("Votre cannal pr�f�r�\n");
							if (persons.get(author).doesAcceptPerm(Permissions.FAVORITE_GAME)) {
								perms.append("+ ");
							} else {
								perms.append("- ");
							}
							perms.append("Votre jeu pr�f�r�\n");
							if (persons.get(author).doesAcceptPerm(Permissions.NUMBER_OF_MESSAGE)) {
								perms.append("+ ");
							} else {
								perms.append("- ");
							}
							perms.append("Votre nombre de messages post�s\n");
						} else {
							perms.append("Rien ne peut �tre r�colter sur vous");
						}
						temp.addField("Etat de la t�l�m�trie", telemetry);
						temp.addField(api.getYourself().getName()+" peut r�colter", "```diff\n"+perms.toString()+"\n```");
						try {
							temp.addField("Param�tres", "Site non actif, pour faire des modifications de permissions merci de contacter "+ api.getOwner().get().getDiscriminatedName());
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						temp.setFooter(api.getYourself().getName() + version);
						users[i].sendMessage(temp);
					}
				}
				
				
				if (message.contains("..update")&&author.getId()==113616829481484288L) {
					if (message.length()==8) {
						for (int i = 0; i < users.length; i++) {
							System.out.println("Index "+i+" : "+users[i].getDiscriminatedName());
							persons.get(users[i]).close();
							persons.remove(users[i]);
							persons.put(users[i], new Person(users[i]));
							System.out.println("J'ai reload "+users[i].getDiscriminatedName());
						}
					} else {
						if (message.contains(" ")) {
							String id = message.substring(message.indexOf(" "), message.length());
							for (int i = 0; i < users.length; i++) {
								if (users[i].getIdAsString().equals(id)) {
									persons.get(users[i]).close();
									persons.replace(users[i], new Person(users[i]));
								}
							}
						}
					}
				}
				
				
			});
			
			servers = api.getServers().toArray(new Server[0]);
			int totalpeople = 0;
			for (int i = 0; i < servers.length; i++) {
				totalpeople+=servers[i].getMemberCount();
			}
			
			users = new User[totalpeople-8];
			System.out.println(totalpeople);
			for (int i = 0; i < servers.length; i++) {
				User[] tempUser = servers[i].getMembers().toArray(new User[0]);
				System.out.println("Je parcours le serveur "+servers[i].getName());
				for (int j = 0; j < tempUser.length; j++) {
					long timestamp = System.currentTimeMillis() / 1000L;
					users[j] = tempUser[j];
					persons.put(users[j], new Person(users[j]));
					if (!users[i].getStatus().equals(UserStatus.OFFLINE)) {
						persons.get(users[i]).setOnlineTime(timestamp);
					}
					System.out.println("J'ai ajout� "+tempUser[j].getDiscriminatedName()+" � la liste users");
				}
			}
			
			System.out.println("Utilisateur � l'index 0 "+users[0].getDiscriminatedName());
			
		});
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			
			public void run() {
				for (int i = 0; i < users.length; i++) {
					long timestamp = System.currentTimeMillis() / 1000L;
					if (persons.get(users[i]).doesAcceptPerm(Permissions.AVERAGE_CONNECTED_TIME)) {
						if (!users[i].getStatus().equals(UserStatus.OFFLINE)) {
							persons.get(users[i]).setOfflineTime(timestamp);
						}
					}
					persons.get(users[i]).update();
					persons.get(users[i]).close();
				}
				System.out.println("Tout est sync");
			}
			
		});
		
	}
}