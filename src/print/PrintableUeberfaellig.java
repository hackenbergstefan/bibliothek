package print;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.imageio.ImageIO;

import log.Logger;
import model.Ausleihe;
import util.DateUtils;

public class PrintableUeberfaellig implements Printable{
	public static int BORDER = 5;
	public static int FONT_BORDER = 10;
	public static int TOP_BORDER = 50;
	public static BufferedImage[] images;
	static{
		
		try {
			images = new BufferedImage[]{
					ImageIO.read(ClassLoader.getSystemResource("icons/ueberfaellig1.jpg")),
					ImageIO.read(ClassLoader.getSystemResource("icons/ueberfaellig2.jpg"))
			};
		} catch (IOException e) {
			Logger.logError(e.getMessage());
		}
	}
	private Vector<Ausleihe> ueberfaellige;
	private Font basicFont = new Font("Calibri", Font.BOLD, 14);
	
	/**
	 * 
	 * @param ausleihenToPrint if null allTooLate will be printed. 
	 */
	public PrintableUeberfaellig(Vector<Ausleihe> ausleihenToPrint){
		if(ausleihenToPrint == null || ausleihenToPrint.size() == 0)
			ueberfaellige = Ausleihe.getAllTooLate();
		else ueberfaellige = ausleihenToPrint;
	}
	
	
	
	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
		System.out.println("PrintableUeberfaellig.print()");
		
		if(Math.ceil(ueberfaellige.size()/6) < pageIndex) return NO_SUCH_PAGE;
		
		System.out.println("\t page="+pageIndex);
		
		Graphics2D g2d = (Graphics2D)g;
	    
		int width = (int) ((pf.getImageableWidth()-1*BORDER)/2) -2;
		int height = (int) ((pf.getImageableHeight()-2*BORDER)/3) -2;
		int zeroX =(int)pf.getImageableX() +1;
		int zeroY = (int)pf.getImageableY() +1;
		
	    Rectangle[] rects = new Rectangle[]{
	    		new Rectangle(zeroX, zeroY, width, height),	
	    		new Rectangle(zeroX+width+BORDER, zeroY, width, height),
	    		new Rectangle(zeroX, zeroY+height+BORDER, width, height),
	    		new Rectangle(zeroX+width+BORDER, zeroY+height+BORDER, width, height),
	    		new Rectangle(zeroX, zeroY+2*height+2*BORDER, width, height),
	    		new Rectangle(zeroX+width+BORDER, zeroY+2*height+2*BORDER, width, height)
	    };
	    
	    for(int i = 6*pageIndex; i< Math.min(6*pageIndex + 6, ueberfaellige.size()); i++){
	    	Rectangle r = rects[i%6];
	    	g2d.drawRect(r.x, r.y, r.width, r.height);
	    	printToRect(r, ueberfaellige.get(i), g2d);
	    }
	    
	    System.out.println("page Complete");
		return PAGE_EXISTS;
 	}
	
	
