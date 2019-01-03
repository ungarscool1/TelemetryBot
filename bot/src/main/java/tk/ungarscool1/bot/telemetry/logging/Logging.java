package tk.ungarscool1.bot.telemetry.logging;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Logging {

	@SuppressWarnings("unused")
	private boolean sqlConnected;
	private Connection sql;
	
	public Logging() {
		try {
            sql = DriverManager.getConnection("jdbc:mysql://localhost:3306/telemetry", "root", "");
            sqlConnected = true;
        } catch (Exception e) {
            System.err.println("Impossible de se connecté à la base de donnée");
            sqlConnected = false;
        }
	}
	
	public void addEvent(String Qui, String action) {
		try {
			Statement statement = sql.createStatement();
			statement.execute("INSERT INTO logs (temps, de, action) VALUES (NULL, '"+Qui+"', '"+action+"'");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
