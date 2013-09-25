package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import log.Logger;

import stefan.piechart.PieChartData;

import com.ibm.icu.text.SimpleDateFormat;

import db.DBManager;

public class Statistic {
	public static final int YEAR_OF_START = 2011;
	public static final String beginOfYear = "-09-01", endOfYear = "-08-30";
	public static final String sqlTimeFormat = "yyyy-MM-dd";
	public static final String GENDER_BOTH = "both", GENDER_MALE = "male", GENDER_FEMALE = "female";
	private static int currentSchuljahr = -1;
	
	/**
	 * 
	 * @param year e.g. 2011 = Schuljahr vom 1.9.2010 bis 30.8.2011
	 * @param gender Statistics.GENDER_BOTH, Statistics.GENDER_MALE, Statistics.GENDER_FEMALE
	 * @param jahrgangsstufe 
	 * 					-1 : Alle
	 * 				 	 0 : Sonstige
	 * 				 	 1 : 1. Klassen
	 * 						...	
	 * @return
	 */
	public static PieChartData[] getPieData_Kategorien(int year, String gender, int jahrgangsstufe){
		Vector<PieChartData> vec = new Vector<PieChartData>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			String gender_query_where = "";
			String stufe_query = "";
			if(gender == GENDER_MALE){
				gender_query_where = "s.male = 1";
			}else if(gender == GENDER_FEMALE){
				gender_query_where = "s.male = 0";
			}
			
			switch(jahrgangsstufe){
			case -1: break;
			case 0: stufe_query = "not regexp_like(s.klasse, '\\d.+')"; break;
			default: stufe_query = "regexp_like(s.klasse, '"+jahrgangsstufe+"\\D+')"; break;
			}
			
			String where = "";
			if(gender_query_where.length()!=0 && stufe_query.length()!=0) where += "where "+gender_query_where+" and "+stufe_query;
			else if(gender_query_where.length()!=0) where += "where "+gender_query_where;
			else if(stufe_query.length() != 0) where += "where "+stufe_query;
			
			ResultSet set = state.executeQuery(String.format("select b.kategorie_id, count(a.id) from %s a join %s b on b.id = a.b_id join %s s on s.id = a.s_id " +
					" %s " +
					"group by b.kategorie_id order by b.kategorie_id", 
					DBManager.TABLE_AUSLEIHEN+getSQLyearAddition(year), DBManager.TABLE_BUECHER+getSQLyearAddition(year), 
					DBManager.TABLE_SCHUELER+getSQLyearAddition(year), where));
			
			while(set.next()){
				Kategorie kat = Kategorie.fromId(set.getInt(1));
				vec.add(new PieChartData(kat.getName(), set.getInt(2), kat.getColor()));
			}
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return (PieChartData[]) vec.toArray(new PieChartData[0]);
	}
	
	/**
	 * 
	 * @param year e.g. 2011 = Schuljahr vom 1.9.2010 bis 30.8.2011
	 * @param gender Statistics.GENDER_BOTH, Statistics.GENDER_MALE, Statistics.GENDER_FEMALE
	 * @param jahrgangsstufe 
	 * 					-1 : Alle
	 * 				 	 0 : Sonstige
	 * 				 	 1 : 1. Klassen
	 * 						...	
	 * @return
	 */
	public static PieChartData[] getPieData_Jahrgang(int year, String gender){
		Vector<PieChartData> vec = new Vector<PieChartData>();
		for(int i=0; i<=4; i++){
			try{
				Statement state = DBManager.getIt().getConnection().createStatement();
				String gender_query_where = "";
				if(gender == GENDER_MALE){
					gender_query_where = "and s.male = 1";
				}else if(gender == GENDER_FEMALE){
					gender_query_where = "and s.male = 0";
				}

				String jahrgang_query = "not regexp_like(s.klasse, '\\d+\\D+')";
				if(i != 0) jahrgang_query = String.format("regexp_like(s.klasse, '%d\\D+')", i);
				
				
				ResultSet set = state.executeQuery(String.format("select count(a.id) from %s a join %s s on s.id = a.s_id " +
						"where %s %s", 
						DBManager.TABLE_AUSLEIHEN+getSQLyearAddition(year), DBManager.TABLE_SCHUELER+getSQLyearAddition(year), jahrgang_query, gender_query_where));
				
				set.next();
				if(i==0) vec.add(new PieChartData("Sonstige", set.getInt(1)));
				else vec.add(new PieChartData(i+". Klassen", set.getInt(1)));
				
				set.close();
				state.close();
			}catch(SQLException ex){
				Logger.logError(ex.getMessage());
			}
		}
		return (PieChartData[]) vec.toArray(new PieChartData[0]);
	}
	
