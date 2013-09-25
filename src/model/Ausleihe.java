package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.core.databinding.observable.list.WritableList;

import log.Logger;
import util.DateUtils;
import db.DBManager;

public class Ausleihe implements IDefault{
	private Date von = new Date(System.currentTimeMillis()), bis = DateUtils.getTwoWeeks();
	private int id = -1;
	private Schueler s;
	private Buch b;
	private boolean isVorgemerkt;
	private String anmerkungen = "";
	private Timestamp rueckdate;
	
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	public Ausleihe(){
		
	}
	
	public Ausleihe(ResultSet set){
		try{
			id = set.getInt("id");
			von = set.getDate("von");
			bis = set.getDate("bis");
			isVorgemerkt = set.getBoolean("vorgemerkt");
			anmerkungen = set.getString("anmerkungen");
			rueckdate = set.getTimestamp("rueckdate");
			
			//get Schüler und Buch
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set2 = state.executeQuery("select * from "+DBManager.TABLE_SCHUELER+" where id = "+set.getInt("s_id"));
			set2.next();
			s = new Schueler(set2);
			set2.close();
			set2 = state.executeQuery("select * from "+DBManager.TABLE_BUECHER+" where id = "+set.getInt("b_id"));
			set2.next();
			b = new Buch(set2);
			set2.close();
			state.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		if(anmerkungen == null) anmerkungen = "";
	}
		
	public Ausleihe(Buch b, Schueler s, Date von, Date bis, boolean isVorgemerkt) {
		super();
		this.b = b;
		this.s = s;
		this.von = von;
		this.bis = bis;
		this.isVorgemerkt = isVorgemerkt;
	}
	
	
	/**
	 * @return the start
	 */
	public Date getVon() {
		return von;
	}
	/**
	 * @param start the start to set
	 */
	public void setVon(Date start) {
		changes.firePropertyChange("bis", this.bis, this.bis = DateUtils.getDaysAdded(start, getDauer()));
		changes.firePropertyChange("von", this.von, this.von = start);
	}
	
	
	/**
	 * @return the end
	 */
	public Date getBis() {
		return bis;
	}
	/**
	 * @param end the end to set
	 */
	public void setBis(Date end) {
		changes.firePropertyChange("von", this.von, this.von = DateUtils.getDaysAdded(end, -getDauer()));
		changes.firePropertyChange("bis", this.bis, this.bis= end);
	}
	
	public void setDauer(int dauer){
		int old = getDauer();
		changes.firePropertyChange("bis", this.bis, this.bis= DateUtils.getDaysAdded(von, dauer));
		changes.firePropertyChange("dauer", old, getDauer());
	}
	
	public int getDauer(){
		return DateUtils.getDifferenceInDays(von, bis);
	}
	
	/**
	 * @return the isVorgemerkt
	 */
	public boolean isVorgemerkt() {
		return isVorgemerkt;
	}
	
	/**
	 * @param isVorgemerkt the isVorgemerkt to set
	 */
	public void setVorgemerkt(boolean isVorgemerkt) {
		changes.firePropertyChange("vorgemerkt", this.isVorgemerkt, this.isVorgemerkt = isVorgemerkt);
		correctVonDate();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @return the s
	 */
	public Schueler getS() {
		return s;
	}


	/**
	 * @param s the s to set
	 */
	public void setS(Schueler s) {
		changes.firePropertyChange("s", this.s, this.s = s);
	}


	/**
	 * @return the b
	 */
	public Buch getB() {
		return b;
	}


	/**
	 * @param b the b to set
	 */
	public void setB(Buch b) {
		changes.firePropertyChange("b", this.b, this.b = b);
		changes.firePropertyChange("von", null, von);
		changes.firePropertyChange("bis", null, bis);
		correctVonDate();
	}
	
	/**
	 * Setzt korrektes Startdatum, d.h. Termin, ab dem das gewählte Medium verfügbar ist.
	 */
	private void correctVonDate(){
		if(isVorgemerkt && b != null)
			setVon(b.getNextFreeDate());
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
	
	

	/**
	 * @return the rueckgabe
	 */
	public Date getRueckdate() {
		if(rueckdate == null) return null;
		return new Date(rueckdate.getTime());
	}

	/**
	 * Neuen Datensatz erstellen, oder vorhandenen Datensatz updaten.
	 */
	public void eintragen(){
		try{
			if(id == -1){
				Statement state = DBManager.getIt().getConnection().createStatement();
				ResultSet set = state.executeQuery("select "+DBManager.SEQ_AUSLEIHEN+".nextval from dual");
				set.next();
				id = set.getInt(1);
				state.execute(String.format(
					"insert into "+DBManager.TABLE_AUSLEIHEN+" (id,s_id,b_id,vorgemerkt,von,bis,anmerkungen) values (%d, %d, %d, %d, to_date('%s', 'yyyy-mm-dd'), to_date('%s', 'yyyy-mm-dd'),'%s')",
					id,s.getId(),b.getId(),isVorgemerkt?1:0, von, bis,anmerkungen));
				set.close();
				state.close();

				Logger.logEvent("Ausleihe.eintragen", toString());
			}else{
				Statement state = DBManager.getIt().getConnection().createStatement();
				state.execute(String.format(
						"update "+DBManager.TABLE_AUSLEIHEN+" set s_id=%d, b_id=%d, vorgemerkt=%d, von=to_date('%s', 'yyyy-mm-dd'), bis=to_date('%s', 'yyyy-mm-dd'), anmerkungen='%s' where id = %d",
						s.getId(),b.getId(),isVorgemerkt?1:0, von, bis, anmerkungen,id));
				state.close();

				Logger.logEvent("Ausleihe.update", toString());
			}
		
			//check
			if(Ausleihe.fromId(id) == null){ 
				id=-1;
				eintragen();
			}
			
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}

	}
	
	public void zuruckgeben(){
		if(id == -1) return;
		bis = new Date(System.currentTimeMillis());
		
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			state.execute(String.format(
					"update "+DBManager.TABLE_AUSLEIHEN+" set done=1, bis = to_date('%s', 'yyyy-mm-dd'), rueckdate=systimestamp where id=%d",bis,id));
			state.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		Logger.logEvent("Ausleihe.zurückgeben", toString());
	}
	
	public void entfernen(){
		if(id == -1) return;
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			state.execute("delete from "+DBManager.TABLE_AUSLEIHEN+" where id="+id);
			state.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		} 
		Logger.logEvent("Ausleihe.entfernen", toString());
	}
	
	public Schueler getVorgemerktAn(){
		if(id == -1) return null;
		try{
			Schueler s = null;
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery(String.format(
					"select s_id from "+DBManager.TABLE_AUSLEIHEN+" where b_id = %d and von >= to_date('%s', 'yyyy-mm-dd') and id != %d and vorgemerkt = 1 and done = 0 order by von asc",b.getId(), bis, id));
			if(set.next())
				s = Schueler.fromId(set.getInt(1));
			set.close();
			state.close();
			return s;
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Falls vorgemerkt, Ausleihe beginnen mit Startdatum = now.
	 */
	public void vorgemerktAusleihen(){
		if(!isVorgemerkt) return;
		von = new Date(System.currentTimeMillis());
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			state.execute(String.format(
					"update "+DBManager.TABLE_AUSLEIHEN+" set vorgemerkt = 0, von = to_date('%s', 'yyyy-mm-dd') where id=%d",von,id));
			state.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		} 
	}
	
	/**
	 * 
	 * @return tage, die das Medium schon überfällig ist, wobei am Tag der Abgabe als 0 zählt. 
	 */
	public int getDaysTooLate(){
		return DateUtils.getDifferenceToToday(bis);
	}
	
	
	public boolean isTooLate(){
		return DateUtils.getDifferenceToToday(bis)<0;
	}
	
	public boolean matches(String s_id, String b_isbn, boolean verliehenAn){
		boolean flag = true;
		
		if(b_isbn != null && b_isbn.length() != 0 && !b.getIsbn().equals(b_isbn)){
			flag = false;
			return flag;
		}
		try{
			if(verliehenAn){
				if(s_id != null && s_id.length() != 0 && s.getId() != new Integer(s_id)){
					flag = false;
					return flag;
				}
			}else{
				if(s_id != null && s_id.length() != 0 && getVorgemerktAn() != null && getVorgemerktAn().getId() != new Integer(s_id)){
					flag = false;
					return flag;
				}
			}
		}catch(NumberFormatException ex){
			flag = false;
		}
		
		return flag;
	}
	
	public void reactivate(){
		if(id == -1) return;
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			state.execute("update "+DBManager.TABLE_AUSLEIHEN+" set done = 0, rueckdate = null where id="+id);
			state.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}

		Logger.logEvent("Ausleihe.reactivate", toString());
	}
	
	public boolean getDone(){
		if(id == -1) return false;
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select done from "+DBManager.TABLE_AUSLEIHEN+" where id="+id);
			set.next();
			boolean done = set.getBoolean(1);
			set.close();
			state.close();
			return done;
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Ausleihe){
			if(((Ausleihe) obj).id == id) return true;
		}
		return false;
	}
	
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }
    
