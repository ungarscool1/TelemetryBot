package tk.ungarscool1.bot.telemetry.connector;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SendStats extends Thread{

	public void run() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (calendar.get(Calendar.DAY_OF_MONTH)==1) {
			if (calendar.get(Calendar.HOUR_OF_DAY)==0&&calendar.get(Calendar.MINUTE)==0&&(calendar.get(Calendar.SECOND)>=1&&calendar.get(Calendar.SECOND)<=3)) {
				Connector.sendStats();
			}
		}
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (Exception e) {
			System.err.println("Il y a un problème sur le thread SendStats.java");
		}
		System.out.println("SendStats.java - Exécuté");
		run();
	}
	
}
