package model;

import gui.options.Preferences;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Vector;

import log.Logger;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import util.ApproximateStringAnalyzer;
import util.DateUtils;

public class Inventur {
	private File path;
	private Vector<Buch> inventur, ausgewerted_remove, ausgewerted_add;
	private Vector<UpdateObject> updates;
	private Document doc;

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	private static Inventur theone;
	
	public static Inventur getCurrent(){
		if(theone == null) theone = new Inventur(new File(Preferences.getPrefs().getString("inventur.file")));
		return theone;
	}
	
	private Inventur(File path){
		this.path = path;
		if(!path.exists()){
			try {
				inventur = new Vector<Buch>();
				doc = new Document(new Element("Inventur"));
				doc.getRootElement().setAttribute("startDate", DateUtils.longFormat.format(new Date(System.currentTimeMillis())));
				XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
				out.output(doc, new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
			} catch (Exception e) {
				Logger.logError(e.getMessage());
			}
		}else readFile();
	}
	
	/**
	 * returns COPY of vector.
	 * @return
	 */
	public Vector<Buch> getCurrentlyStocked(){
		if(inventur == null) readFile();
		
		return (Vector<Buch>) inventur.clone();
	}
	
	/**
	 * returns i-th element.
	 * @param i
	 * @return
	 */
	public Buch getCurrentlyStocked(int i){
		return inventur.get(i);
	}
	
	private void readFile(){
		inventur = new Vector<Buch>();
		try{
			doc = (new SAXBuilder()).build(path);
			for(Element e: doc.getRootElement().getChildren()){
				inventur.add(Buch.fromXML(e));
			}
		}catch(Exception ex){
			Logger.logError(ex.getMessage());
		}
		
		changes.firePropertyChange("inventur", null, inventur);
	}
	
	private void saveFile(){
		try{
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			out.output(doc,new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
		}catch(Exception ex){
			Logger.logError(ex.getMessage());
		}
	}
	
	public void addBuch(Buch b){
		if(b == null) return;
		
		doc.getRootElement().addContent(b.toXML());
		inventur.add(b);
		
		saveFile();
		
		changes.fireIndexedPropertyChange("inventur", inventur.size()-1, null, inventur);
	}
	
	public void removeIndex(int index){
		if(index < 0) return;
		
		inventur.remove(index);
		doc.getRootElement().removeContent(index);
		
		changes.fireIndexedPropertyChange("inventur", index, null, inventur);
	}
	
	/**
	 * Searches a book by isbn code in already stocked books.
	 * @return
	 */
	public Buch searchInStocked(String isbn){
		for(Buch b:inventur){
			if(b.getIsbn().equals(isbn)) return b;
		}
		return null;
	}
	
	
	public void auswerten(){
		ausgewerted_remove =  Buch.getAllBuecher();
		ausgewerted_add = (Vector<Buch>) inventur.clone();
		updates = new Vector<UpdateObject>();
		
		ApproximateStringAnalyzer sa = new ApproximateStringAnalyzer();
		
		for(int i=0;i<ausgewerted_remove.size();i++){
			Buch b1 = ausgewerted_remove.get(i);
			for(int j=0;j<ausgewerted_add.size();j++){
				Buch b2 = ausgewerted_add.get(j);
				if(b1.getIsbn().equals(b2.getIsbn())){
					ausgewerted_remove.remove(i);
					ausgewerted_add.remove(j);
					i--;
					break;
				}
			}
		}
		
		//try to do approximate match
		for(int i=0;i<ausgewerted_add.size();i++){
			Buch b1 = ausgewerted_add.get(i);
			for(int j=0;j<ausgewerted_remove.size();j++){
				Buch b2 = ausgewerted_remove.get(j);
				if(sa.calculateSimilarity(b1.getAutor(), b2.getAutor()) < 2 && sa.calculateSimilarity(b1.getTitel(), b2.getTitel()) < 2){
					updates.add(new UpdateObject(b2, b1));
					ausgewerted_add.remove(i);
					ausgewerted_remove.remove(j);
					i--;
					break;
				}
			}
		}
		
		getUpdates();
	}
	
	public Vector<Buch> getAusgewertetRemove(){
		return ausgewerted_remove;
	}
	public Vector<Buch> getAusgewertetAdd(){
		return ausgewerted_add;
	}
	public Vector<UpdateObject> getUpdates(){
		return updates;
	}
	
	public void commit(){
		if(ausgewerted_remove == null || ausgewerted_add == null || updates == null) return;
		for(Buch b: ausgewerted_remove){
			b.entfernen();
		}
		for(Buch b: ausgewerted_add){
			b.eintragen(true);
		}
		for(UpdateObject o: updates){
			o.from.setValues(o.to);
			o.from.eintragen(true);
		}
	}
	

	/**
	 * @return the path
	 */
	public File getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(File path) {
		this.path = path;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }
	
    
    public class UpdateObject{
    	public Buch from, to;
    	public UpdateObject(Buch from, Buch to){
    		this.from = from;
    		this.to = to;
    	}
    }
}
