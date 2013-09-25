package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import log.Logger;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import db.DBManager;

public class Kategorie {
	public static final String DELIMITER = ",";
	/**
	 * ID für beliebige Kategorie
	 */
	public static final int ALLE_ID = 0;
	
	private int id=-1;
	private Color color;
	private String name;
	
	public static Vector<Kategorie> getKategorien(){
		Vector<Kategorie> vec = new Vector<Kategorie>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select * from "+DBManager.TABLE_KATEGORIEN+" order by id");
			while(set.next()){
				vec.add(new Kategorie(set));
			}
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return vec;
	}
	
	public Kategorie(ResultSet set){
		try{
			this.id = set.getInt("id");
			this.name = set.getString("name");
			this.color = fromString(set.getString("color"));
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
	}
	
	public Kategorie(String name, String color){
		this.name = name;
		this.color = fromString(color);
	}
	
	
	public Kategorie(String name, Color color) {
		this.name = name;
		this.color = color;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}
	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	public void eintragen(){
		try{
			if(id == -1){

				Statement state = DBManager.getIt().getConnection().createStatement();
				ResultSet set = state.executeQuery("select "+DBManager.SEQ_KATEGORIEN+".nextval from dual");
				set.next();
				id = set.getInt(1);
				set.close();
				state.execute(String.format(
					"insert into "+DBManager.TABLE_KATEGORIEN+" (id,name,color) values (%d,'%s', '%s')",id,name, toString(color)));
				state.close();
			}
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}

		Logger.logEvent("Kategorie.eintragen", toString());
	}
	
	public void entfernen(){
		if(id == -1) return;
		try{

			Statement state = DBManager.getIt().getConnection().createStatement();
			state.execute(String.format(
					"update "+DBManager.TABLE_BUECHER+" set kategorie_id = 0 where kategorie_id=%d",id));
			state.execute(String.format(
				"delete from "+DBManager.TABLE_KATEGORIEN+" where id=%d",id));
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		Logger.logEvent("Kategorie.entfernen", toString());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Kategorie)
			return ((Kategorie) obj).getId() == id;
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("[Kategorie id=%d, name=%s, color=%s]", id, name, Kategorie.toString(color));
	}
	
	public static Color fromString(String s){
		String[] split = s.split(",");
		return new Color(Display.getCurrent(), new Integer(split[0]), new Integer(split[1]), new Integer(split[2]));
	}
	
	public static String toString(Color c){
		return c.getRed()+DELIMITER+c.getGreen()+DELIMITER+c.getBlue();
	}
	
	public static Kategorie fromId(int id){
		try{

			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select * from "+DBManager.TABLE_KATEGORIEN+" where id = "+id);
			set.next();
			Kategorie k = new Kategorie(set);
			set.close();
			state.close();
			return k;
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return null;
	}
	
	public static Vector<Object[]> getTop(int anzahl){
		Vector<Object[]> vec = new Vector<Object[]>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery(String.format("select count(*), k.id from %s a, %s k, %s b where a.b_id = b.id and b.kategorie_id = k.id " +
					"group by k.id order by count(*) desc",
					DBManager.TABLE_AUSLEIHEN, DBManager.TABLE_KATEGORIEN,DBManager.TABLE_BUECHER));
			while(set.next() && vec.size() < anzahl){
				vec.add(new Object[]{set.getString(1), Kategorie.fromId(set.getInt(2))});
			}
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return vec;
	}
}
