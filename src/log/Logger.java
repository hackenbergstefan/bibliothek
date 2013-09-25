package log;

import gui.options.Preferences;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import util.DateUtils;

public class Logger {
	private static final boolean LOGGIN_ON = true;
	private static final boolean LOG_TO_STDIN_ON = true;
	private static final String newLine = System.getProperty("line.separator");
	
	public static void logEvent(String eventname, String data){
		if(!LOGGIN_ON) return;
		try{
			File f = new File(Preferences.getPrefs().getString("log.file"));
			if(!f.exists()) f.createNewFile();
			FileWriter wr = new FileWriter(f, true);
			
			String s = DateUtils.longFormat.format(new Date(System.currentTimeMillis()))+"\t"+eventname+" - "+data;
			wr.append(s+newLine);
			if(LOG_TO_STDIN_ON) System.out.println(s);
			
			wr.close();
		}catch (Exception e) {
			Logger.logError(e.getMessage());
		}
	}
	
	public static void logError(String data){
		if(!LOGGIN_ON) return;
		try{
			File f = new File(Preferences.getPrefs().getString("log.errfile"));
			if(!f.exists()) f.createNewFile();
			FileWriter wr = new FileWriter(f, true);
			
			StackTraceElement el = Thread.currentThread().getStackTrace()[2];
			String s = DateUtils.longFormat.format(new Date(System.currentTimeMillis()))+"\t"+el.getClassName()+"."+el.getMethodName()+": "+data;
			wr.append(s+newLine);
			if(LOG_TO_STDIN_ON) System.out.println(s);
			
			wr.close();
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
}
