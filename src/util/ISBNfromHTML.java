package util;

import java.net.URL;
import java.util.List;

import log.Logger;

import com.google.gdata.client.books.BooksService;
import com.google.gdata.client.books.VolumeQuery;
import com.google.gdata.data.books.VolumeEntry;
import com.google.gdata.data.books.VolumeFeed;

public class ISBNfromHTML {
	
	public static String[] fromHTML(String isbn){
		try{
			BooksService booksService = new BooksService("myCompany-myApp-1");
			booksService.setUserCredentials("hackenberg.stefan@t-online.de", "ORGANISATION");

			URL url = new URL("http://www.google.com/books/feeds/volumes/?q=ISBN%3C" + isbn + "%3E");
			VolumeQuery volumeQuery = new VolumeQuery(url);
			VolumeFeed volumeFeed = booksService.query(volumeQuery, VolumeFeed.class);

			// using an ISBN in query gives only one entry in VolumeFeed
			List<VolumeEntry> volumeEntries = volumeFeed.getEntries();
			int index = 0;
			
			VolumeEntry entry = null;
			while(index < volumeEntries.size()){
				entry = volumeEntries.get(index);
				if(entry.getIdentifiers().get(2).getValue().substring(5).equals(isbn)) break;
				else index++;
			}
			
			if(index == volumeEntries.size()) entry = volumeEntries.get(0);
			
			String titel = entry.getTitle().getPlainText();
			String autor = entry.getCreators().get(0).getValue();
			String[] split = autor.split("\\s");
		    String nachname = split[split.length-1];
		    String vorname = autor.substring(0, autor.length()-nachname.length());
		    autor = nachname.trim()+", "+vorname.trim();
		    
		    String jahr = "";
		    try{
		    	jahr = entry.getDates().get(0).getValue().split("-")[0];
		    }catch(Exception ex){}
			
		    return new String[]{titel, autor, jahr};
		}catch(Exception ex){
			Logger.logError(ex.getMessage());
		}
		return null;
	}

}
