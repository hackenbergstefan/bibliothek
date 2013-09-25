package gui.validators;

import gui.StringConstants;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class ISBNValidator implements IValidator{

	@Override
	public IStatus validate(Object value) {
		if (value instanceof String) {
			String s = (String) value;
			// We check if the string is longer then 2 signs
			if (s.length() == 10 || s.length() == 13) {
				return Status.OK_STATUS;
			} else {
				return ValidationStatus
						.error(StringConstants.VALIDATION_ERROR_ISBN);
			}
		} else {
			throw new RuntimeException(
					"Not supposed to be called for non-strings.");
		}
	}

}
