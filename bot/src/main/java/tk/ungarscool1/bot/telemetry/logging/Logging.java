package tk.ungarscool1.bot.telemetry.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logging {

	FileOutputStream log = null;
	File logFile = null;
	public Logging() {
		logFile = new File("logs/telemetry-" + year() + "-" + month() + "-" + day() + "-" + hh() + mm() + ".log");
		try {
			logFile.getParentFile().mkdirs();
			logFile.createNewFile();
			log = new FileOutputStream(logFile, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addEvent(String log) {
		log = "[" +day() + " " + hh() + ":" + mm() + ":" + ss() + "] " + log;
		System.out.println(log);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
			writer.write(log);
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int year() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy");
        Date date = new Date();

        return Integer.parseInt(dateFormat.format(date));
	}
	
	private int month() {
		DateFormat dateFormat = new SimpleDateFormat("MM");
        Date date = new Date();

        return Integer.parseInt(dateFormat.format(date));
	}
	
	private int day() {
		DateFormat dateFormat = new SimpleDateFormat("dd");
        Date date = new Date();

        return Integer.parseInt(dateFormat.format(date));
	}
	
	private int hh() {
		DateFormat dateFormat = new SimpleDateFormat("HH");
        Date date = new Date();

        return Integer.parseInt(dateFormat.format(date));
	}
	
	private int mm() {
		DateFormat dateFormat = new SimpleDateFormat("mm");
        Date date = new Date();

        return Integer.parseInt(dateFormat.format(date));
	}
	
	private int ss() {
		 DateFormat dateFormat = new SimpleDateFormat("ss");
	        Date date = new Date();

	        return Integer.parseInt(dateFormat.format(date));
	}
	
}
