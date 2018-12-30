package tk.ungarscool1.bot.telemetry.entities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.javacord.api.entity.user.User;


public class Person {

	
	private User discordUser;
	private boolean acceptTelemetry;
	private HashMap<Permissions, Integer> perms = new HashMap<>();
	private long numberOfMessages;
	private long NumberOfConnection;
	private long timeOfConnection;
	private long playTime;
	private HashMap<String, Long> games = new HashMap<>();
	private HashMap<String, Long> channels = new HashMap<>();
	private long typingTime;
	
	// Connection var
	
	private long onlineTimeStamp;
	private long offlineTimeStamp;
	
	// Play var
	
	private long startPlay;
	private long endPlay;
	
	// Type var
	
	private long startType, endType;
	
	// SQL var
	
	private Connection sql;
	private boolean sqlConnected = false;
	
	public Person(User user) {
		this.discordUser = user;
		try {
            sql = DriverManager.getConnection("jdbc:mysql://localhost:3306/telemetry", "root", "");
            sqlConnected = true;
        } catch (Exception e) {
            System.err.println("Impossible de se connecté à la base de donnée");
            sqlConnected = false;
        }
		if (sqlConnected) {
			if (SQL_UserExist()) {
				SQL_Perm();
				SQL_Games();
				SQL_Channels();
			} else {
				SQL_AddUser();
				SQL_Perm();
			}
		}
	}
	
	
	
	public boolean doesAcceptTelemetry() {
		return this.acceptTelemetry;
	}
	
	public boolean doesAcceptPerm(Permissions permission) {
		return (perms.get(permission)==1);
	}
	
	public long getNumberOfConnection() {
		return this.NumberOfConnection;
	}
	
	public void setOnlineTime(long unixTime) {
		this.onlineTimeStamp = unixTime;
	}	
	
	public void setOfflineTime(long unixTime) {
		this.offlineTimeStamp = unixTime;
		if (this.onlineTimeStamp!=0) {
			long diff = this.offlineTimeStamp - this.onlineTimeStamp;
			this.timeOfConnection += diff;
		}
	}
	
	public void setStartPlay(long unixTime) {
		this.startPlay = unixTime;
	}
	
	public void setEndPlay(long unixTime, String gameName) {
		this.endPlay = unixTime;
		if (this.startPlay!=0) {
			long diff = this.endPlay - this.startPlay;
			
			if (games.get(gameName)!=null) {
				long time = games.get(gameName) + diff;
				games.remove(gameName);
				games.put(gameName, time);
				SQL_SyncGame();
			} else {
				games.put(gameName, diff);
				SQL_AddGame(gameName);
			}
			
			this.playTime += diff;
		}
	}
	
	public void startType() {
		this.startType = System.currentTimeMillis() / 1000L;
	}
	public void stopType() {
		this.endType = System.currentTimeMillis() / 1000L;
		long diff = this.endType - this.startType;
		this.typingTime+=diff;
	}
	
	public String getTypingTime() {
		StringBuilder result = new StringBuilder();
		
		double time = typingTime / 3600d; // Fait des heures
		if ((int)time>0) {
			
			result.append((int)time+"h ");
			time -= (int) time; // Retire la partie entière
		}
		
		time *= 60d; // Fait des minutes
		
		if ((int)time>0) {
			result.append((int)time+ "m ");
			time -= (int) time; // Retire la partie entière
		}
		
		time *= 60d; // Fait des secondes
		if ((long)time>0) {
			result.append((long)time+ "s ");
			time -= (long) time; // Retire la partie entière
		}
		if (result.length()==0) {
			result.append("Vous n'avez aucune connexion");
		}
		return result.toString();
	}
	
	public String getAverageTypingTime() {
		StringBuilder result = new StringBuilder();
		
		double moy = (this.typingTime * 1d) / (this.numberOfMessages * 1d);
		result.append(String.format("%.2f", moy));
		result.append("s/msg");
		
		return result.toString();
	}
	
	public String getFavoriteGame() {
		long better = 0;
		String name = "Pas de jeu favori";
		for (Map.Entry game : games.entrySet()) {
			if((long) game.getValue() > better) {
				better = (long) game.getValue();
				StringBuilder result = new StringBuilder();
				
				double time = better / 3600d; // Fait des heures
				if ((int)time>0) {
					
					result.append((int)time+"h ");
					time -= (int) time; // Retire la partie entière
				}
				
				time *= 60d; // Fait des minutes
				
				if ((int)time>0) {
					result.append((int)time+ "m ");
					time -= (int) time; // Retire la partie entière
				}
				
				time *= 60d; // Fait des secondes
				if ((long)time>0) {
					result.append((long)time+ "s");
					time -= (long) time; // Retire la partie entière
				}
				name = (String) game.getKey() + "("+result.toString()+")";
			}
		}
		
		return name;
	}
	
