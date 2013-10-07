package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.regex.Pattern;

import log.Logger;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.wb.swt.SWTResourceManager;

import util.FontUtil;
import util.MutableInteger;
import db.DBManager;

public class Schueler implements Comparable<Schueler>, IStringable, IDefault{	
	private int id=-1;
	private String vorname="NN", nachname="NN", klasse="", anmerkungen = "";
	private Gender gender;
	private boolean out;

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	public Schueler(){
		
	}
	
	public Schueler(int id){
		changes.firePropertyChange("id", this.id, this.id= id);
	}
	
	public Schueler(ResultSet set){
		try{
			id = set.getInt("id");
			vorname = set.getString("vorname");
			nachname = set.getString("nachname");
			klasse = set.getString("klasse");
			anmerkungen = set.getString("anmerkungen");
			out = set.getBoolean("out");
			gender = set.getBoolean("male")?Gender.MALE:Gender.FEMALE;
		}catch(SQLException ex){}
		if(anmerkungen == null) anmerkungen = "";
	}
	
	public int getId() {
		return id;
	}
	
	
	
	/**
	 * @param vorname the vorname to set
	 */
	public void setVorname(String vorname) {
		changes.firePropertyChange("vorname", this.vorname, this.vorname = vorname);
	}

	/**
	 * @return the vorname
	 */
	public String getVorname() {
		return vorname;
	}

	
	
	/**
	 * @param nachname the nachname to set
	 */
	public void setNachname(String nachname) {
		changes.firePropertyChange("nachname", this.nachname, this.nachname = nachname);
	}

	/**
	 * @return the nachname
	 */
	public String getNachname() {
		return nachname;
	}

	
	/**
	 * @param klasse the klasse to set
	 */
	public void setKlasse(String klasse) {
		changes.firePropertyChange("klasse", this.klasse, this.klasse = klasse);
	}

	/**
	 * @return the klasse
	 */
	public String getKlasse() {
		return klasse;
	}
	
	
	/**
	 * @return the anmerkungen
	 */
	public String getAnmerkungen() {
		return anmerkungen;
	}

