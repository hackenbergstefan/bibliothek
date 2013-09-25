package stefan.piechart;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class PieChart extends Composite {
	private PieChartData[] data = new PieChartData[0];
	private static final int DISTANCE_TO_CIRC = 50, DISTANCE_TO_CIRC_SMALL = 10, TEXT_GAP = 2;
	private double total;
	private boolean legendOnCirc = true;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PieChart(Composite parent, int style, PieChartData[] data) {
		super(parent, style);
		if(data != null)
			this.data = data;
		if(data != null) rearrangeData();
		setLayout(new FillLayout());
		Canvas can = new Canvas(this, SWT.NONE);
		can.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				drawPie(e.gc);
			}
		});
		
		computeTotal();
		
		/*addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				Point size = getSize();
				if(size.x < 300) setSize(300, 300);
				//else if(size.x > 800) setSize(800,800);
				
			}
		});*/
	}
	
	/*@Override
	public Point computeSize(int wHint, int hHint) {
		return new Point(300,300);
	}
	
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(300,300);
	}*/
	
	private void computeTotal(){
		//sum total amount
		total = 0;
		for(int i=0;i<data.length;i++){
			total += data[i].value;
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	
	
	/**
	 * @return the legendOnCirc
	 */
	public boolean isLegendOnCirc() {
		return legendOnCirc;
	}

	/**
	 * @param legendOnCirc the legendOnCirc to set
	 */
	public void setLegendOnCirc(boolean legendOnCirc) {
		this.legendOnCirc = legendOnCirc;
		redraw();
	}

	/**
	 * @return the data
	 */
	public PieChartData[] getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(PieChartData[] data) {
		if(data != null){
			this.data = data;
			rearrangeData();
			computeTotal();
			layout(true);
		}
	}
	
	private void rearrangeData(){
		Vector<PieChartData> vec = new Vector<PieChartData>();
		for(PieChartData d: data){
			int i=0;
			for(i=0;i<vec.size();i++){
				if(vec.get(i).value > d.value)break;
			}
			vec.add(i, d);
		}
		
		data = new PieChartData[vec.size()];
		for(int i=0;i<(int)(data.length/2);i++){
			data[2*i] = vec.get(i);
			data[2*i+1] = vec.get(data.length-1-i);
		}
		if(data.length/2.0 != (int)(data.length/2)){
			data[data.length-1] = vec.get((int)(data.length/2));
		}
	}

	private void drawPie(GC gc) {
		if(data.length == 0) return;
		
		//draw Arcs
		int width = getClientArea().width;
		int height = getClientArea().height;
		Point middle = new Point(width/2, height/2);
		Rectangle pieRect = new Rectangle((int)(width*0.15), (int)(height*0.15),
				(int)(width*0.7), (int)(height*0.7));  //let 30 percent in each dimension free
		
		int curAngle = 0;
		
		
		int correctX = pieRect.width<pieRect.height?0:(pieRect.width-pieRect.height);
		int correctY = pieRect.width>pieRect.height?0:(pieRect.height-pieRect.width);
		int radius = (pieRect.width<pieRect.height?pieRect.width:pieRect.height)/2;

		
		gc.setLineWidth(1);
		gc.setFont(new Font(getDisplay(), "Calibri", 15, SWT.BOLD));
		
		//fill arcs
		for(int i=0;i<data.length;i++){
			gc.setBackground(ColorMath.changeSaturation(data[i].color, 0.2));
			gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
			int angle = (int)Math.round(data[i].value/total*360.0);
			gc.fillArc(pieRect.x+correctX/2,pieRect.y+correctY/2,pieRect.width-correctX,pieRect.height-correctY,curAngle, angle);
			curAngle += angle;
		}
		
		
		curAngle = 0;
		//draw lines
		for(int i=0;i<data.length;i++){
			gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
			int angle = (int)Math.round(data[i].value/total*360);
			gc.drawArc(pieRect.x+correctX/2,pieRect.y+correctY/2,pieRect.width-correctX,pieRect.height-correctY,curAngle, angle);
			curAngle += angle;
			gc.drawLine(middle.x, middle.y, middle.x+(int)(radius*Math.cos(Math.toRadians((int)curAngle))),
					middle.y-(int)(radius*Math.sin(Math.toRadians((int)curAngle))));
		}
		
		curAngle = 0;
		//draw labels 
		for(int i=0;i<data.length;i++){
			int angle = (int)Math.round(data[i].value/total*360);
			String label = data[i].label.trim()+" ("+String.format("%1.0f",data[i].value)+" - "+String.format("%1.1f",data[i].value/total*100)+"%)";
			Point size = gc.textExtent(label); 
			Point pos_polar = new Point(radius+DISTANCE_TO_CIRC,(int)(curAngle+angle/2));
			Point pos = p2k(pos_polar,middle);
			
			if(curAngle+angle/2 <= 90){
			}else if(curAngle+angle/2 <= 180){
				pos.x -= size.x;
			}else if(curAngle+angle/2 <= 270){
				pos.x -= size.x;
				pos.y -= size.y;
			}else if(curAngle+angle/2 <= 360){
				pos.y -= size.y;
			}
			
			
			gc.setFont(new Font(getDisplay(), "Calibri", 10, SWT.BOLD));
			gc.setForeground(ColorMath.changeBrightness(data[i].color, 0.5));
			gc.drawText(label, pos.x, pos.y,true);
			curAngle += angle;
		}
	}
	
	public Composite getLegende(Composite parent, int direction){
		Composite leg = new Composite(parent, SWT.NONE);
		RowLayout rowLayout = new RowLayout(direction);
		rowLayout.wrap = true;
		leg.setLayout(rowLayout);
		for(int i=0;i<data.length;i++){
			PieChartData d = data[i];
			Label l = new Label(leg, SWT.BORDER);
			l.setFont(new Font(getDisplay(), "Calibri", 12, SWT.BOLD));
			l.setForeground(ColorMath.changeBrightness(d.color, 0.5));
			l.setText((i+1)+": "+d.label+" ("+String.format("%1.1f",d.value/total*100)+"%)");
		}
		return leg;
	}
	
	
	
	public static Point p2k(int r, int phi, Point source){
		Point p = new Point((int)(r*Math.cos(phi/180.0*Math.PI)), (int)(r*Math.sin(phi/180.0*Math.PI)));
		if(source != null) return relToSource(p, source);
		else return p;
	}
	
	public static Point p2k(Point p, Point source){
		return p2k(p.x,p.y, source);
	}
	
	public static Point k2p(int x, int y, Point source){
		return new Point((int)(Math.sqrt((x-(source!=null?source.x:0))^2+(y-(source!=null?source.y:0))^2)), 
				(int)(Math.atan2(((source!=null?source.y:0)-y), (x-(source!=null?source.x:0)))/Math.PI*180.0));
	}
	
	public static Point k2p(Point p, Point source){
		return k2p(p.x,p.y, source);
	}
	
	
	public static int getR(Point p){
		return (int)Math.sqrt(p.x^2+p.y^2);
	}
	
	public static int getPhi(Point p){
		return (int)(Math.atan2(p.y, p.x)/Math.PI*180.0);
	}
	
	public static int dist(Point p, Point q){
		return (int)(Math.sqrt(Math.pow(p.x-q.x,2) + Math.pow(p.y-q.y,2)));
	}
	
	public static Point relToSource(Point p, Point source){
		return new Point(source.x+p.x, source.y-p.y);
	}
	
	public static Point inverse(Point p){
		return new Point(-p.x,-p.y);
	}
	
	/*public static void main(String[] args){
		final Display d = new Display();
		final Shell s = new Shell(d);
		s.setLayout(new GridLayout());
		
		
		PieChartData[] data = new PieChartData[]{
		new PieChartData("Punkt 1",50, SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE)),
		new PieChartData("Punkt 2",30, SWTResourceManager.getColor(SWT.COLOR_CYAN)),
		new PieChartData("Punkt 3",40, SWTResourceManager.getColor(SWT.COLOR_GREEN)),
		new PieChartData("Punkt 4",55, SWTResourceManager.getColor(SWT.COLOR_RED)),
		new PieChartData("Punkt 5",55, SWTResourceManager.getColor(SWT.COLOR_RED)),
		new PieChartData("Punkt 6",30, SWTResourceManager.getColor(SWT.COLOR_BLUE)),
		new PieChartData("Punkt 7",30, SWTResourceManager.getColor(SWT.COLOR_BLUE)),
		new PieChartData("Punkt 8",30, SWTResourceManager.getColor(SWT.COLOR_BLUE)),
		new PieChartData("Punkt 9",30, SWTResourceManager.getColor(SWT.COLOR_BLUE))};
		

		PieChart p =  new PieChart(s, SWT.NONE,null);
		p.setData(data);
		p.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		Composite c = p.getLegende(s, SWT.HORIZONTAL);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		
		s.open();
		
		while(!s.isDisposed())
			if(!d.readAndDispatch())
				d.sleep();
	}*/
}
