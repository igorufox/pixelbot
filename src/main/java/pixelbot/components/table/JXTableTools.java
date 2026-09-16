package pixelbot.components.table;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.*;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.*;
import org.jdesktop.swingx.decorator.HighlightPredicate.AndHighlightPredicate;
import org.jdesktop.swingx.renderer.*;
import org.jdesktop.swingx.table.*;

public class JXTableTools {
	protected static SimpleDateFormat view_date_formater = new SimpleDateFormat("E, d MMM yyyy");
	protected static SimpleDateFormat view_datetime_formater = new SimpleDateFormat(
			"dd.MM.yyyy HH:mm:ss");
	protected static SimpleDateFormat view_time_formater = new SimpleDateFormat("HH:mm:ss");

	public static void setLocale(Locale l) {
		JXTableTools.view_date_formater = new SimpleDateFormat(
				JXTableTools.view_date_formater.toPattern(), l);
		JXTableTools.view_datetime_formater = new SimpleDateFormat(
				JXTableTools.view_datetime_formater.toPattern(), l);
	}

	public static void setJXTableClassRenderers(final JXTable table) {
		table.setDefaultRenderer(java.sql.Date.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 9215548411064726879L;

			@Override
			protected void setValue(Object value) {
				if (value instanceof Date) {
					super.setValue(JXTableTools.view_date_formater.format((Date) value));
				} else if (value == null) {
					super.setValue(null);
				} else {
					super.setValue(value.toString());
				}
			}

		});
		table.setDefaultRenderer(java.sql.Timestamp.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 9215548411064726879L;

			@Override
			protected void setValue(Object value) {
				if (value instanceof Date) {
					super.setValue(JXTableTools.view_datetime_formater.format((Date) value));
				} else if (value == null) {
					super.setValue(null);
				} else {
					super.setValue(value.toString());
				}
			}
		});

		table.setDefaultRenderer(java.sql.Time.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 9215548411064726879L;

			@Override
			protected void setValue(Object value) {
				if (value instanceof Date) {
					super.setValue(JXTableTools.view_time_formater.format((Date) value));
				} else if (value == null) {
					super.setValue(null);
				} else {
					super.setValue(value.toString());
				}
			}
		});

		try {
			table.setDefaultRenderer(Link.class, new DefaultTableRenderer(new HyperlinkProvider(
					new LinkAction())));
		} catch (Exception e) {}

	}

	public static interface ICallBack {
		public void call(Object... params);
	}

	public static void addJXTableResetButton(final JXTable table, final ICallBack callback) {
		table.setColumnControlVisible(true);
		table.getActionMap().put("columns.reset", new AbstractAction("Reset") {
			private static final long serialVersionUID = -1462388219999017837L;

			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				callback.call();
			}
		});
		table.setColumnControl(new ColumnControlButton(table) {
			private static final long serialVersionUID = -215955006643921241L;

			@Override
			protected void populatePopup() {
				super.populatePopup();
				this.getColumnControlPopup().addAdditionalActionItems(
						Arrays.asList(new Action[] { table.getActionMap().get("columns.reset") }));
			}
		});
	}

	public static void initComparators(JXTable table) {
		//table.setSortsOnUpdates(false);

		List<TableColumn> list = table.getColumns(true);
		for (int i = 0; i < list.size(); ++i) {
			if (Icon.class.isAssignableFrom(table.getModel().getColumnClass(i))
					|| Link.class.isAssignableFrom(table.getModel().getColumnClass(i))) {
				((TableColumnExt) list.get(i)).setComparator(new ObjectComparator());
			}
		}
	}

	public static void addColorHighlighter(final JXTable table, HighlightPredicate predicate,
			Color cellBackground, Color cellForeground) {
		if (cellBackground == null) {
			table.addHighlighter(new ColorHighlighter(predicate, null, cellForeground));
		} else {
			final float FACTOR = 0.85f;
			Color cellBackgroundDark = new Color(Math.max((int) (cellBackground.getRed() * FACTOR),
					0), Math.max((int) (cellBackground.getGreen() * FACTOR), 0), Math.max(
					(int) (cellBackground.getBlue() * FACTOR), 0), cellBackground.getAlpha());

			table.addHighlighter(new ColorHighlighter(new AndHighlightPredicate(
					HighlightPredicate.EVEN, predicate), cellBackgroundDark, cellForeground));
			table.addHighlighter(new ColorHighlighter(new AndHighlightPredicate(
					HighlightPredicate.ODD, predicate), cellBackground, cellForeground));
		}
	}

	public static void addFontHighlighter(final JXTable table, HighlightPredicate predicate,
			Font font) {
		table.addHighlighter(new FontHighlighter(predicate, font));
	}

	public static void addToolTipHighlighter(final JXTable table, HighlightPredicate predicate) {
		table.addHighlighter(new ToolTipHighlighter(new AndHighlightPredicate(predicate,
				new HighlightPredicate() {
					@Override
					public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
						return adapter.getValue() != null;
					}
				}), new StringValue() {
			private static final long serialVersionUID = -4068708813257996718L;

			@Override
			public String getString(Object value) {
				if (value == null)
					return "";
				return value.toString();
			}
		}));
	}

	public static List<Map<String, Integer>> getColumsState(final JXTable table) {
		List<TableColumn> columns = table.getColumns(true);
		List<Map<String, Integer>> result = new ArrayList<Map<String, Integer>>(columns.size());

		for (int i = 0; i < columns.size(); ++i) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("index", Integer.valueOf(table.convertColumnIndexToView(i)));
			map.put("size", Integer.valueOf(columns.get(i).getWidth()));

			result.add(map);
		}

		return result;
	}

	public static void setColumsState(final JXTable table, List<Map<String, Integer>> state) {
		if (state == null)
			return;
		List<TableColumn> columns = table.getColumns(true);
		if (columns.size() != state.size())
			return;

		int[] ids = new int[state.size()];
		for (int i = 0; i < state.size(); ++i) {
			ids[i] = -1;
		}
		for (int i = 0; i < state.size(); ++i) {
			columns.get(i).setWidth(state.get(i).get("size").intValue());
			int index = state.get(i).get("index").intValue();
			((TableColumnExt) columns.get(i)).setVisible(index != -1);
			if (index != -1) {
				ids[index] = i;
			}
		}
		for (int i = 0; i < state.size(); ++i) {
			if (ids[i] == -1)
				break;
			table.moveColumn(table.convertColumnIndexToView(ids[i]), i);
		}

	}
}
