package util;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

public class ImageComposite extends Composite {
	private Image image;
	
	
	public ImageComposite(Composite parent, int style) {
		super(parent, style);
		
		addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				Rectangle rect = getClientArea();
				gc.drawImage(new Image(getDisplay(), image.getImageData().scaledTo(rect.width, rect.height)), 0, 0);
			}
		});
	}
	
	
	@Override
	public void setBackgroundImage(Image image) {
		this.image = image;
	}
	
	@Override
	public Image getBackgroundImage() {
		return image;
	}

}
