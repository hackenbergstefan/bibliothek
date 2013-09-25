package util;

import java.io.File;
import java.io.IOException;

import log.Logger;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class FileFieldEditor2 extends FileFieldEditor{
	
	/**
     * Creates a new file field editor 
     */
    protected FileFieldEditor2() {
    }

    /**
     * Creates a file field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public FileFieldEditor2(String name, String labelText, Composite parent) {
        super(name, labelText, false, parent);
    }
    
    /**
     * Creates a file field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param enforceAbsolute <code>true</code> if the file path
     *  must be absolute, and <code>false</code> otherwise
     * @param parent the parent of the field editor's control
     */
    public FileFieldEditor2(String name, String labelText, boolean enforceAbsolute, Composite parent) {
        super(name, labelText, enforceAbsolute, VALIDATE_ON_FOCUS_LOST, parent);
    }
    /**
     * Creates a file field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param enforceAbsolute <code>true</code> if the file path
     *  must be absolute, and <code>false</code> otherwise
     * @param validationStrategy either {@link StringButtonFieldEditor#VALIDATE_ON_KEY_STROKE}
     *  to perform on the fly checking, or {@link StringButtonFieldEditor#VALIDATE_ON_FOCUS_LOST}
     *  (the default) to perform validation only after the text has been typed in
     * @param parent the parent of the field editor's control.
     * @since 3.4
     * @see StringButtonFieldEditor#VALIDATE_ON_KEY_STROKE
     * @see StringButtonFieldEditor#VALIDATE_ON_FOCUS_LOST
     */
    public FileFieldEditor2(String name, String labelText,
            boolean enforceAbsolute, int validationStrategy, Composite parent) {
        super(name,labelText,enforceAbsolute,validationStrategy,parent);
    }

	@Override
	protected boolean checkState() {
		String msg = null;

        String path = getTextControl().getText();
        if (path != null) {
			path = path.trim();
		} else {
			path = "";//$NON-NLS-1$
		}
        if (path.length() == 0) {
            if (!isEmptyStringAllowed()) {
				msg = getErrorMessage();
			}
        } else {
            File file = new File(path);
            if(!file.exists()){
            	try {
					file.createNewFile();
				} catch (IOException e) {
					Logger.logError(e.getMessage());
				}
            }
            if (file.isFile()) {
                
            } else {
                msg = getErrorMessage();
            }
        }

        if (msg != null) { // error
            showErrorMessage(msg);
            return false;
        }

        if(doCheckState()) { // OK!
	        clearErrorMessage();
	        return true;
        }
        msg = getErrorMessage(); // subclass might have changed it in the #doCheckState()
        if (msg != null) {
            showErrorMessage(msg);
        }
    	return false;
	}
}
