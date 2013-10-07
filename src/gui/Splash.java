package gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import update.DBUpdater;

import db.DBManager;

public class Splash extends Shell {
	private static Splash splash;
	private ProgressBar progressBar;
	private Text txtStatus;
	
	public static Splash getSplash(Display display){
		if(splash == null) splash = new Splash(display);
		return splash;
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	private Splash(Display display) {
		super(display, SWT.APPLICATION_MODAL);
		
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		Label lblSdf = new Label(this, SWT.NONE);
		lblSdf.setImage(SWTResourceManager.getImage(Splash.class, "/icons/Splash.png"));
		lblSdf.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		txtStatus = new Text(this, SWT.NONE);
		txtStatus.setEditable(false);
		txtStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		progressBar = new ProgressBar(this, SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		createContents();
		
		Rectangle disp = display.getClientArea();
		Rectangle curr = getBounds();
		setBounds((disp.width - curr.width)/2, (disp.height-curr.height)/2, curr.width,curr.height);
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("");
		pack();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	
	@Override
	public void open() {
		super.open();
		
		Display d = Display.getCurrent();
				
		new StartupThread(d).start();
	}
	
	private class StartupThread extends Thread{
		private Display display;
		
		
		public StartupThread(Display display) {
			super();
			this.display = display;
		}


		@Override
		public void run() {
			do{
				DBManager.getIt().connect();
				display.syncExec(new Runnable(){
					public void run(){
						txtStatus.setText("Warte auf Datenbank ...");
						progressBar.setSelection(10);
					}
				});
				
				try{
					Thread.sleep(500);
				}catch(Exception ex){}

			}while(!DBManager.getIt().isConnected());
			
			display.syncExec(new Runnable() {
				
				@Override
				public void run() {
					txtStatus.setText("Checke nach Updates...");
					progressBar.setSelection(20);
					DBUpdater.checkForDBUpdates(getShell());
				}
				
			});
			
			display.syncExec(new Runnable() {
				
				@Override
				public void run() {
					txtStatus.setText("Starte Programm ...");
				}
			});
			
			
			Display.getDefault().syncExec(new Runnable() {
				
				@Override
				public void run() {
					MainApplication.MAIN.createContents();
				}
			});
			
			for(int i=1;i<80;i++){
				display.syncExec(new Runnable(){
					@Override
					public void run() {
						progressBar.setSelection(progressBar.getSelection() + 1);
					}
				});
				
				try{
					Thread.sleep(30);
				}catch(InterruptedException ex){}
			}
			
			display.syncExec(new Runnable() {
				
				@Override
				public void run() {
					dispose();
				}
			});
		}
	}

}
