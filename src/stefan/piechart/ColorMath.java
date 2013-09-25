package stefan.piechart;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class ColorMath {
	
	/**
	 * 
	 * @param c
	 * @param brightness from 0.0 to 1.0
	 * @return
	 */
	public static Color changeBrightness(Color c, double brightness){
		float[] hsb = c.getRGB().getHSB();
		
		return new Color(c.getDevice(), new RGB(hsb[0], hsb[1], (float)brightness));
	}
	
	/**
	 * 
	 * @param c
	 * @param hue from 0 to 360
	 * @return
	 */
	public static Color changeHue(Color c, double hue){
		float[] hsb = c.getRGB().getHSB();
		
		return new Color(c.getDevice(), new RGB((float)hue, hsb[1], hsb[2]));
	}
	
	public static Color changeSaturation(Color c, double saturation){
		float[] hsb = c.getRGB().getHSB();
		
		return new Color(c.getDevice(), new RGB(hsb[0], (float) saturation, hsb[2]));
	}

}
