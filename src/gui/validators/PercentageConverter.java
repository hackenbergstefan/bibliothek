package gui.validators;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.TableColumn;

public class PercentageConverter implements IConverter {
	private TableColumn control;
	private double percentage;
	
	public PercentageConverter(TableColumn control, double percentage){
		this.control = control;
		this.percentage = percentage;
	}
	
	@Override
	public Object getFromType() {
		return Point.class;
	}

	@Override
	public Object getToType() {
		return int.class;
	}

	@Override
	public Object convert(Object fromObject) {
		return (int) Math.round(percentage*((Point)fromObject).x);
	}

}
