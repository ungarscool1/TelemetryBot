package tk.ungarscool1.bot.telemetry.entities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.javacord.api.entity.user.User;


public class Person {

	
	private User discordUser;
	private boolean acceptTelemetry;
	private HashMap<Permissions, Boolean> perms = new HashMap<>();
	private long numberOfMessages;
	private long NumberOfConnection;
	private long timeOfConnection;
	private long playTime;
	private long listenTime;
	private HashMap<String, Long> games = new HashMap<>();
	private HashMap<String, Long> channels = new HashMap<>();
	private HashMap<String, Long> musics = new HashMap<>();
	private long typingTime;
	private Timestamp lastSync;
	
	// Connection var
	
	private long onlineTimeStamp;
	private long offlineTimeStamp;
	
	// Play var
	
	private long startPlay;
	private long endPlay;
	
	// Type var
	
	private long startType, endType;
	
	// Listen var
	
	private long startListen, stopListen;
	
	// SQL var
	
	private Connection sql;
	private boolean sqlConnected = false;
	
	public Person(User user) {
		this.discordUser = user;
		try {
            sql = DriverManager.getConnection("jdbc:mysql://localhost:3306/telemetry", "root", "YOUR_ROOT_PASSWORD");
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
				SQL_Musics();
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
		return perms.get(permission);
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
			
			result.append((int)time+"h");
			time -= (int) time; // Retire la partie entière
		}
		
		time *= 60d; // Fait des minutes
		
		if ((int)time>0) {
			result.append(" "+(int)time+ "m");
			time -= (int) time; // Retire la partie entière
		}
		
		time *= 60d; // Fait des secondes
		if ((long)time>0) {
			result.append(" "+(long)time+ "s");
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
	
	public void startListen(String music) {
		this.startListen = System.currentTimeMillis() / 1000L;
		if (musics.get(music)!=null) {
			long times = musics.get(music) + 1;
			musics.remove(music);
			musics.put(music, times);
			SQL_SyncMusics();
		} else {
			musics.put(music, 1L);
			SQL_AddMusic(music);
		}
	}
	
	public void stopListen() {
		this.stopListen = System.currentTimeMillis() / 1000L;
		long diff = this.stopListen - this.startListen;
		this.listenTime += diff;
	}
	
	public String getListenTime() {
		StringBuilder result = new StringBuilder();
		
		double time = this.listenTime / 3600d; // Fait des heures
		if ((int)time>0) {
			
			result.append((int)time+"h");
			time -= (int) time; // Retire la partie entière
		}
		
		time *= 60d; // Fait des minutes
		
		if ((int)time>0) {
			result.append(" "+(int)time+ "m");
			time -= (int) time; // Retire la partie entière
		}
		
		time *= 60d; // Fait des secondes
		if ((long)time>0) {
			result.append(" "+(long)time+ "s");
			time -= (long) time; // Retire la partie entière
		}
		if (result.length()==0) {
			result.append("Vous n'avez pas encore écouter de musique sur Spotify, ou vérifier que votre compte discord soit lier à Spotify.");
		}
		return result.toString();
	}
	
	@SuppressWarnings("rawtypes")
	public String getFavoriteMusic() {
		long better = 0;
		String name = "Pas de musique favorite";
		for (Map.Entry music : musics.entrySet()) {
			if((long) music.getValue() > better) {
				better = (long) music.getValue();
				String temp = (String) music.getKey();
				System.out.println("Debug: " + temp);
				String disasemble[] = temp.split(" , ");
				name = disasemble[0] + " - " + disasemble[1] + " ("+better+")";
			}
		}
		
		return name;
	}
	
	@SuppressWarnings("rawtypes")
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
	
	
	@SuppressWarnings("rawtypes")
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
		if (doesAcceptPerm(Permissions.AVERAGE_LISTEN_TIME)) {
			result.append("+ ");
		} else {
			result.append("- ");
		}
		result.append("Votre temps d'écoute de musique sur Spotify\n");
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
		if (doesAcceptPerm(Permissions.FAVORITE_MUSIC)) {
			result.append("+ ");
		} else {
			result.append("- ");
		}
		result.append("Votre musique préférée\n");
		if (doesAcceptPerm(Permissions.FAVORITE_MUSIC_ALBUM)) {
			result.append("+ ");
		} else {
			result.append("- ");
		}
		result.append("Votre album de musique préféré\n");
		if (doesAcceptPerm(Permissions.FAVORITE_MUSIC_ARTIST)) {
			result.append("+ ");
		} else {
			result.append("- ");
		}
		result.append("Votre artiste préféré\n");
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
		try {
		SQL_ModifyTelemetry();
		} catch (Exception e) {
			try {
				sql = DriverManager.getConnection("jdbc:mysql://localhost:3306/telemetry", "root", "QiN@t^jt02!P");
			} catch (SQLException e1) {
			}
			this.update();
		}
	}
	
	public void setPerm(Permissions permission, boolean active) {
		this.perms.replace(permission, active);
	}
	
	public Timestamp getLastSync() {
		return this.lastSync;
	}
	
	
	
	public void update() {
		this.lastSync = new Timestamp(System.currentTimeMillis());
		try {
		SQL_ModifyTelemetry();
		} catch (Exception e) {
			try {
				sql = DriverManager.getConnection("jdbc:mysql://localhost:3306/telemetry", "root", "QiN@t^jt02!P");
			} catch (SQLException e1) {
			}
			this.update();
			return;
		}
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
				perms.put(Permissions.AVERAGE_CONNECTED_TIME, resultSet.getBoolean("Perm.AVERAGE_CONNECTED_TIME"));
				perms.put(Permissions.AVERAGE_PLAY_TIME, resultSet.getBoolean("Perm.AVERAGE_PLAY_TIME"));
				perms.put(Permissions.AVERAGE_LISTEN_TIME, resultSet.getBoolean("Perm.AVERAGE_LISTEN_TIME"));
				perms.put(Permissions.FAVORITE_CHANNEL, resultSet.getBoolean("Perm.FAVORITE_CHANNEL"));
				perms.put(Permissions.FAVORITE_GAME, resultSet.getBoolean("Perm.FAVORITE_GAME"));
				perms.put(Permissions.FAVORITE_MUSIC, resultSet.getBoolean("Perm.FAVORITE_MUSIC"));
				perms.put(Permissions.FAVORITE_MUSIC_ARTIST, resultSet.getBoolean("Perm.FAVORITE_MUSIC_ARTIST"));
				perms.put(Permissions.FAVORITE_MUSIC_ALBUM, resultSet.getBoolean("Perm.FAVORITE_MUSIC_ALBUM"));
				perms.put(Permissions.NUMBER_OF_MESSAGE, resultSet.getBoolean("Perm.NUMBER_OF_MESSAGE"));
				perms.put(Permissions.TYPING_TIME, resultSet.getBoolean("Perm.TYPING_TIME"));
				acceptTelemetry = resultSet.getBoolean("acceptTelemetry");
				numberOfMessages = resultSet.getLong("numberOfMessage");
				NumberOfConnection = resultSet.getLong("nombreDeCo");
				timeOfConnection = resultSet.getLong("TempsDeCo");
				playTime = resultSet.getLong("playTime");
				typingTime = resultSet.getLong("typingTime");
				listenTime = resultSet.getLong("listenTime");
				lastSync = resultSet.getTimestamp("lastSync");
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
	
	@SuppressWarnings("rawtypes")
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
	@SuppressWarnings("rawtypes")
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
	
	
	/*
	 * SQL function for musics	
	 */
	private void SQL_Musics() {
		try {
			Statement statement = sql.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM musics WHERE discordId = '"+this.discordUser.getId()+"'");
			
			while(resultSet.next()) {
				String musicBuilder = resultSet.getString("name") + " , " + resultSet.getString("artist") + " , " + resultSet.getString("album");
				musics.put(musicBuilder, resultSet.getLong("times"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void SQL_AddMusic(String music) {
		try {
			if (music.contains("'")) {
				music = music.substring(0, music.indexOf("'"))+"\\"+music.substring(music.indexOf("'"));
			}
			String disasemble[] = music.split(" , "); // Titre , Artiste , Album
			Statement statement = sql.createStatement();
			statement.execute("INSERT INTO musics (discordId, name, artist) VALUES ('" + this.discordUser.getId() + "', '" + disasemble[0] + "', '" + disasemble[1] + "')");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void SQL_SyncMusics() {
		for(Map.Entry music: musics.entrySet()) {
			String disasemble[] = music.getKey().toString().split(" , "); // Titre , Artiste , Album
			try {
				if(music.getKey().toString().contains("'")) {
					String before = disasemble[0].substring(0, music.getKey().toString().indexOf("'"));
					String after = disasemble[0].substring(music.getKey().toString().indexOf("'"));
					Statement statement = sql.createStatement();
					statement.executeUpdate("UPDATE musics SET times = "+music.getValue()+" WHERE discordId = '"+this.discordUser.getId()+"' AND name = '"+before+"\\"+after+"' AND artist = '" + disasemble[1] + "'");
				} else {
					Statement statement = sql.createStatement();
					statement.executeUpdate("UPDATE musics SET times = "+music.getValue()+" WHERE discordId = '" + this.discordUser.getId() + "' AND name = '" + disasemble[0] + "' AND artist = '" + disasemble[1] + "'");
			
				}
		
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
	
	
	
	
	private void SQL_ModifyTelemetry() throws SQLException {
			Statement statement = sql.createStatement();
			statement.executeUpdate("UPDATE users SET acceptTelemetry = "+this.acceptTelemetry+", numberOfMessage = "+this.numberOfMessages+", nombreDeCo = "+this.NumberOfConnection+", TempsDeCo = "+this.timeOfConnection+", playTime = "+this.playTime+", typingTime = "+this.typingTime+", listenTime = "+this.listenTime+", lastSync = '"+this.lastSync+"' WHERE discordId = '"+this.discordUser.getId()+"'");
	}
	
}
