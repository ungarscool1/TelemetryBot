package tk.ungarscool1.bot.telemetry.connector;

import java.awt.Color;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserStatus;

import tk.ungarscool1.bot.telemetry.Main;
import tk.ungarscool1.bot.telemetry.entities.Permissions;
import tk.ungarscool1.bot.telemetry.entities.Person;

public class Connector {
	
	private static MessageBuilder messageBuilder = new MessageBuilder();
	private static List<User> users = Main.users;
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
		for (int i = 0; i < users.size(); i++) {
			System.out.println("Je suis l.25 id "+users.get(i).getId()+"\nEst-ce qu'on le cherche ?"+(users.get(i).getIdAsString().equals(userName)));
			if (users.get(i).getIdAsString().equals(userName)) {
				messageBuilder.setEmbed(embed).send(users.get(i)).join();
				return;
			}
		}
	}
	
	public static void sendStats() {
		for(int i = 0; i < users.size(); i++) {
			if(persons.get(users.get(i)).doesAcceptTelemetry()) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle("Vos statistiques").setFooter("Telemetry 2.0").setColor(Color.LIGHT_GRAY);
				if (persons.get(users.get(i)).doesAcceptPerm(Permissions.AVERAGE_CONNECTED_TIME)) {
					embed.addField("Nombre de connexion", persons.get(users.get(i)).getNumberOfConnection()+"");
					embed.addField("Temps de connexion", persons.get(users.get(i)).getTimeOfConnection());
				}
				if (persons.get(users.get(i)).doesAcceptPerm(Permissions.AVERAGE_PLAY_TIME)) {
					embed.addField("Temps de jeu", persons.get(users.get(i)).getPlayTime());
				}
				if (persons.get(users.get(i)).doesAcceptPerm(Permissions.FAVORITE_CHANNEL)) {
					embed.addField("Votre cannal préféré", persons.get(users.get(i)).getFavoriteChannel());
				}
				if (persons.get(users.get(i)).doesAcceptPerm(Permissions.FAVORITE_GAME)) {
					embed.addField("Jeu préféré", persons.get(users.get(i)).getFavoriteGame());
				}
				if (persons.get(users.get(i)).doesAcceptPerm(Permissions.NUMBER_OF_MESSAGE)) {
					embed.addField("Nombre de messages envoyé", persons.get(users.get(i)).getNumbreOfMessage()+"");
				}
				
				embed.addField("Telemetry 2.0 peut récolter", persons.get(users.get(i)).getPerms());
			}
		}
	}
	
	public static void updateTelemetryInfo(String userId) {
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getIdAsString().equals(userId)) {
				persons.replace(users.get(i), new Person(users.get(i)));
			}
		}
	}
	
	public static void syncToDB() {
		for (int i = 0; i < users.size(); i++) {
			long timestamp = System.currentTimeMillis() / 1000L;
			if (persons.get(users.get(i)).doesAcceptPerm(Permissions.AVERAGE_CONNECTED_TIME)) {
				if (!users.get(i).getStatus().equals(UserStatus.OFFLINE)) {
					persons.get(users.get(i)).setOfflineTime(timestamp);
					persons.get(users.get(i)).setOnlineTime(timestamp);
				}
			}
				persons.get(users.get(i)).update();
		}
		System.out.println("Les utilisateurs ont été sync à la base de données");
	}
	
}