    @Override
    public String toString() {
    	return String.format("[Ausleihe id=%d, von=%s, bis=%s, buch.id=%d, schueler.id=%d, isVorgemertk=%b, rueckdate=%s, anmerkungen=%s]",
    			id,DateUtils.shortFormat.format(von), DateUtils.shortFormat.format(bis), b.getId(), s.getId(), isVorgemerkt, 
    			rueckdate==null?"null":DateUtils.longFormat.format(rueckdate), anmerkungen);
    }
	
	@Override
	public boolean isDefault() {
		if(id==-1 && s == null && b == null) return true;
		return false;
	}
	
	public static Vector<Ausleihe> getAllAusleihen(){
		Vector<Ausleihe> ret = new Vector<Ausleihe>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_AUSLEIHEN+"");
			while(set.next()){
				ret.add(new Ausleihe(set));
			}
			set.close();
			state.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return ret;
	}
	
	public static void getAllAusleihen(final WritableList data){
		data.clear();
		new Thread(){
			public void run() {
				try{
					Statement state = DBManager.getIt().getConnection().createStatement();
					ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_AUSLEIHEN+"");
					final ArrayList<Ausleihe> vec = new ArrayList<Ausleihe>();
					while(set.next()){
						Ausleihe b = new Ausleihe(set);
						vec.add(b);
						if(vec.size() >= 10){
							data.getRealm().asyncExec(new Runnable() {
								@Override
								public void run() {
									data.addAll(vec);
									vec.clear();
								}
							});
						}
					}
					data.getRealm().asyncExec(new Runnable() {
						@Override
						public void run() {
							data.addAll(vec);
							vec.clear();
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
	
	public static Vector<Ausleihe> getAllDone(){
		Vector<Ausleihe> ret = new Vector<Ausleihe>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_AUSLEIHEN+" where done = 1 order by rueckdate desc");
			while(set.next()){
				ret.add(new Ausleihe(set));
			}
			set.close();
			state.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return ret;
	}
	
	public static void getAllDone(final WritableList data){
		data.clear();
		new Thread(){
			public void run() {
				try{
					Statement state = DBManager.getIt().getConnection().createStatement();
					ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_AUSLEIHEN+" where done = 1 order by rueckdate desc");
					final ArrayList<Ausleihe> vec = new ArrayList<Ausleihe>();
					while(set.next()){
						Ausleihe b = new Ausleihe(set);
						vec.add(b);
						if(vec.size() >= 10){
							data.getRealm().asyncExec(new Runnable() {
								@Override
								public void run() {
									data.addAll(vec);
									vec.clear();
								}
							});
						}
					}
					data.getRealm().asyncExec(new Runnable() {
						@Override
						public void run() {
							data.addAll(vec);
							vec.clear();
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
	
	public static Vector<Ausleihe> getAllTooLate(){
		Vector<Ausleihe> ret = new Vector<Ausleihe>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_AUSLEIHEN+" where done = 0 AND bis < sysdate-1 order by bis asc");
			while(set.next()){
				ret.add(new Ausleihe(set));
			}
			set.close();
			state.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return ret;
	}
	
	public static void getAllTooLate(final WritableList data){
		data.clear();
		new Thread(){
			public void run() {
				try{
					Statement state = DBManager.getIt().getConnection().createStatement();
					ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_AUSLEIHEN+" where done = 0 AND bis < sysdate-1 order by bis asc");
					final ArrayList<Ausleihe> vec = new ArrayList<Ausleihe>();
					while(set.next()){
						Ausleihe b = new Ausleihe(set);
						vec.add(b);
						if(vec.size() >= 10){
							data.getRealm().asyncExec(new Runnable() {
								@Override
								public void run() {
									data.addAll(vec);
									vec.clear();
								}
							});
						}
					}
					data.getRealm().asyncExec(new Runnable() {
						@Override
						public void run() {
							data.addAll(vec);
							vec.clear();
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
	
	public static Vector<Ausleihe> getAllOpen(){
		Vector<Ausleihe> ret = new Vector<Ausleihe>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_AUSLEIHEN+" where done = 0");
			while(set.next()){
				ret.add(new Ausleihe(set));
			}
			set.close();
			state.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return ret;
	}
	
	public static void getAllOpen(final WritableList data){
		data.clear();
		new Thread(){
			public void run() {
				try{
					Statement state = DBManager.getIt().getConnection().createStatement();
					ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_AUSLEIHEN+" where done = 0");
					final ArrayList<Ausleihe> vec = new ArrayList<Ausleihe>();
					while(set.next()){
						Ausleihe b = new Ausleihe(set);
						vec.add(b);
						if(vec.size() >= 10){
							data.getRealm().asyncExec(new Runnable() {
								@Override
								public void run() {
									data.addAll(vec);
									vec.clear();
								}
							});
						}
					}
					data.getRealm().asyncExec(new Runnable() {
						@Override
						public void run() {
							data.addAll(vec);
							vec.clear();
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
	public static Vector<Ausleihe> getVorgemerkte(){
		Vector<Ausleihe> ret = new Vector<Ausleihe>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_AUSLEIHEN+" where done = 0 and vorgemerkt = 1");
			while(set.next()){
				ret.add(new Ausleihe(set));
			}
			set.close();
			state.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return ret;
	}
	
	public static void getVorgemerkte(final WritableList data){
		data.clear();
		new Thread(){
			public void run() {
				try{
					Statement state = DBManager.getIt().getConnection().createStatement();
					ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_AUSLEIHEN+" where done = 0 and vorgemerkt = 1");
					final ArrayList<Ausleihe> vec = new ArrayList<Ausleihe>();
					while(set.next()){
						Ausleihe b = new Ausleihe(set);
						vec.add(b);
						if(vec.size() >= 10){
							data.getRealm().asyncExec(new Runnable() {
								@Override
								public void run() {
									data.addAll(vec);
									vec.clear();
								}
							});
						}
					}
					data.getRealm().asyncExec(new Runnable() {
						@Override
						public void run() {
							data.addAll(vec);
							vec.clear();
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
	
	public static Ausleihe fromId(int id){
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_AUSLEIHEN+" WHERE id = "+id);
			set.next();
			Ausleihe a = new Ausleihe(set);
			set.close();
			state.close();
			return a;
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public static Vector<Ausleihe> getHistoryFor(Schueler s){
		Vector<Ausleihe> ret = new Vector<Ausleihe>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_AUSLEIHEN+" where done = 1 and s_id = "+s.getId()+" order by rueckdate desc");
			while(set.next()){
				ret.add(new Ausleihe(set));
			}
			set.close();
			state.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return ret;
	}
	
	public static Vector<Ausleihe> getHistoryFor(Buch b){
		Vector<Ausleihe> ret = new Vector<Ausleihe>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_AUSLEIHEN+" where done = 1 and b_id = "+b.getId()+" order by rueckdate desc");
			while(set.next()){
				ret.add(new Ausleihe(set));
			}
			set.close();
			state.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return ret;
	}
	
}
