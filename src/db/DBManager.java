package db;

import gui.options.Preferences;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import log.Logger;


public class DBManager {	
	public static final String[] STUFEN = {"Beliebig","A", "B", "C", "D"};
	public static final String[] MEDIENART = {"Beliebig", "Buch", "Zeitschrift", "Spiel", "CD", "Kassette"};
	
	public static String SEQ_SCHUELER = "seq_bib_schueler";
	public static String SEQ_BUECHER = "seq_bib_buecher";
	public static String SEQ_AUSLEIHEN = "seq_bib_ausleihen";
	public static String SEQ_KATEGORIEN = "seq_bib_kategorien";
	
	/*
	 * Tabellen Namen.
	 * Jedes Schuljahr werden alle Tabellen in "tabellenname_AltesSchuljahr" also z.B. bib_schueler_2011 umbenannt.
	 * 
	 */
	public static String TABLE_SCHUELER = "bib_schueler";
	public static String TABLE_BUECHER = "bib_buecher";
	public static String TABLE_AUSLEIHEN = "bib_ausleihen";
	public static String TABLE_KATEGORIEN = "bib_kategorien";
	
	private static DBManager dbManager;
	
	
	public static DBManager getIt(){
		if(dbManager == null){
			try{
				dbManager = new DBManager();
			}catch(Exception ex){
				return null;
			}
		}
		return dbManager;
	}
	
	private Connection conn = null;
	
	
	public DBManager(){
		connect();
	}
	
	public Connection getConnection(){
		return conn;
	}
	
	public void disconnect(){
		try {
			if(conn != null) conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Logger.logError(e.getMessage());
		}
	}
	
	public void connect(){
		try {
			if(conn != null && !conn.isClosed()) disconnect();
			
			// Load the JDBC driver
		    String driverName = "oracle.jdbc.driver.OracleDriver";
		    Class.forName(driverName);

		    // Create a connection to the database
		    String url = "jdbc:oracle:thin:@" + Preferences.getPrefs().getString("db.servername")+ ":" + Preferences.getPrefs().getString("db.portnumber") + ":" + Preferences.getPrefs().getString("db.sid");
		    Properties props = new Properties();
		    props.setProperty("user", Preferences.getPrefs().getString("db.username"));
		    props.setProperty("password", Preferences.getPrefs().getString("db.password"));
		    props.setProperty("useUnicode","true");
		    props.setProperty("characterEncoding","UTF-8");
		    props.setProperty("connectionCollation","utf8_general_ci");
		    conn = DriverManager.getConnection(url, props);
		} catch (Exception e) {
			// TODO Auto-generated catLogger.logError(e.getMessage())StackTrace();
			Logger.logError(e.getMessage());
		}
	}
	
	public static void runSQLFile(File f){
		try {
			Runtime.getRuntime().exec(String.format("sqlplus %s/%s @\"%s\"",
					Preferences.getPrefs().getString("db.username"),
					Preferences.getPrefs().getString("db.password"),
					f.getAbsolutePath()));
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
