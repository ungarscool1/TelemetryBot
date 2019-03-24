package tk.ungarscool1.bot.telemetry.connector;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import tk.ungarscool1.bot.telemetry.Main;
import tk.ungarscool1.bot.telemetry.logging.Logging;

public class SendStats extends Thread{

	private static Logging logs = Main.logs;
	
	public void run() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (calendar.get(Calendar.DAY_OF_MONTH)==1) {
			if (calendar.get(Calendar.HOUR_OF_DAY)==0&&calendar.get(Calendar.MINUTE)==0) {
				Connector.sendStats();
			}
		}
		try {
			TimeUnit.MINUTES.sleep(1);
		} catch (Exception e) {
			logs.addEvent("Le thread SendStats vient de crash... Le relancement automatique est désactivé !");
		}
		run();
	}
	
}
