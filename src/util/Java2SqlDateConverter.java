package util;

import org.eclipse.core.databinding.conversion.IConverter;

public class Java2SqlDateConverter implements IConverter{

	@Override
	public Object getFromType() {
		return java.sql.Date.class;
	}

	@Override
	public Object getToType() {
		return java.util.Date.class;
	}

	@Override
	public Object convert(Object fromObject) {
		return DateUtils.sql2java((java.sql.Date)fromObject);
	}

}