	/**
	 * 
	 * @param year e.g. 2011 = Schuljahr vom 1.9.2010 bis 30.8.2011
	 * @param gender Statistics.GENDER_BOTH, Statistics.GENDER_MALE, Statistics.GENDER_FEMALE
	 * 
	 * @return
	 */
	public static PieChartData[] getPieData_Klassen(int year, String gender){
		Vector<PieChartData> vec = new Vector<PieChartData>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			String gender_query_where = "";
			if(gender == GENDER_MALE){
				gender_query_where = "where s.male = 1";
			}else if(gender == GENDER_FEMALE){
				gender_query_where = "where s.male = 0";
			}
			
			ResultSet set = state.executeQuery(String.format("select s.klasse, count(a.id) from %s a join %s s on s.id = a.s_id " +
					"%s group by s.klasse order by s.klasse", 
					DBManager.TABLE_AUSLEIHEN+getSQLyearAddition(year), DBManager.TABLE_SCHUELER+getSQLyearAddition(year), gender_query_where));
			
			while(set.next())
				vec.add(new PieChartData(set.getString(1), set.getInt(2)));
			
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		
		return (PieChartData[]) vec.toArray(new PieChartData[0]);
	}
	
	
	public static int getCurrentSchuljahr(){
		if(currentSchuljahr != -1) return currentSchuljahr;
		Calendar c = Calendar.getInstance();
		try {
			Date beginOfSchuljahr = new SimpleDateFormat(sqlTimeFormat).parse(c.get(Calendar.YEAR)+beginOfYear);
			if(new Date(c.getTimeInMillis()).after(beginOfSchuljahr)){
				currentSchuljahr = c.get(Calendar.YEAR);
				return currentSchuljahr;
			}
		} catch (ParseException e) {
			//Logger.logError(e.getMessage())d catch block
			e.printStackTrace();
		}
		currentSchuljahr = c.get(Calendar.YEAR)-1;
		return currentSchuljahr;
	}
	
	public static int getAusleihen(int year){
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery(String.format("select count(id) from %s",
					DBManager.TABLE_AUSLEIHEN+getSQLyearAddition(year)));
			
			while(set.next()){
				return set.getInt(1);
			}

			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return 0;
	}
	
	public static int getAusgelieheneMedien(int year){
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery(String.format("select count(b.isbn) from %s b, %s a where " +
					"b.id = a.b_id", 
					DBManager.TABLE_BUECHER+getSQLyearAddition(year),
					DBManager.TABLE_AUSLEIHEN+getSQLyearAddition(year)));
			
			while(set.next()){
				return set.getInt(1);
			}

			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return 0;
	}
	
	public static int getKlassenCount(int year){
		int ret = 0;
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery(String.format("select count(klasse) from %s",
					DBManager.TABLE_SCHUELER+getSQLyearAddition(year)));
			
			set.next();
			ret = set.getInt(1);
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return ret;
	}
	
	public static ArrayList<String> getKlassen(int year){
		ArrayList<String> ret = new ArrayList<String>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery(String.format("select klasse from %s group by klasse order by klasse",
					DBManager.TABLE_SCHUELER+getSQLyearAddition(year)));
			
			while(set.next()){
				ret.add(set.getString(1));
			}
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return ret;
	}
	
	public static ArrayList<Statistic_SchuelerUndAnzahl> getTop5(int year, String klasse){
		ArrayList<Statistic_SchuelerUndAnzahl> ret = new ArrayList<Statistic_SchuelerUndAnzahl>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery(String.format("select * from (select s.nachname, s.vorname, count(a.id) from %s a join %s s on s.id = a.s_id " +
					"where s.klasse = '%s' group by s.nachname, s.vorname order by count(a.id) desc) where rownum <= 5",
					DBManager.TABLE_AUSLEIHEN+getSQLyearAddition(year), DBManager.TABLE_SCHUELER+getSQLyearAddition(year), klasse));
			
			while(set.next()){
				ret.add(new Statistic_SchuelerUndAnzahl(set.getString(1), set.getString(2), set.getInt(3)));
			}

			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return ret;
	}
	

	public static ArrayList<Statistic_SchuelerUndAnzahl> getFlop5(int year, String klasse){
		ArrayList<Statistic_SchuelerUndAnzahl> ret = new ArrayList<Statistic_SchuelerUndAnzahl>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery(String.format("select * from (select nachname, vorname from %s " +
					"where klasse = '%s' and id not in (select s_id from %s) group by nachname, vorname) where rownum <= 5",
					DBManager.TABLE_SCHUELER+getSQLyearAddition(year), klasse, DBManager.TABLE_AUSLEIHEN+getSQLyearAddition(year)));
			
			while(set.next()){
				ret.add(new Statistic_SchuelerUndAnzahl(set.getString(1), set.getString(2), 0));
			}

			set.close();
			state.close();
			state = DBManager.getIt().getConnection().createStatement();
			set = state.executeQuery(String.format("select * from (select s.nachname, s.vorname, count(a.id) from %s a join %s s on s.id = a.s_id " +
					"where s.klasse = '%s' group by s.nachname, s.vorname order by count(a.id) asc) where rownum <= %d",
					DBManager.TABLE_AUSLEIHEN+getSQLyearAddition(year), DBManager.TABLE_SCHUELER+getSQLyearAddition(year), klasse, 5-ret.size()));
			
			while(set.next()){
				ret.add(new Statistic_SchuelerUndAnzahl(set.getString(1), set.getString(2), set.getInt(3)));
			}

			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return ret;
	}
	
	public static String getSQLyearAddition(int year){
		if(year == getCurrentSchuljahr()) return "";
		else return "_"+year;
	}
}
