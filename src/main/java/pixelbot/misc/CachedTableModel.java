package pixelbot.misc;

import java.util.*;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class CachedTableModel<R extends CachedTableModel.Row, F extends CachedTableModel.RowFactory<R>>
		extends AbstractTableModel {
	private static final long serialVersionUID = -6825455682945295183L;

	public interface RowFactory<R> {
		public R produce(Map<String, Object> data);

		public String getColumnPrefix();

		public String[] getColumnNames();

		public Class<?> getColumnClass(int columnIndex);
	}

	public interface EditableRowFactory<R> extends RowFactory<R> {
		public boolean isCellEditable(int rowIndex, int columnIndex);
	}

	public static abstract class DataSource<R extends CachedTableModel.Row, F extends RowFactory<R>> {
		protected F factory;

		public DataSource(F factory) {
			this.factory = factory;
		}

		public abstract int count();

		public abstract List<Map<String, Object>> get(int index);

	}

	public interface Row {
		public Object getValue(String key);
	}

	public interface EditableRow extends Row {
		public void setValue(String key, Object value);
	}

	protected RowFactory<R> factory;
	public DataSource<R, F> datasource;

	private Map<Integer, R> cache = new HashMap<Integer, R>();
	private int size = -1;
	private Locale locale = Locale.getDefault();

	public CachedTableModel(RowFactory<R> factory, DataSource<R, F> datasource) {
		this.factory = factory;
		this.datasource = datasource;
	}

	public void setLocale(Locale l) {
		this.locale = l;
	}

	public String getColumnCode(int column) {
		return this.factory.getColumnNames()[column];
	}

	public int getColumnIndex(String code) {
		return Arrays.asList(this.factory.getColumnNames()).indexOf(code);
	}

	@Override
	public final String getColumnName(int column) {
		try {
			ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, this.locale);

			return messages.getString(this.factory.getColumnPrefix()
					+ this.factory.getColumnNames()[column]);
		} catch (MissingResourceException mre) {
			return this.factory.getColumnPrefix() + this.factory.getColumnNames()[column];
		}
	}

	@Override
	public int getColumnCount() {
		return this.factory.getColumnNames().length;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Class<?> c = this.factory.getColumnClass(columnIndex);
		if (c != null) {
			return c;
		}
		return super.getColumnClass(columnIndex);
	}

	@Override
	public int getRowCount() {
		if (this.size == -1) {
			try {
				this.size = this.datasource.count();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return this.size;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (this.factory instanceof EditableRowFactory<?>) {
			return ((EditableRowFactory<?>) this.factory).isCellEditable(rowIndex, columnIndex);
		}
		return false;
	}

	@Override
	public Object getValueAt(int y, int x) {
		Row row = getValueAt(y);
		if (row != null) {
			return row.getValue(this.getColumnCode(x));
		}
		return "<Error>";
	}

	public R getValueAt(int index) {
		if (!this.cache.containsKey(Integer.valueOf(index))) {
			if (this.cache.size() > 200) {
				this.cache.clear();
			}
			List<Map<String, Object>> rows = this.datasource.get(index);
			int i = index;
			for (Map<String, Object> data : rows) {
				R row = this.factory.produce(data);
				this.cache.put(Integer.valueOf(i++), row);
			}
		}
		return this.cache.get(Integer.valueOf(index));
	}

	@Override
	public void setValueAt(Object value, int y, int x) {
		Row row = getValueAt(y);
		if (row != null && row instanceof EditableRow) {
			((EditableRow) row).setValue(this.getColumnCode(x), value);
		}
	}

	public void clearCache() {
		this.cache.clear();
		this.size = -1;
	}

	public void reset() {
		this.clearCache();
		this.fireTableDataChanged();
	}

	public R getSelectedRow(JTable table) {
		if (table.getSelectedRow() != -1) {
			return this.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()));
		}
		return null;
	}

	public List<R> getSelectedRows(JTable table) {
		List<R> result = new ArrayList<R>();
		for (int r : table.getSelectedRows()) {
			result.add(this.getValueAt(table.convertRowIndexToModel(r)));
		}
		return result;
	}

}