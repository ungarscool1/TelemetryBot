package tk.ungarscool1.bot.telemetry.connector;

import java.util.concurrent.TimeUnit;

public class AutoSaver extends Thread{
	
	public void run() {
		try {
			TimeUnit.MINUTES.sleep(5);
		} catch (Exception e) {
			
		}
		Connector.syncToDB();
		run();
	}
}
