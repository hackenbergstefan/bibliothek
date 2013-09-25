package util;


import infos.CollapsibleComposite;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
	 * inner class handles button click events for arrow button
	 * 
	 */
	public class CollapseButtonListener implements Listener {
		private int width;
		private int height;
		private CollapsibleComposite container;
		private GridData gridData;


		public CollapseButtonListener(CollapsibleComposite container, GridData gridData) {
			super();
			this.container = container;
			this.gridData = gridData;
			
			container.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(ControlEvent e) {
					Point p =  CollapseButtonListener.this.container.getSize();
					width = p.x;
					height = p.y;
				}
			});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets
		 * .Event)
		 */
		@Override
		public void handleEvent(Event event) {
			// toggles the Text Area as visible or not.
			// When Text Area is collapsed, resize the parent group
			if (container.content.getVisible()) {
				// collapse the text area
//				container.content.setSize(container.computeSize(1, 1));
//				gridData.heightHint = 1;
//				gridData.widthHint = 1;
				gridData.exclude = true;
				container.content.setLayoutData(gridData);
				container.content.setVisible(false);
//				container.content.pack(true);
			} else {
				// expand text area back to original sizing
//				container.content.setSize(container.computeSize(width,
//						height));
//				gridData.heightHint = height;
//				gridData.widthHint = width;
				gridData.exclude = false;
				container.content.setLayoutData(gridData);
				container.content.setVisible(true);
//				container.content.pack(true);
			}
			
			container.layout(true, true);

		}

	}
