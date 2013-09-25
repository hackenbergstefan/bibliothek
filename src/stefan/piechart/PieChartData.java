package stefan.piechart;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wb.swt.SWTResourceManager;


public class PieChartData{
	public String label;
	public double value;
	public Color color;
	
	public PieChartData(String label,double value, Color color) {
		super();
		this.label = label;
		this.value = value;
		this.color = color;
	}
	
	public PieChartData(String label,double value) {
		super();
		this.label = label;
		this.value = value;
		this.color = SWTResourceManager.getColor(new RGB((float)Math.random()*360, (float)Math.random(), 1));
	}
}
