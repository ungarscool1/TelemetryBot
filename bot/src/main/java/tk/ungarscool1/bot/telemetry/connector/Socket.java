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
		port(8081);
		get("/setPass/:user", (req, res)-> {
			int pass = 100000 + (int)(Math.random() * ((999999 - 100000) + 1));
			setPassword(req.params(":user"), pass+"");
			Connector.sendMessage(getPassword(req.params(":user")), req.params(":user"));
			return "The password has been updated for "+req.params(":user");
		});
		
		get("/update/:user", (req, res)-> {
			Connector.updateTelemetryInfo(req.params(":user"));
			return "Une mise à jour a été demander sur le bot";
		});
		
		get("/getPass/:user", (req, res) -> {
			return getPassword(req.params(":user"));
		});
		
		notFound((req, res) -> {
		    return "<html>\r\n" + 
		    		"	<head>\r\n" + 
		    		"		<title>404 Not Found</title>\r\n" + 
		    		"	</head>\r\n" + 
		    		"	<body>\r\n" + 
		    		"		<h1>404 Not Found</h1>\r\n" + 
		    		"		<p>This website is not found</p>\r\n" + 
		    		"	</body>\r\n" + 
		    		"</html>";
		});
		
		
		
	}
	
}
