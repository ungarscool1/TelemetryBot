package tk.ungarscool1.bot.telemetry.connector;

import java.awt.Color;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserStatus;

import tk.ungarscool1.bot.telemetry.Main;
import tk.ungarscool1.bot.telemetry.entities.Permissions;
import tk.ungarscool1.bot.telemetry.entities.Person;

public class Connector {
	
	private static MessageBuilder messageBuilder = new MessageBuilder();
	private static User[] users = Main.users;
	private static HashMap<User, Person> persons = Main.persons;
    
	public static void sendMessage(String pass, String userName) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Votre mot de passe");
		embed.addField("Mot de passe", pass);
		embed.addField("Temporaire ?", "Oui");
		embed.setUrl("https://telemetry.ungarscool1.tk/connect");
		long timestamp = System.currentTimeMillis() / 1000L;
		long perime = timestamp+300;
		Date expiry = new Date(perime * 1000);
		embed.addField("Se périme le ", expiry.toLocaleString());
		embed.setColor(Color.MAGENTA);
		embed.setFooter("Telemetry 2.0");
		for (int i = 0; i < users.length; i++) {
			System.out.println("Je suis l.25 id "+users[i].getId()+"\nEst-ce qu'on le cherche ?"+(users[i].getIdAsString().equals(userName)));
			if (users[i].getIdAsString().equals(userName)) {
				messageBuilder.setEmbed(embed).send(users[i]).join();
				return;
			}
		}
	}
	
	public static void sendStats() {
		for(int i = 0; i < users.length; i++) {
			if(persons.get(users[i]).doesAcceptTelemetry()) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle("Vos statistiques").setFooter("Telemetry 2.0").setColor(Color.LIGHT_GRAY);
				StringBuilder perms = new StringBuilder();
				if (persons.get(users[i]).doesAcceptPerm(Permissions.AVERAGE_CONNECTED_TIME)) {
					perms.append("- Votre temps de connexion par jours\n");
					embed.addField("Nombre de connexion", persons.get(users[i]).getNumberOfConnection()+"");
					embed.addField("Temps de connexion", persons.get(users[i]).getTimeOfConnection());
				}
				if (persons.get(users[i]).doesAcceptPerm(Permissions.AVERAGE_PLAY_TIME)) {
					perms.append("- Votre temps de jeu en moyenne par jours\n");
					embed.addField("Temps de jeu", persons.get(users[i]).getPlayTime());
				}
				if (persons.get(users[i]).doesAcceptPerm(Permissions.FAVORITE_CHANNEL)) {
					perms.append("- Votre canal préféré\n");
					embed.addField("Votre cannal préféré", persons.get(users[i]).getFavoriteChannel());
				}
				if (persons.get(users[i]).doesAcceptPerm(Permissions.FAVORITE_GAME)) {
					perms.append("- Votre jeu préféré\n");
					embed.addField("Jeu préféré", persons.get(users[i]).getFavoriteGame());
				}
				if (persons.get(users[i]).doesAcceptPerm(Permissions.NUMBER_OF_MESSAGE)) {
					perms.append("- Votre nombre de messages postés\n");
					embed.addField("Nombre de messages envoyé", persons.get(users[i]).getNumbreOfMessage()+"");
				}
				
				embed.addField("Telemetry 2.0 peut récolter", "```css\n"
						+ perms.toString()
						+ "\n```");
			}
		}
	}
	
	public static void updateTelemetryInfo(String userId) {
		System.out.println(userId+ " c'est id");
		for (int i = 0; i < users.length; i++) {
			if (users[i].getIdAsString().equals(userId)) {
				persons.replace(users[i], new Person(users[i]));
			}
		}
	}
	
	public static void syncToDB() {
		for (int i = 0; i < users.length; i++) {
			long timestamp = System.currentTimeMillis() / 1000L;
			if (persons.get(users[i]).doesAcceptPerm(Permissions.AVERAGE_CONNECTED_TIME)) {
				if (!users[i].getStatus().equals(UserStatus.OFFLINE)) {
					persons.get(users[i]).setOfflineTime(timestamp);
					persons.get(users[i]).setOnlineTime(timestamp);
				}
			}
				persons.get(users[i]).update();
		}
		System.out.println("Les utilisateurs ont été sync à la base de données");
	}
	
}
