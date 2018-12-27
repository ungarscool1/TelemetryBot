package tk.ungarscool1.bot.telemetry.connector;

import static spark.Spark.*;

import java.util.HashMap;


public class Socket extends Thread{

	private HashMap<String, String> tempPassword = new HashMap<>();
	
	private void setPassword(String user, String pass) {
		if (!isPresent(user)) {
			tempPassword.put(user, pass);
		} else {
			tempPassword.replace(user, pass);
		}
	}
	
	private boolean isPresent(String user) {
		return tempPassword.containsKey(user);
	}
	
	private String getPassword(String userName) {
		return tempPassword.get(userName);
	}
	
	public void run() {
		get("/newPass/:user/:pass", (req, res)-> {
			setPassword(req.params(":user"), req.params(":pass"));
			Connector.sendMessage(getPassword(req.params(":user")), req.params(":user"));
			return "The password has been updated\nWho:"+req.params(":user")+"\tPass:"+getPassword(req.params(":user"));
		});
		
		get("/update/:user", (req, res)-> {
			Connector.updateTelemetryInfo(req.params(":user"));
			return "Une mise à jour a été demander sur le bot";
		});
		
	}
	
}
