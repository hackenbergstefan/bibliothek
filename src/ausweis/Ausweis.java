package ausweis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;

import javax.imageio.ImageIO;

import util.SWTAWTImageConverter;

import com.barcodelib.barcode.Linear;

import log.Logger;
import model.Schueler;

public class Ausweis{
	public static final Point2D size_inch = new Point2D.Double(3.3464566929133858267716535433071,
										3.937007874015748031496062992126); //size in cm: 8.5 x 10 
	
	public static final Font nameFont = new Font("Comic Sans MS", Font.BOLD, 50);
	public static final Font idFont = new Font("Comic Sans MS", Font.PLAIN, 30);
	
	public static final Point nameStart = new Point(60,200);
	public static final Rectangle codeRect = new Rectangle(60,228, 0,140);
	public static final Point idStart = new Point(0,410);
	
	private static Linear code = new Linear();
	
	
	private static BufferedImage img;
	static{
		try {
			img = ImageIO.read(ClassLoader.getSystemResource("icons/ausweis.png"));
		} catch (IOException e) {
			Logger.logError(e.getMessage());
		}
		
		code.setType(Linear.CODE128);
		code.setShowText(false);
		code.setUOM(Linear.UOM_PIXEL);
		code.setY(codeRect.height);
		code.setX(3);
	}
	
	public static BufferedImage getAusweisAWT(Schueler s){
		String id = String.format("%05d", s.getId());
		String name = s.getVorname()+" "+s.getNachname();
		
		BufferedImage ausweis = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		Graphics2D g = ausweis.createGraphics();
		g.drawImage(img, 0, 0, null);
		
		g.setFont(nameFont);
		g.setColor(Color.black);
		g.drawString(name, nameStart.x, nameStart.y);
		
		g.translate(-9*code.getX(), 0);
		
		code.setData(id);
		try {
			BufferedImage codeImg = code.renderBarcode();
			g.drawImage(codeImg, codeRect.x, codeRect.y, null);
		} catch (Exception e){
			Logger.logError(e.getMessage());
		}
		
		g.setFont(idFont);
		g.setColor(Color.black);
		int idWidth = g.getFontMetrics().stringWidth(id);
		g.drawString(id, codeRect.x + (code.getGeneratedBarcodeImageWidthInPixel() - idWidth)/2, idStart.y);
		
		
		
		g.finalize();
		
		return ausweis;
	}
	
	public static org.eclipse.swt.graphics.ImageData getAusweisSWT(Schueler s){
		BufferedImage i = getAusweisAWT(s);
		return SWTAWTImageConverter.makeSWTImage(i);
	}
	
}