	public String getFavoriteChannel() {
		long better = 0;
		String name = "Pas de cannal favori";

		for (Map.Entry channel : channels.entrySet()) {
			if((long) channel.getValue() > better) {
				better = (long) channel.getValue();
				name = (String) channel.getKey() + "     ("+better+")";
			}
		}
		
		return name;
	}
		
	
	public String getPlayTime() {
		StringBuilder result = new StringBuilder();
		
		double time = playTime / 3600d; // Fait des heures
		if ((int)time>0) {
			
			result.append((int)time+"h ");
			time -= (int) time; // Retire la partie entière
		}
		
		time *= 60d; // Fait des minutes
		
		if ((int)time>0) {
			result.append((int)time+ "m ");
			time -= (int) time; // Retire la partie entière
		}
		
		time *= 60d; // Fait des secondes
		if ((long)time>0) {
			result.append((long)time+ "s ");
			time -= (long) time; // Retire la partie entière
		}
		if (result.length()==0) {
			result.append("Vous n'avez pas encore joué");
		}
		return result.toString();
	}
	
	public String getTimeOfConnection() {
		StringBuilder result = new StringBuilder();
		
		double time = timeOfConnection / 3600d; // Fait des heures
		if ((int)time>0) {
			
			result.append((int)time+"h ");
			time -= (int) time; // Retire la partie entière
		}
		
		time *= 60d; // Fait des minutes
		
		if ((int)time>0) {
			result.append((int)time+ "m ");
			time -= (int) time; // Retire la partie entière
		}
		
		time *= 60d; // Fait des secondes
		if ((long)time>0) {
			result.append((long)time+ "s ");
			time -= (long) time; // Retire la partie entière
		}
		if (result.length()==0) {
			result.append("Vous n'avez aucune connexion");
		}
		return result.toString();
	}
	
	
	
	public void addNumberOfConnection() {
		this.NumberOfConnection++;
	}
	
	public long getNumbreOfMessage() {
		return this.numberOfMessages;
	}
	
	public String getPerms() {
		StringBuilder result = new StringBuilder();
		result.append("```diff\n");
		if (doesAcceptPerm(Permissions.AVERAGE_CONNECTED_TIME)) {
			result.append("+ ");
		} else {
			result.append("- ");
		}
		result.append("Votre temps de connexion par jours\n");
		if (doesAcceptPerm(Permissions.AVERAGE_PLAY_TIME)) {
			result.append("+ ");
		} else {
			result.append("- ");
		}
		result.append("Votre temps de jeu en moyenne par jours\n");
		if (doesAcceptPerm(Permissions.FAVORITE_CHANNEL)) {
			result.append("+ ");
		} else {
			result.append("- ");
		}
		result.append("Votre cannal préféré\n");
		if (doesAcceptPerm(Permissions.FAVORITE_GAME)) {
			result.append("+ ");
		} else {
			result.append("- ");
		}
		result.append("Votre jeu préféré\n");
		if (doesAcceptPerm(Permissions.NUMBER_OF_MESSAGE)) {
			result.append("+ ");
		} else {
			result.append("- ");
		}
		result.append("Votre nombre de messages postés\n");
		if (doesAcceptPerm(Permissions.TYPING_TIME)) {
			result.append("+ ");
		} else {
			result.append("- ");
		}
		result.append("Votre temps d'écriture de message\n");
		result.append("\n```");
		return result.toString();
	}
	
	public void addMessageToCount(String channelName) {
		if (channels.get(channelName)!=null) {
			long temp = channels.get(channelName)+1;
			channels.remove(channelName);
			channels.put(channelName, temp);
		} else {
			channels.put(channelName, (long) 1);
			SQL_AddChannel(channelName);
		}
		this.numberOfMessages++;
	}
	
	public void setTelemetry(boolean active) {
		this.acceptTelemetry = active;
		SQL_ModifyTelemetry();
	}
	
	public void update() {
		SQL_ModifyTelemetry();
		SQL_SyncGame();
		SQL_SyncChannel();
	}
	
	
	/**
	 * 
	 * Function about SQL
	 * 
	 */
	