//	private void printToRect(Rectangle r, Ausleihe a, Graphics2D g){
//		//Draw todays date in upper right corner
//		g.setFont(basicFont.deriveFont(8));
//		FontMetrics fm = g.getFontMetrics();
//		String date = DateUtils.shortFormat.format(new Date(System.currentTimeMillis()));
//		g.drawString(date, r.x + r.width - fm.stringWidth(date) - BORDER, r.y + BORDER);
//		
//		g.setFont(basicFont.deriveFont(Font.BOLD, 20));
//		int newy = drawCenteredString(r, g, "Zurückbringen!", r.y+FONT_BORDER+TOP_BORDER);
//		
//		g.setFont(basicFont.deriveFont(Font.BOLD, 16));
//		newy = drawCenteredString(r, g, a.getS().toNiceString()+", ", newy+2*FONT_BORDER);
//		
//		g.setFont(basicFont);
//		newy = drawCenteredString(r, g, "du hast", newy+FONT_BORDER);
//		
//		g.setFont(basicFont.deriveFont(Font.BOLD));
//		newy = drawCenteredString(r, g, a.getB().toNiceString(), newy+FONT_BORDER);
//		
//		g.setFont(basicFont.deriveFont(Font.BOLD, 20));
//		newy = drawCenteredString(r, g, "schon "+a.getDaysTooLate()+" Tage zu lange!.", newy+2*FONT_BORDER);
//	}
	
	private void printToRect(Rectangle r, Ausleihe a, Graphics2D g){
		BufferedImage img = images[(int)Math.min((int)(a.getDaysTooLate() / 10), images.length-1)];
		
		//print image
		double scale = img.getWidth() / (double)img.getHeight();
		int newHeight = (int)((r.height - 2*BORDER)*0.7);
		int newWidth = (int)(newHeight * scale);
		int newY = r.y + BORDER + (r.height - 2*BORDER - newHeight)/2;
		g.drawImage(img, r.x + r.width - newWidth - BORDER, newY, r.x + r.width - BORDER, newY + newHeight, 0,0, img.getWidth(), img.getHeight(), null);
				
				
		//Draw todays date in upper right corner
		g.setFont(basicFont.deriveFont(8));
		FontMetrics fm = g.getFontMetrics();
		String date = DateUtils.shortFormat.format(new Date(System.currentTimeMillis()));
		g.drawString(date, r.x + r.width - fm.stringWidth(date) - BORDER, r.y + fm.getHeight() + BORDER);
		
		g.setFont(basicFont.deriveFont(Font.BOLD, 20));
		int newy = drawCenteredString(r, g, "Zurückbringen!", r.y+FONT_BORDER+TOP_BORDER);
		
		g.setFont(basicFont.deriveFont(Font.BOLD, 16));
		newy = drawParagraphedString(r, g, a.getS().toNiceString()+", ", newy+2*FONT_BORDER);
		
		g.setFont(basicFont);
		newy = drawParagraphedString(r, g, "du hast", newy+FONT_BORDER);
		
		g.setFont(basicFont.deriveFont(Font.BOLD));
		newy = drawParagraphedString(r, g, a.getB().toNiceString(), newy+FONT_BORDER);
		
		g.setFont(basicFont.deriveFont(Font.BOLD, 20));
		newy = drawParagraphedString(r, g, "schon "+a.getDaysTooLate()+" Tage zu lange!", newy+2*FONT_BORDER);
	}
	
	/**
	 * 
	 * @param r
	 * @param g
	 * @param s
	 * @param y
	 * @return new y coordinate
	 */
	private int drawCenteredString(Rectangle r, Graphics2D g, String s, int y){
		FontMetrics fm = g.getFontMetrics();
		int width = fm.stringWidth(s);
		g.drawString(s, r.x + (r.width - width)/2 , y);
		return y + fm.getHeight();
	}
	
	private int drawParagraphedString(Rectangle r, Graphics2D g, String s, int y){
		Hashtable<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();
		map.put(TextAttribute.FONT, g.getFont());
		AttributedCharacterIterator paragraph = new AttributedString(s,map).getIterator();
		
		int paragraphStart = paragraph.getBeginIndex();
        int paragraphEnd = paragraph.getEndIndex();
        FontRenderContext frc = g.getFontRenderContext();
        LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);

		// Set break width to width of Component.
		float breakWidth = r.width - 2*BORDER;
		float drawPosX = r.x + BORDER;
		float drawPosY = y;
		// Set position to the index of the first character in the paragraph.
		lineMeasurer.setPosition(paragraphStart);
		
		// Get lines until the entire paragraph has been displayed.
        while (lineMeasurer.getPosition() < paragraphEnd) {
 
            // Retrieve next layout. A cleverer program would also cache
            // these layouts until the component is re-sized.
            TextLayout layout = lineMeasurer.nextLayout(breakWidth);
 
            // Compute pen x position. If the paragraph is right-to-left we
            // will align the TextLayouts to the right edge of the panel.
            // Note: this won't occur for the English text in this sample.
            // Note: drawPosX is always where the LEFT of the text is placed.
            
 
            // Move y-coordinate by the ascent of the layout.
            drawPosY += layout.getAscent();
 
            // Draw the TextLayout at (drawPosX, drawPosY).
            layout.draw(g, drawPosX, drawPosY);
 
            // Move y-coordinate in preparation for next layout.
            drawPosY += layout.getDescent() + layout.getLeading();
        }
        
        return (int)drawPosY;
	}

}
