package ausweis;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import model.Schueler;

public class Ausweis_Printable implements Printable{
	private Schueler s;
	private static final int dpi = 72;
	
	public Ausweis_Printable(Schueler s){
		this.s = s;
	}
	

	@Override
	public int print(Graphics g1, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		if(pageIndex > 0) return NO_SUCH_PAGE;
		
		Graphics2D g = (Graphics2D)g1;
		
		System.out.println("imageable size = "+pageFormat.getImageableWidth()+", "+pageFormat.getImageableHeight());
		
		double width = pageFormat.getImageableWidth() - pageFormat.getImageableX();
		double height = pageFormat.getImageableHeight() - pageFormat.getImageableY();
		
		g.translate((int)pageFormat.getImageableX(), (int)pageFormat.getImageableY());
		
		BufferedImage img = Ausweis.getAusweisAWT(s); 
		g.drawImage(img, 30, 30, (int)(30 + Ausweis.size_inch.getX()*dpi), (int)(Ausweis.size_inch.getY()*dpi), 0,0, img.getWidth(), img.getHeight(), null);
		
		
		return PAGE_EXISTS;
	}
	
	
}
