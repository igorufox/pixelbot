package pixelbot.components.logviewer;

import java.util.*;
import java.util.logging.Level;

import javax.swing.table.AbstractTableModel;

import pixelbot.misc.Tools;

public abstract class BaseModel extends AbstractTableModel {
	private static final long serialVersionUID = -1054748237278481035L;

	private Locale locale = Locale.getDefault();

	@Override
	public final String getColumnName(int column) {
		ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, this.locale);

		return messages.getString("log.columns."
				+ new String[] { "date", "time", "type", "message" }[column]);
	}

	@Override
	public final int getColumnCount() {
		return 4;
	}

	@Override
	public final Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
		case 1:
			return new DateSeq(this.getMillis(rowIndex), this.getSequence(rowIndex));
		case 2:
			return this.getLevel(rowIndex).toString();
		case 3:
			return this.getMessage(rowIndex);
		default:
			return "";
		}
	}

	protected abstract long getMillis(int rowIndex);

	protected abstract long getSequence(int rowIndex);

	protected abstract Level getLevel(int rowIndex);

	protected abstract String getMessage(int rowIndex);

	public void setLocale(Locale l) {
		this.locale = l;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
		case 1:
			return DateSeq.class;
		case 2:
			return Level.class;
		case 3:
			return String.class;
		}
		return super.getColumnClass(columnIndex);
	}

}
