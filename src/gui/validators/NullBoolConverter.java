package gui.validators;

import org.eclipse.core.databinding.conversion.IConverter;

public class NullBoolConverter implements IConverter{
	@Override
	public Object getToType() {
		return boolean.class;
	}
	
	@Override
	public Object getFromType() {
		return Object.class;
	}
	
	@Override
	public Object convert(Object fromObject) {
		return fromObject != null;
	}
}