	private boolean SQL_UserExist() {		
		try {
			Statement statement = sql.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE discordId = '"+this.discordUser.getId()+"'");
			while (resultSet.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void SQL_AddUser() {
		try {
			Statement statement = sql.createStatement();
			statement.execute("INSERT INTO users (discordId) VALUES ('"+this.discordUser.getId()+"')");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void SQL_Perm() {
		
		try {
			Statement statement = sql.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE discordId = '"+this.discordUser.getId()+"'");
			
			while(resultSet.next()) {
				perms.put(Permissions.AVERAGE_CONNECTED_TIME, resultSet.getInt("Perm.AVERAGE_CONNECTED_TIME"));
				perms.put(Permissions.AVERAGE_PLAY_TIME, resultSet.getInt("Perm.AVERAGE_PLAY_TIME"));
				perms.put(Permissions.FAVORITE_CHANNEL, resultSet.getInt("Perm.FAVORITE_CHANNEL"));
				perms.put(Permissions.FAVORITE_GAME, resultSet.getInt("Perm.FAVORITE_GAME"));
				perms.put(Permissions.NUMBER_OF_MESSAGE, resultSet.getInt("Perm.NUMBER_OF_MESSAGE"));
				perms.put(Permissions.TYPING_TIME, resultSet.getInt("Perm.TYPING_TIME"));
				acceptTelemetry = resultSet.getBoolean("acceptTelemetry");
				numberOfMessages = resultSet.getLong("numberOfMessage");
				NumberOfConnection = resultSet.getLong("nombreDeCo");
				timeOfConnection = resultSet.getLong("TempsDeCo");
				playTime = resultSet.getLong("playTime");
				typingTime = resultSet.getLong("typingTime");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void SQL_Games() {
		try {
			Statement statement = sql.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM games WHERE discordId = '"+this.discordUser.getId()+"'");
			
			while(resultSet.next()) {
				games.put(resultSet.getString("gameName"), resultSet.getLong("playTime"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void SQL_AddGame(String gameName) {
		try {
			if (gameName.contains("'")) {
				gameName = gameName.substring(0, gameName.indexOf("'"))+"\\"+gameName.substring(gameName.indexOf("'"));
			}
			Statement statement = sql.createStatement();
			statement.execute("INSERT INTO games (discordId, gameName, playTime) VALUES ('"+this.discordUser.getId()+"', '"+gameName+"', "+games.get(gameName)+")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void SQL_SyncGame() {
		for(Map.Entry game: games.entrySet()) {
			try {
				if(game.getKey().toString().contains("'")) {
					String before = game.getKey().toString().substring(0, game.getKey().toString().indexOf("'"));
					String after = game.getKey().toString().substring(game.getKey().toString().indexOf("'"));
					Statement statement = sql.createStatement();
					statement.executeUpdate("UPDATE games SET playTime = "+game.getValue()+" WHERE discordId = '"+this.discordUser.getId()+"' AND gameName = '"+before+"\\"+after+"'");
				} else {
					Statement statement = sql.createStatement();
					statement.executeUpdate("UPDATE games SET playTime = "+game.getValue()+" WHERE discordId = '"+this.discordUser.getId()+"' AND gameName = '"+game.getKey()+"'");
			
				}
		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
	private void SQL_Channels() {
		try {
			Statement statement = sql.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM channels WHERE discordId = '"+this.discordUser.getId()+"'");
			
			while(resultSet.next()) {
				channels.put(resultSet.getString("channelName"), resultSet.getLong("nombre"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void SQL_AddChannel(String channelName) {
		try {
			Statement statement = sql.createStatement();
			statement.execute("INSERT INTO channels (discordId, channelName, nombre) VALUES ('"+this.discordUser.getId()+"', '"+channelName+"', "+channels.get(channelName)+")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void SQL_SyncChannel() {
		for(Map.Entry channel: channels.entrySet()) {
			try {
				Statement statement = sql.createStatement();
				statement.executeUpdate("UPDATE channels SET nombre = "+channel.getValue()+" WHERE discordId = '"+this.discordUser.getId()+"' AND channelName = '"+channel.getKey()+"'");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	public void close() {
		try {
			sql.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	private void SQL_ModifyTelemetry() {
		try {
			Statement statement = sql.createStatement();
			statement.executeUpdate("UPDATE users SET acceptTelemetry = "+this.acceptTelemetry+", numberOfMessage = "+this.numberOfMessages+", nombreDeCo = "+this.NumberOfConnection+", TempsDeCo = "+this.timeOfConnection+", playTime = "+this.playTime+", typingTime = "+this.typingTime+" WHERE discordId = '"+this.discordUser.getId()+"'");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
