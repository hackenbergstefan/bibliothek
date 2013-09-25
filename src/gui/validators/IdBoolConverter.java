package gui.validators;

import org.eclipse.core.databinding.conversion.IConverter;

public class IdBoolConverter implements IConverter{
	@Override
	public Object getToType() {
		return boolean.class;
	}
	
	@Override
	public Object getFromType() {
		return int.class;
	}
	
	@Override
	public Object convert(Object fromObject) {
		int id = (Integer)fromObject;
		return id != -1;
	}
}
