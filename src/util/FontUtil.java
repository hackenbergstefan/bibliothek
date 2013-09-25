package util;

import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class FontUtil {
	private static final int addHeight_Bigg = 2, addHeight_Big = 1;
	
	public static final Font systemFont = Display.getCurrent().getSystemFont();
	public static final Font boldFont = getModifiedFont(systemFont, SWT.BOLD);
	public static final Font italicFont = getModifiedFont(systemFont, SWT.ITALIC);
	public static final Font systemFontBigg = getModifiedFont(systemFont, addHeight_Bigg, SWT.NONE);
	public static final Font boldFontBigg = getModifiedFont(systemFont, addHeight_Bigg, SWT.BOLD);
	public static final Font italicFontBigg = getModifiedFont(systemFont, addHeight_Bigg, SWT.ITALIC);
	public static final Font systemFontBig = getModifiedFont(systemFont, addHeight_Big, SWT.NONE);
	public static final Font boldFontBig = getModifiedFont(systemFont, addHeight_Big, SWT.BOLD);
	public static final Font italicFontBig = getModifiedFont(systemFont, addHeight_Big, SWT.ITALIC);

	public  static FontData[] getModifiedFontData(FontData[] originalData, int additionalStyle) {
		FontData[] styleData = new FontData[originalData.length];
		for (int i = 0; i < styleData.length; i++) {
			FontData base = originalData[i];
			styleData[i] = new FontData(base.getName(), base.getHeight(), base.getStyle() | additionalStyle);
		}
		return styleData;
	}
	
	public  static FontData[] getModifiedFontData(FontData[] originalData, int addHeight, int additionalStyle) {
		FontData[] styleData = new FontData[originalData.length];
		for (int i = 0; i < styleData.length; i++) {
			FontData base = originalData[i];
			styleData[i] = new FontData(base.getName(), base.getHeight()+addHeight, base.getStyle() | additionalStyle);
		}
		return styleData;
	}
	
	private static Font getModifiedFont(Font font, int additionalStyle){
		return new Font(Display.getCurrent(), getModifiedFontData(font.getFontData(), additionalStyle));
	}
	
	private static Font getModifiedFont(Font font, int addHeight, int additionalStyle){
		return new Font(Display.getCurrent(), getModifiedFontData(font.getFontData(), addHeight, additionalStyle));
	}
	
	public static Styler boldStyler(){
		return new Styler() {
			
			@Override
			public void applyStyles(TextStyle textStyle) {
				textStyle.font = boldFont;
			}
		};
	}
	
	public static Styler italicStyler(){
		return new Styler() {
			
			@Override
			public void applyStyles(TextStyle textStyle) {
				textStyle.font = italicFont;
			}
		};
	}
	
	
	public static Label getBoldBiggLabel(Composite parent, String txt){
		Label l = new Label(parent,SWT.NONE);
		l.setFont(boldFontBigg);
		l.setText(txt);
		return l;
	}
	
	public static Label getBoldBigLabel(Composite parent, String txt){
		Label l = new Label(parent,SWT.NONE);
		l.setFont(boldFontBig);
		l.setText(txt);
		return l;
	}
	
	public static Label getBoldLabel(Composite parent, String txt){
		Label l = new Label(parent,SWT.NONE);
		l.setFont(boldFont);
		l.setText(txt);
		return l;
	}
	
}
