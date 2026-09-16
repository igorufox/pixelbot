package pixelbot.components.table;

import java.awt.Component;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.*;

import pixelbot.misc.CachedTableModel;

public class HighlightValuePredicate implements HighlightPredicate {

	private String key;
	private Object value;

	public HighlightValuePredicate(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {

		JXTable table = (JXTable) adapter.getComponent();
		CachedTableModel<?, ?> model = ((CachedTableModel<?, ?>) table.getModel());
		CachedTableModel.Row row = model.getValueAt(adapter.convertRowIndexToModel(adapter.row));
		if (this.value == null) {
			return null == row.getValue(this.key);
		} else {
			return this.value.equals(row.getValue(this.key));
		}
	}
}