	/**
	 * @param anmerkungen the anmerkungen to set
	 */
	public void setAnmerkungen(String anmerkungen) {
		changes.firePropertyChange("anmerkungen", this.anmerkungen, this.anmerkungen = anmerkungen);
	}
	
	
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		changes.firePropertyChange("gender", this.gender, this.gender = gender);
	}

	public boolean isOut() {
		return out;
	}

	public void setOut(boolean out) {
		changes.firePropertyChange("out", this.out, this.out = out);
	}

	public void eintragen(){
		try{
			if(id == -1){
				Statement state = DBManager.getIt().getConnection().createStatement();
				ResultSet set = state.executeQuery("select "+DBManager.SEQ_SCHUELER+".nextval from dual");
				set.next();
				id = set.getInt(1);
				set.close();
				state.execute(
						String.format("insert into "+DBManager.TABLE_SCHUELER+" (id,nachname,vorname,klasse,anmerkungen,male) values (%d, '%s', '%s', '%s', '%s', %d)",
								id,nachname,vorname,klasse,anmerkungen, gender==Gender.MALE?1:0));
				state.close();

				Logger.logEvent("Schueler.eintragen", toString());
			}else{
				Statement state = DBManager.getIt().getConnection().createStatement();
				state.execute(
					String.format("update "+DBManager.TABLE_SCHUELER+" set nachname='%s', vorname='%s', klasse='%s', anmerkungen='%s', male=%d   where id = %d",
							nachname,vorname,klasse,anmerkungen,gender==Gender.MALE?1:0,id));
				state.close();

				Logger.logEvent("Schueler.update", toString());
			}
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}

	}
	
	public void entfernen(){
		if(id == -1) return;
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			state.execute(
				String.format("delete from "+DBManager.TABLE_SCHUELER+" where id = %d",id));
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		
		Logger.logEvent("Schueler.entfernen", toString());
	}
	
	/**
	 * Aktive Ausleihen ohne Vormerkungen und ohne überfällige
	 * 
	 * Sollte genau eine sein.
	 * @return
	 */
	public Vector<Ausleihe> getAusleihen(){
		Vector<Ausleihe> vec = new Vector<Ausleihe>();
		if(id == -1) return vec;
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery(
				String.format("select * from "+DBManager.TABLE_AUSLEIHEN+" where done = 0 and bis > sysdate - 1 and s_id = "+id));
			while(set.next())
				vec.add(new Ausleihe(set));
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return vec;
	}
	
	
	/**
	 * Aktive Ausleihen MIT Vormerkungen und MIT überfälligen
	 * 
	 * Sollte genau eine sein.
	 * @return
	 */
	public Vector<Ausleihe> getAllOpenAusleihen(){
		Vector<Ausleihe> vec = new Vector<Ausleihe>();
		if(id == -1) return vec;
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery(
				String.format("select * from "+DBManager.TABLE_AUSLEIHEN+" where done = 0 and s_id = "+id));
			while(set.next())
				vec.add(new Ausleihe(set));
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return vec;
	}
	
	public Vector<Ausleihe> getAusleihenTooLate(){
		Vector<Ausleihe> vec = new Vector<Ausleihe>();
		if(id == -1) return vec;
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery(
				String.format("select * from "+DBManager.TABLE_AUSLEIHEN+" where done = 0 and s_id = "+id+" and bis < sysdate-1"));
			while(set.next())
				vec.add(new Ausleihe(set));
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return vec;
	}
	
	public Ausleihe getAusleihe(String isbn){
		Ausleihe b = null;
		if(id == -1) return b;
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery(
				String.format(
						"select a.id from "+DBManager.TABLE_AUSLEIHEN+" a, "+DBManager.TABLE_BUECHER+" b where done = 0 and s_id = %d and a.b_id = b.id and b.isbn = %s", id, isbn));
			if(set.next())
					b = Ausleihe.fromId(set.getInt(1));
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return b;
	}


	public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Schueler [id=" + id + ", vorname=" + vorname + ", nachname="
				+ nachname + ", klasse=" + klasse + "]";
	}
	
	public String toNiceString(){
		return vorname+" "+nachname+ " ("+klasse+")";
	}
	
	
	public boolean matches(int id, String vorname, String nachname, String klasse){
		boolean match = true;
		
		if(id != -1 && id != 0 && this.id != id){
			match = false;
			return match;
		}
		
		if(vorname != null && vorname.length() != 0 && !this.vorname.matches("(?i)"+Pattern.quote(vorname)+".*")){
			match = false;
			return match;
		}
		if(nachname != null && nachname.length() != 0 && !this.nachname.matches("(?i)"+Pattern.quote(nachname)+".*")){
			match = false;
			return match;
		}
		if(klasse != null && klasse.length() != 0 && !this.klasse.matches("(?i)"+Pattern.quote(klasse)+".*")){
			match = false;
			return match;
		}
		
		return match;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Schueler){
			if(((Schueler) obj).id == id) return true;
		}
		return false;
	}
	

	@Override
	public int compareTo(Schueler o) {
		//Klasse
		int c = klasse.compareTo(o.getKlasse());
		if(c != 0) return c;
		//Nachname
		c = nachname.compareTo(o.getNachname());
		if(c != 0) return c;
		//Vornmae
		c = vorname.compareTo(o.getVorname());
		if(c != 0) return c;
		
		return 0;
	}
	
	public StyledString toStyledString(){
		StyledString s = new StyledString();
		
		s.append(vorname+" ");
		s.append(nachname, FontUtil.boldStyler());
		s.append(" ");
		s.append("("+klasse+")", new Styler(){
			@Override
			public void applyStyles(TextStyle textStyle) {
				textStyle.font = FontUtil.systemFont;
				textStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_GRAY);
			}});
		
		return s;
	}
	
	
	public Vector<Info> getInfos(){
		Vector<Info> vec = new Vector<Info>();
		
		if(id == -1) return vec;
		
		Vector<Ausleihe> tooLate = getAusleihenTooLate();
		for(Ausleihe a: tooLate)
			vec.add(new Info(this, a, a.getB(), Info.INFO_UEBERFAELLIG));
		
		Vector<Ausleihe> ausleihen = getAusleihen();
		for(Ausleihe a: ausleihen)
			if(a.isVorgemerkt()) vec.add(new Info(this, a, a.getB(), Info.INFO_VORGEMERKT));
			else vec.add(new Info(this, a, a.getB(), Info.INFO_VERLIEHEN));
		
		
		
		return vec;
	}
	
	public boolean isDefault(){
		if(id == -1 && nachname.equals("NN") && vorname.equals("NN")) return true;
		else return false;
	}
	
	public static Vector<Schueler> getAllSchueler(){
		Vector<Schueler> ret = new Vector<Schueler>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_SCHUELER);
			while(set.next()){
				Schueler b = new Schueler(set);
				ret.add(b);
			}
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return ret;
	}
	
	public static void getAllSchueler(final WritableList data){
		data.clear();
		new Thread(){
			public void run() {
				try{
					Statement state = DBManager.getIt().getConnection().createStatement();
					ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_SCHUELER);
					final ArrayList<Schueler> vec = new ArrayList<Schueler>();
					final MutableInteger curStart = new MutableInteger(0);
					while(set.next()){
						Schueler b = new Schueler(set);
						vec.add(b);
						if(vec.size()%10 == 0){
							data.getRealm().asyncExec(new Runnable() {
								@Override
								public void run() {
									int curSize = vec.size();
									int cur = curStart.getValue();
									int curEnd = (int)Math.min(curSize, cur+10);
									curStart.setValue(curEnd);
									
									if(cur < curSize && curEnd <= curSize)
										data.addAll(vec.subList(cur, curEnd));
								}
							});
						}
					}
					data.getRealm().asyncExec(new Runnable() {
						@Override
						public void run() {
							int curSize = vec.size();
							int cur = curStart.getValue();
							int curEnd = (int)Math.min(curSize, cur+10);
							curStart.setValue(curEnd);
							
							if(cur < curSize && curEnd <= curSize)
								data.addAll(vec.subList(cur, curEnd));
						}
					});
					set.close();
					state.close();
				}catch(SQLException ex){
					Logger.logError(ex.getMessage());
				}
			}
		}.start();
		
	}
	
	public static Schueler fromId(int id){
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_SCHUELER+" WHERE id = "+id);
			set.next();
			Schueler s = new Schueler(set);
			set.close();
			state.close();
			return s;
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return null;
	}
	
	public static Vector<String> getTop(int anzahl){
		Vector<String> vec = new Vector<String>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery(String.format("select count(*), s.nachname, s.vorname, s.klasse from %s a, %s s " +
					"where a.s_id = s.id " +
					"group by s.nachname, s.vorname, s.klasse order by count(*) desc", DBManager.TABLE_AUSLEIHEN, DBManager.TABLE_SCHUELER));
			while(set.next()){
				vec.add(String.format("%d x  %s %s (%s)",set.getInt(1), set.getString(3), set.getString(2), set.getString(4)));
			}
			set.close();
			state.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return vec;
	}
	
	public static int getCount(){
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select count(*) from "+DBManager.TABLE_SCHUELER);
			set.next();
			int ret = set.getInt(1);
			set.close();
			state.close();
			return ret;
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return 0;
	}
	
	public static int getCountAktuellVerliehen(){
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select count(distinct s_id) from "+DBManager.TABLE_AUSLEIHEN+" where done = 0 and vorgemerkt = 0");
			set.next();
			int ret = set.getInt(1);
			set.close();
			state.close();
			return ret;
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return 0;
	}
	
	public static int getCountPassiv(){
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select count(*) from "+DBManager.TABLE_SCHUELER+" where id not in (select distinct s_id from "+DBManager.TABLE_AUSLEIHEN+")");
			set.next();
			int ret = set.getInt(1);
			set.close();
			state.close();
			return ret;
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return 0;
	}
	
	public static Vector<Schueler> getPassiv(){
		Vector<Schueler> vec = new Vector<Schueler>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select * from "+DBManager.TABLE_SCHUELER+" where id not in (select distinct s_id from "+DBManager.TABLE_AUSLEIHEN+")");
			while(set.next())
				vec.add(new Schueler(set));
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return vec;
	}
	
	public static Vector<Schueler> getActive(){
		Vector<Schueler> vec = new Vector<Schueler>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select * from "+DBManager.TABLE_SCHUELER+" where id in (select distinct s_id from "+DBManager.TABLE_AUSLEIHEN+" where done = 0)");
			while(set.next())
				vec.add(new Schueler(set));
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		Collections.sort(vec);
		return vec;
	}
}
