package model;

import org.eclipse.jface.viewers.StyledString;

/**
 * Implements functions for nice output string and StyledString.
 * @author Zero
 *
 */
public interface IStringable {
	public String toNiceString();
	public StyledString toStyledString();
}
