package infos;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

//import com.brianl.timetrack.controller.TimeSession;
//import com.brianl.timetrack.view.CollapseButtonListener;

/**
 * Simple SWT Shell that contains a Group with some controls. An arrow button is
 * used to toggle the Text area widget as collapsed or not.
 * 
 * @author ammianus http://librixxxi.blogspot.com/
 * 
 */
public class CollapsibleTextBox {

	private static final int NUMBER_GRID_COLUMNS = 1;

	private Display _display;

	private Shell _shell;

	CollapsibleTextBox() {
		_display = new Display();
		_shell = new Shell(_display);
		_shell.setSize(300, 250);
		// Collapsible Group
		_shell.setText("Collapsible Group Example");
		// format shell as single column grid layout
		_shell.setLayout(new GridLayout(NUMBER_GRID_COLUMNS, true));

		// create the group
		Group collapseGroup = new Group(_shell, SWT.NONE);
		GridData groupGrid = new GridData(SWT.TOP, SWT.LEFT, false, false);
		groupGrid.horizontalSpan = 1;
		collapseGroup.setLayoutData(groupGrid);
		collapseGroup.setText("Control Group");

		// create a Label and Button inside the Group
		GridData labelGridData = new GridData(GridData.VERTICAL_ALIGN_END);
		labelGridData.horizontalSpan = 1;
		String labelText = "Pressing button toggles Text. ";
		final Label instructionLabel = new Label(collapseGroup, SWT.NONE);
		instructionLabel.setText(labelText);
		instructionLabel.setLayoutData(labelGridData);
		instructionLabel.pack();

		final Button collapseButton = new Button(collapseGroup, SWT.ARROW | SWT.UP);

		// multi-row Text area with word-wrap
		final Text textArea = new Text(collapseGroup, SWT.MULTI | SWT.LEAD	| SWT.BORDER | SWT.WRAP);

		// set height and width of Text area
		GC gc = new GC(textArea);
		FontMetrics fm = gc.getFontMetrics();
		final int textBoxWidth = 50 * fm.getAverageCharWidth();
		final int textBoxHeight = 4 * fm.getHeight();
		gc.dispose();
		textArea.setSize(textArea.computeSize(textBoxWidth, textBoxHeight));
		GridData textBoxGrid = new GridData(SWT.TOP, SWT.LEFT, false, false);
		textBoxGrid.horizontalSpan = NUMBER_GRID_COLUMNS;
		textBoxGrid.heightHint = textBoxHeight;
		textBoxGrid.widthHint = textBoxWidth;
		textArea.setLayoutData(textBoxGrid);

		// set the layout for the items contained by the Group as a two column
		// grid
		collapseGroup.setLayout(new GridLayout(2, false));
		collapseGroup.layout(true);

		// create a listener that waits for button press
		collapseButton.addListener(SWT.Selection, new CollapseButtonListener(
				textArea, textBoxWidth, textBoxHeight, collapseGroup,
				textBoxGrid));

		// open the shell and run UI loop
		_shell.pack();
		_shell.open();
		while (!_shell.isDisposed()) {
			if (!_display.readAndDispatch())
				_display.sleep();
		}
		_display.dispose();
	}

	/**
	 * Runs the demo
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new CollapsibleTextBox();
	}

	/**
	 * inner class handles button click events for arrow button
	 * 
	 */
	public class CollapseButtonListener implements Listener {

		private Text _timeNotesBox;
		private int _textBoxHeight;
		private int _textBoxWidth;
		private Group _container;
		private GridData _textBoxGrid;

		/**
		 * @param timeNotesBox
		 * @param textBoxHeight
		 * @param textBoxWidth
		 * @param container
		 * @param textBoxGrid
		 */
		public CollapseButtonListener(Text timeNotesBox, int textBoxWidth,
				int textBoxHeight, Group container, GridData textBoxGrid) {
			super();
			// save references to the various controls to update
			this._timeNotesBox = timeNotesBox;
			this._textBoxHeight = textBoxHeight;
			this._textBoxWidth = textBoxWidth;
			this._container = container;
			this._textBoxGrid = textBoxGrid;
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
			if (_timeNotesBox.getVisible()) {
				// collapse the text area
				_timeNotesBox.setSize(_timeNotesBox.computeSize(1, 1));
				_textBoxGrid.heightHint = 1;
				_textBoxGrid.widthHint = 1;
				_timeNotesBox.setLayoutData(_textBoxGrid);
				_timeNotesBox.setVisible(false);
				_timeNotesBox.pack(true);
				System.out.println("Hiding textbox");
			} else {
				// expand text area back to original sizing
				_timeNotesBox.setSize(_timeNotesBox.computeSize(_textBoxWidth,
						_textBoxHeight));
				_textBoxGrid.heightHint = _textBoxHeight;
				_textBoxGrid.widthHint = _textBoxWidth;
				_timeNotesBox.setLayoutData(_textBoxGrid);
				_timeNotesBox.setVisible(true);
				_timeNotesBox.pack(true);
				System.out.println("Showing textbox");
			}

			// in this case the container is the Group
			_container.pack(true);

			// resize the parent shell too
			_container.getParent().pack(true);
		}

	}
}
