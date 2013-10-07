package model;

import gui.StringConstants;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Pattern;

import log.Logger;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;
import org.jdom2.Element;

import util.ArrayUtils;
import util.FontUtil;
import util.MutableInteger;
import db.DBManager;

public class Buch implements Comparable<Buch>, IStringable, IDefault{
	
	public static Vector<Buch> getAllBuecher(){
		Vector<Buch> ret = new Vector<Buch>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_BUECHER+"");
			while(set.next()){
				Buch b = new Buch(set);
				ret.add(b);
			}
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return ret;
	}
	
	public static void getAllBuecher(final WritableList data){
		data.clear();
		new Thread(){
			public void run() {
				try{
					Statement state = DBManager.getIt().getConnection().createStatement();
					ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_BUECHER);
					final ArrayList<Buch> vec = new ArrayList<Buch>();
					final MutableInteger curStart = new MutableInteger(0);
					while(set.next()){
						Buch b = new Buch(set);
						vec.add(b);
						if(vec.size()%20 == 0){
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
	
	private int id = -1;
	private String isbn="", autor="NN", titel="NN", jahr="", anmerkungen="";
	private int art=0, stufe=0;
	private Vector<String> stichwort = new Vector<String>();
	private Kategorie kategorie = Kategorie.fromId(Kategorie.ALLE_ID);

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	
	
	public Buch(){
		
	}
	
	public Buch(ResultSet set){
		try{
			id = set.getInt("id");
			isbn = set.getString("isbn");
			autor = set.getString("autor");
			titel = set.getString("titel");
			jahr = set.getString("jahr");
			art = set.getInt("art");
			stufe = set.getInt("stufe");
			stichwort = ArrayUtils.commaSeparatedString2vec(set.getString("stichwort"));
			anmerkungen = set.getString("anmerkungen");
			kategorie = Kategorie.fromId(set.getInt("kategorie_id"));
			
			changes.firePropertyChange("anzahl", null, getAnzahl());
			
		}catch(SQLException ex){}
		if(jahr == null) jahr = "";
		if(anmerkungen == null) anmerkungen = "";
	}
	
	public int getId() {
		return id;
	}
	
	
	/**
	 * @return the isbn
	 */
	public String getIsbn() {
		return isbn;
	}

	/**
	 * @param isbn the isbn to set
	 */
	public void setIsbn(String isbn) {
		changes.firePropertyChange("isbn", this.isbn, this.isbn = isbn);
	}

	/**
	 * @return the autor
	 */
	public String getAutor() {
		return autor;
	}

	/**
	 * @param autor the autor to set
	 */
	public void setAutor(String autor) {
		changes.firePropertyChange("autor", this.autor, this.autor = autor);
	}

	/**
	 * @return the titel
	 */
	public String getTitel() {
		return titel;
	}

	/**
	 * @param titel the titel to set
	 */
	public void setTitel(String titel) {
		changes.firePropertyChange("titel", this.titel, this.titel = titel);
	}

	/**
	 * @return the jahr
	 */
	public String getJahr() {
		return jahr;
	}

	/**
	 * @param jahr the jahr to set
	 */
	public void setJahr(String jahr) {
		changes.firePropertyChange("jahr", this.jahr, this.jahr = jahr);
	}

	/**
	 * @return the art
	 */
	public int getMedienart() {
		return art;
	}

	/**
	 * @param art the art to set
	 */
	public void setMedienart(int art) {
		changes.firePropertyChange("medienart", this.art, this.art = art);
	}

	/**
	 * @return the stufe
	 */
	public int getStufe() {
		return stufe;
	}

	/**
	 * @param stufe the stufe to set
	 */
	public void setStufe(int stufe) {
		changes.firePropertyChange("stufe", this.stufe, this.stufe = stufe);
	}
	
	public Vector<String> getStichworterVec(){
		return (Vector<String>) stichwort.clone();
	}
	
	public String getStichworter(){
		return ArrayUtils.vec2CommaSeparatedString(stichwort);
	}
	
	public void setStichworter(String s){
		stichwort = ArrayUtils.commaSeparatedString2vec(s);
		changes.firePropertyChange("stichworter", null, stichwort);
	}
	
	public void addStrichwort(String wort){
		if(!stichwort.contains(wort)) stichwort.add(wort);
		changes.firePropertyChange("stichworter", null, stichwort);
	}
	
	public boolean hasStichwort(String wort){
		return stichwort.contains(wort);
	}
	
	public void removeStichwort(String wort){
		stichwort.remove(wort);
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
	 * @return the kategorie
	 */
	public Kategorie getKategorie() {
		return kategorie;
	}

	/**
	 * @param kategorie the kategorie to set
	 */
	public void setKategorie(Kategorie kategorie) {
		changes.firePropertyChange("kategorie", this.kategorie, this.kategorie = kategorie);
	}

	public void eintragen(){
		eintragen(false);
	}
	
	public void eintragen(boolean avoidUpdate){
		//Catch unhandled exceptions
		//isbn, autor, titel must be set
		if(isbn == null || isbn.equals("") ||
		   autor == null || autor.equals("") ||
		   titel == null || titel.equals("")) return;
		try{
			if(id == -1){
				Statement state = DBManager.getIt().getConnection().createStatement();
				ResultSet set = state.executeQuery("select "+DBManager.SEQ_BUECHER+".nextval from dual");
				set.next();
				id = set.getInt(1);
				set.close();
				PreparedStatement ps = DBManager.getIt().getConnection().prepareStatement("insert into "+DBManager.TABLE_BUECHER+" (id,isbn,autor,titel,jahr,art,stufe,stichwort,anmerkungen,kategorie_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				ps.setInt(1, id);
				ps.setString(2, isbn);
				ps.setString(3, autor);
				ps.setString(4, titel);
				ps.setString(5, jahr);
				ps.setInt(6, art);
				ps.setInt(7, stufe);
				ps.setString(8, ArrayUtils.vec2CommaSeparatedString(stichwort));
				ps.setString(9, anmerkungen);
				ps.setInt(10, kategorie.getId());
				ps.execute();
				ps.close();

				Logger.logEvent("Buch.eintragen", toString());
			}else{
				PreparedStatement ps = DBManager.getIt().getConnection().prepareStatement("update "+DBManager.TABLE_BUECHER+" set isbn=?, autor=?, titel=?, jahr=?, art=?, stufe=?, stichwort=?, anmerkungen=?, kategorie_id=? where id = ?");
				ps.setString(1, isbn);
				ps.setString(2, autor);
				ps.setString(3, titel);
				ps.setString(4, jahr);
				ps.setInt(5, art);
				ps.setInt(6, stufe);
				ps.setString(7, ArrayUtils.vec2CommaSeparatedString(stichwort));
				ps.setString(8, anmerkungen);
				ps.setInt(9, kategorie.getId());
				ps.setInt(10, id);
				ps.execute();
				ps.close();

				Logger.logEvent("Buch.update", toString());
			}
			if(!avoidUpdate)updateEquals();

		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}

	}
	
	/**
	 * update alle Bücher mit identischer ISBN.
	 */
	public void updateEquals(){
		Vector<Buch> alle = Buch.getFromIsbn(isbn);
		for(Buch b: alle){
			if(!b.deepEquals(this)){
				boolean ret = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Achtung!", StringConstants.CONFIRM_DIFFERENT_VALUES);
				if(!ret) return;
				try{
					PreparedStatement ps = DBManager.getIt().getConnection().prepareStatement("update "+DBManager.TABLE_BUECHER+" set isbn=?, autor=?, titel=?, jahr=?, art=?, stufe=?, stichwort=?, anmerkungen=?, kategorie_id=? where isbn = ?");
					ps.setString(1, isbn);
					ps.setString(2, autor);
					ps.setString(3, titel);
					ps.setString(4, jahr);
					ps.setInt(5, art);
					ps.setInt(6, stufe);
					ps.setString(7, ArrayUtils.vec2CommaSeparatedString(stichwort));
					ps.setString(8, anmerkungen);
					ps.setInt(9, kategorie.getId());
					ps.setString(10, isbn);
					ps.execute();
					ps.close();
				}catch(SQLException ex){
					Logger.logError(ex.getMessage());
				}

				Logger.logEvent("Buch.updateEquals", b.toString());
			}
		}
	}
	
	public void entfernen(){
		if(id == -1) return;
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			state.execute(
				String.format("delete from "+DBManager.TABLE_BUECHER+" where id = %d", id));
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}

		Logger.logEvent("Buch.entfernen", toString());
	}
	
	public boolean isVerliehen(){
		boolean flag = false;
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select count(*) from "+DBManager.TABLE_AUSLEIHEN+" where b_id = "+id+" and done = 0");
			set.next();
			if(set.getInt(1) > 0) flag = true;
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return flag;
	}
	
	public boolean isVorgemerkt(){
		boolean flag = false;
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select count(*) from "+DBManager.TABLE_AUSLEIHEN+" where b_id = "+id+" AND vorgemerkt = 1 and done = 0");
			set.next();
			if(set.getInt(1) > 0) flag = true;
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return flag;
	}
	
	public boolean isVorgemerkt(Date von, Date bis, Ausleihe not){
		boolean flag = false;
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			String add = "";
			if(not != null && not.getId() != -1) add = "AND id != "+not.getId();
			ResultSet set = state.executeQuery(String.format("select count(*) from "+DBManager.TABLE_AUSLEIHEN+" where b_id = "+id+" " +
					"AND done = 0 AND vorgemerkt = 1 AND (" +
					"(von <= to_date('%s', 'yyyy-mm-dd') AND bis < to_date('%s', 'yyyy-mm-dd')) OR " +
					"(von < to_date('%s', 'yyyy-mm-dd') AND bis >= to_date('%s', 'yyyy-mm-dd')) OR " +
					"(von >= to_date('%s', 'yyyy-mm-dd') AND bis <= to_date('%s', 'yyyy-mm-dd'))) %s",
					von, von, bis, bis, von, bis,add));
			set.next();
			if(set.getInt(1) > 0) flag = true;
			set.close(); 
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return flag;
	}
	
	public boolean isVerliehen(Date von, Date bis, Ausleihe not){
		boolean flag = false;
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			String add = "";
			if(not != null && not.getId() != -1) add = "AND id != "+not.getId();
			ResultSet set = state.executeQuery(String.format("select count(*) from "+DBManager.TABLE_AUSLEIHEN+" where b_id = "+id+" " +
					"AND done = 0 AND vorgemerkt = 0 AND (" +
					"(von <= to_date('%s', 'yyyy-mm-dd') AND bis > to_date('%s', 'yyyy-mm-dd')) OR " +
					"(von < to_date('%s', 'yyyy-mm-dd') AND bis >= to_date('%s', 'yyyy-mm-dd')) OR " +
					"(von >= to_date('%s', 'yyyy-mm-dd') AND bis <= to_date('%s', 'yyyy-mm-dd'))) %s",
					von, von, bis, bis, von, bis,add));
			set.next();
			if(set.getInt(1) > 0) flag = true;
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return flag;
	}
	
	
	public boolean isVerfuegbar(){
		return !isVerliehen();
	}
	
	/**
	 * 
	 * @return status - 0: verfügbar, 1: verliehen, 2: vorgemerkt
	 */
	public int getStatus(){
		boolean verliehen = isVerliehen(); 
		if(verliehen && isVorgemerkt()) return 2;
		if(verliehen) return 1;
		return 0;
	}
	
//	public void zurueckgeben(){
//		if(id == -1) return;
//		Vector<Ausleihe> v = getAusleihen();
//		if(v.size() == 0) return;
//		v.get(0).zuruckgeben();
//		
//		changes.firePropertyChange("status", null, getStatus());
//	}
	
	
	/**
	 * int wieviele Bücher mit selbiger ISBN im Bestand vorkommen.
	 * @return
	 */
	public int getAnzahl(){
		if(id == -1) return 0;
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select count(id) from "+DBManager.TABLE_BUECHER+" where isbn = "+isbn);
			set.next();
			int i = set.getInt(1);
			set.close();
			state.close();
			return i;
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return 1;
	}
	
	/**
	 * Aktive Ausleihen ohne Vormerkungen
	 * @return
	 */
	public Vector<Ausleihe> getUeberfaellige(){
		if(id == -1) return null;
		Vector<Ausleihe> vec = new Vector<Ausleihe>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select * from "+DBManager.TABLE_AUSLEIHEN+" where done = 0 and vorgemerkt = 0 and bis < sysdate - 1 and b_id = "+id);
			while(set.next()) vec.add(new Ausleihe(set));
			set.close();
			state.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return vec;
	}
	
	/**
	 * Aktive Ausleihen ohne Vormerkungen und ohne überfällige
	 * 
	 * Sollte genau eine sein.
	 * @return
	 */
	public Vector<Ausleihe> getAusleihen(){
		if(id == -1) return null;
		Vector<Ausleihe> vec = new Vector<Ausleihe>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select * from "+DBManager.TABLE_AUSLEIHEN+" where done = 0 and vorgemerkt = 0 and bis > sysdate-1 and b_id = "+id);
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
	 * Aktive Vormerkungen
	 * @return
	 */
	public Vector<Ausleihe> getVormerkungen(){
		if(id == -1) return null;
		Vector<Ausleihe> vec = new Vector<Ausleihe>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select * from "+DBManager.TABLE_AUSLEIHEN+" where done = 0 and vorgemerkt = 1 and b_id = "+id);
			while(set.next())
				vec.add(new Ausleihe(set));
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return vec;
	}
	
	
	public Date getNextFreeDate(){
		Vector<Ausleihe> alle = getAusleihen();
		alle.addAll(getVormerkungen());
		alle.addAll(getUeberfaellige());
		if(alle.size() == 0) return new Date(System.currentTimeMillis());
		
		Date bis = alle.get(0).getBis();
		for(Ausleihe a: alle)
			if(a.getBis().after(bis))
				bis = a.getBis();
		
		return (Date) bis.clone();
	}
	
	
	public void addExemplar(){
		if(id == -1) return;
		id = -1;
		eintragen();
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
		return String.format("[Buch id=%d, isbn=%s, autor=%s, titel=%s, jahr=%s, art=%d, stufe=%s, anmerkungen=%s, stichwort=%s, kategorie=%d]",
				id, isbn, autor, titel, jahr, art, stufe, anmerkungen, ArrayUtils.vec2CommaSeparatedString(stichwort), kategorie.getId());
	}
	
	public Element toXML(){
		Element e = new Element("Buch");
		e.setAttribute("isbn", isbn);
		e.setAttribute("autor", autor);
		e.setAttribute("titel", titel);
		e.setAttribute("jahr", jahr);
		e.setAttribute("art", ""+art);
		e.setAttribute("stufe", ""+stufe);
		e.setAttribute("anmerkungen", anmerkungen);
		e.setAttribute("stichwort", ArrayUtils.vec2CommaSeparatedString(stichwort));
		e.setAttribute("kategorie", ""+kategorie.getId());
		return e;
	}
	
	public static Buch fromXML(Element e){
		try{
			Buch b = new Buch();
			b.isbn = e.getAttributeValue("isbn");
			b.autor = e.getAttributeValue("autor");
			b.titel = e.getAttributeValue("titel");
			b.jahr = e.getAttributeValue("jahr");
			b.art = new Integer(e.getAttributeValue("art"));
			b.stufe = new Integer(e.getAttributeValue("stufe"));
			b.anmerkungen = e.getAttributeValue("anmerkungen");
			b.stichwort = ArrayUtils.commaSeparatedString2vec(e.getAttributeValue("stichwort"));
			b.kategorie = Kategorie.fromId(new Integer(e.getAttributeValue("kategorie")));
			return b;
		}catch(Exception ex){}
		return null;
	}
	
	
	public String toNiceString(){
		return autor+" - "+titel;
	}
	
	public boolean matches(String isbn, String autor, String titel, String jahr, int art, int stufe, String stichworter, Kategorie kategorie, int status){
		boolean match = true;
		
		
		if(isbn != null && isbn.length() != 0 && !this.getIsbn().matches("(?i)"+Pattern.quote(isbn)+".*")){
			match = false;
			return match;
		}
		if(autor != null && autor.length() != 0 && !this.getAutor().matches("(?i).*"+Pattern.quote(autor)+".*")){
			match = false;
			return match;
		}
		if(titel != null && titel.length() != 0 && !this.getTitel().matches("(?i).*"+Pattern.quote(titel)+".*")){
			match = false;
			return match;
		}
		if(jahr != null && jahr.length() != 0 && !this.getJahr().matches("(?i).*"+Pattern.quote(jahr)+".*")){
			match = false;
			return match;
		}
		if(stufe != 0 && this.getStufe() != stufe){
			match = false;
			return match;
		}
		if(art != 0 && this.getMedienart() != art){
			match = false;
			return match;
		}
		if(stichworter != null && stichworter.length() != 0 && !ArrayUtils.matchesVec2Vec(ArrayUtils.commaSeparatedString2vec(stichworter), stichwort) && !ArrayUtils.matchesVec2String(ArrayUtils.commaSeparatedString2vec(stichworter), titel)){
			match = false;
			return match;
		}
		if(kategorie != null  && kategorie.getId() != Kategorie.ALLE_ID && this.kategorie.getId() != kategorie.getId()){
			match = false;
			return match;
		}
		if(status != -1 && this.getStatus() != status){
			match = false;
			return match;
		}
		
		return match;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Buch){
			if(((Buch) obj).id == id) return true;
		}
		return false;
	}
	
	public boolean deepEquals(Object obj){
		if(obj instanceof Buch){
			Buch b = (Buch)obj;
			if(!autor.equals(b.autor) || 
					!isbn.equals(b.isbn) ||
					!titel.equals(b.titel)||
					!jahr.equals(b.jahr) ||
					!anmerkungen.equals(b.anmerkungen) ||
					art != b.art ||
					stufe != b.stufe ||
					!kategorie.equals(b.kategorie) ||
					!stichwort.equals(b.stichwort)) return false;

			return true;
		}
		return false;
	}
	

	@Override
	public int compareTo(Buch o) {
		//Autor
		int c = autor.compareTo(o.getAutor());
		if(c != 0) return c;
		//Titel
		c = titel.compareTo(o.getTitel());
		if(c != 0) return c;
		//Jahr
		c = jahr.compareTo(o.getJahr());
		if(c != 0) return c;
		
		
		return 0;
	}
	
	/**
	 * Setzt alle Werte von b, incl. id.
	 * @param b
	 */
	public void setValues(Buch b){
		changes.firePropertyChange("id", id, id = b.id);
		setIsbn(b.isbn);
		setAutor(b.autor);
		setTitel(b.titel);
		setJahr(b.jahr);
		setAnmerkungen(b.anmerkungen);
		setKategorie(b.kategorie);
		setMedienart(b.art);
		setStufe(b.stufe);
		setStichworter(ArrayUtils.vec2CommaSeparatedString(b.stichwort));

		changes.firePropertyChange("anzahl", null, getAnzahl());
	}
	
	
	public StyledString toStyledString(){
		StyledString s = new StyledString();
		String art = this.art==0?"":BuchConstants.ARTEN.get(this.art);
		
		s.append("\u25CF", new Styler() {
			
			@Override
			public void applyStyles(TextStyle textStyle) {
				textStyle.foreground = kategorie.getColor();
				
			}
		});
		s.append(" "+autor+" - ");
		s.append(titel, FontUtil.boldStyler());
		
		s.append(" ");
		s.append(isbn, new Styler(){
			@Override
			public void applyStyles(TextStyle textStyle) {
				textStyle.font = FontUtil.italicFont;
				textStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_GRAY);
			}});
		
		s.append(" ("+art+")");
		
		return s;
	}
	
	@Override
	public boolean isDefault() {
		if(id==-1 && autor.equals("NN") && titel.equals("NN")) return true;
		return false;
	}
	
	public Vector<Info> getInfos(){
		Vector<Info> vec = new Vector<Info>();
		if(id == -1) return vec;
		Vector<Ausleihe> v = getUeberfaellige();
		for(Ausleihe a: v)
			vec.add(new Info(a.getS(), a, this, Info.INFO_UEBERFAELLIG));
		
		Vector<Ausleihe> ausleihen = getAusleihen();
		for(Ausleihe a: ausleihen)
			vec.add(new Info(a.getS(), a, this, Info.INFO_VERLIEHEN));
		
		v = getVormerkungen();
		for(Ausleihe a: v)
			vec.add(new Info(a.getS(), a, this, Info.INFO_VORGEMERKT));
		
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
				String.format("select * from "+DBManager.TABLE_AUSLEIHEN+" where done=0 and b_id="+id));
			while(set.next())
				vec.add(new Ausleihe(set));
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return vec;
	}
	
	public static Buch fromId(int id){
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("SELECT * from "+DBManager.TABLE_BUECHER+" WHERE id = "+id);
			set.next();
			Buch b = new Buch(set);
			set.close();
			state.close();
			return b;
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return null;
	}

	public static Vector<String[]> getTop(int anzahl){
		Vector<String[]> vec = new Vector<String[]>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery(String.format("select count(*), b.isbn, b.autor, b.titel from %s a, %s b where a.b_id = b.id and b.art = 1 " +
					"group by b.isbn, b.autor, b.titel order by count(*) desc",
					DBManager.TABLE_AUSLEIHEN, DBManager.TABLE_BUECHER));
			while(set.next() && vec.size() < anzahl){
				vec.add(new String[]{set.getString(1),set.getString(3),set.getString(4),set.getString(2)});
			}
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return vec;
	}
	
	public static Vector<Buch> getFromIsbn(String isbn){
		Vector<Buch> vec = new Vector<Buch>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select * from "+DBManager.TABLE_BUECHER+" where isbn = "+isbn);
			while(set.next()){
				vec.add(new Buch(set));
			}
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return vec;
	}
	
	/**
	 * Verfügbar now
	 * @param isbn
	 * @return
	 */
	public static Buch getVerfuegbar(String isbn){
		Vector<Buch> vec = getFromIsbn(isbn);
		for(int i=0;i<vec.size();i++){
			Buch b = vec.get(i);
			if(b.getStatus() == 0) return b;
		}
		return null;
	}
	
	/**
	 * Verfügbar von - bis
	 * @param isbn
	 * @return
	 */
	public static Buch getVerfuegbar(String isbn, Date von, Date bis, Ausleihe not){
		Vector<Buch> vec = getFromIsbn(isbn);
		for(int i=0;i<vec.size();i++){
			Buch b = vec.get(i);
			if(!b.isVerliehen(von, bis, not) && !b.isVorgemerkt(von, bis, not)) return b;
		}
		return null;
	}
	
	public static int getCount(){
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select count(*) from "+DBManager.TABLE_BUECHER+"");
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
			ResultSet set = state.executeQuery("select count(*) from "+DBManager.TABLE_AUSLEIHEN+" where done = 0 and vorgemerkt = 0");
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
	
	public static int getCountNochNieVerliehen(){
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select count(distinct isbn) from "+DBManager.TABLE_BUECHER+" where id not in (select b_id from "+DBManager.TABLE_AUSLEIHEN+")");
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
	
	public static Vector<Buch> getNochNieVerliehen(){
		Vector<Buch> vec = new Vector<Buch>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("select * from "+DBManager.TABLE_BUECHER+" where id not in (select b_id from "+DBManager.TABLE_AUSLEIHEN+")");
			while(set.next())
				vec.add(new Buch(set));
			set.close();
			state.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return vec;
	}

	public static Vector<Buch> getActive() {
		Vector<Buch> vec = new Vector<Buch>();
		try{
			Statement state = DBManager.getIt().getConnection().createStatement();
			ResultSet set = state.executeQuery("sellect * from "+DBManager.TABLE_BUECHER+" where id in (select distinct b_id from "+DBManager.TABLE_AUSLEIHEN+" where done = 0)");
			while(set.next())
				vec.add(new Buch(set));
			set.close();
			state.close();
		}catch(SQLException ex){
			Logger.logError(ex.getMessage());
		}
		return vec;
	}
	
}
