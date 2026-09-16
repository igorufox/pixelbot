package pixelbot.components.logviewer;

import java.awt.*;
import java.text.*;
import java.util.*;
import java.util.logging.Level;

import javax.swing.*;
import javax.swing.table.*;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.*;
import org.jdesktop.swingx.decorator.HighlightPredicate.AndHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.ColumnHighlightPredicate;
import org.jdesktop.swingx.table.*;

import pixelbot.components.table.JXTableTools;
import pixelbot.log.*;

public class JLogArea extends JScrollPane {
	private static final long serialVersionUID = -7486638743395501767L;
	private JXTable table = new JXTable();

	protected DateFormat df_date = new SimpleDateFormat("E, d MMM yyyy");
	protected DateFormat df_time = new SimpleDateFormat("HH:mm:ss.SSS");

	public JLogArea(BaseModel model) {

		this.table = new JXTable();
		this.table.setModel(model);
		this.table.getColumn(0).setMinWidth(100);
		this.table.getColumn(0).setMaxWidth(100);
		this.table.getColumn(1).setMinWidth(90);
		this.table.getColumn(1).setMaxWidth(90);
		this.table.getColumn(2).setMinWidth(90);
		this.table.getColumn(2).setMaxWidth(90);
		this.table.setSortOrder(1, SortOrder.DESCENDING);

		this.table.setColumnControlVisible(true);

		this.table.getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 8245709051037625314L;

			@Override
			protected void setValue(Object value) {
				if (value instanceof DateSeq) {
					super.setValue(JLogArea.this.df_date.format(((DateSeq) value).getDate()));
				} else {
					super.setValue(value);
				}
			}
		});
		this.table.getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 4050921688909806466L;

			@Override
			protected void setValue(Object value) {
				if (value instanceof DateSeq) {
					super.setValue(JLogArea.this.df_time.format(((DateSeq) value).getDate()));
				} else {
					super.setValue(value);
				}
			}
		});

		this.table.getColumnExt(2).setVisible(false);
		this.table.getColumnExt(0).setVisible(false);

		this.table.addHighlighter(new ToolTipHighlighter(new AndHighlightPredicate(Arrays
				.asList(new HighlightPredicate[] { new ColumnHighlightPredicate(3),
						HighlightPredicate.IS_TEXT_TRUNCATED }))));

		Font bold = new Font("Default", Font.BOLD, 12);

		this.addColorHighlighter(Level.SEVERE, Color.red, Color.black);
		this.addFontHighlighter(Level.SEVERE, bold);

		this.addColorHighlighter(StdOutErrLevel.STDOUT, Color.white, Color.gray);
		this.addFontHighlighter(StdOutErrLevel.STDOUT, bold);
		this.addColorHighlighter(StdOutErrLevel.STDERR, Color.white, Color.red);

		this.addColorHighlighter(ScriptLogLevel.Fatal, Color.pink, Color.black);
		this.addFontHighlighter(ScriptLogLevel.Fatal, bold);
		this.addColorHighlighter(ScriptLogLevel.Error, Color.pink, Color.black);
		this.addColorHighlighter(ScriptLogLevel.Warning, Color.yellow, Color.black);
		this.addColorHighlighter(ScriptLogLevel.Info, Color.white, Color.black);
		this.addColorHighlighter(ScriptLogLevel.Trace, Color.lightGray, Color.black);
		this.addColorHighlighter(ScriptLogLevel.Success, Color.green, Color.black);

		this.addColorHighlighter(ScriptLogLevel.CompInfo, Color.cyan, Color.black);
		this.addColorHighlighter(ScriptLogLevel.CompError, Color.blue, Color.black);
		this.addFontHighlighter(ScriptLogLevel.CompError, bold);

		this.setViewportView(this.table);

		// JTableHeader header = this.table.getTableHeader();
		//
		// header.addMouseListener(new MouseAdapter() {
		// public void mouseClicked(MouseEvent evt) {
		// if (evt.getButton() == MouseEvent.BUTTON3) {
		// JTable table = ((JTableHeader) evt.getSource()).getTable();
		// TableColumnModel colModel = table.getColumnModel();
		//
		// // The index of the column whose header was clicked
		// int vColIndex = colModel.getColumnIndexAtX(evt.getX());
		//
		// System.out.println("vColIndex" + vColIndex);
		// }
		// }
		// });

	}

	private void addColorHighlighter(Level level, Color cellBackground, Color cellForeground) {
		JXTableTools.addColorHighlighter(this.table, new PatternPredicate(level.toString(), 2),
				cellBackground, cellForeground);
	}

	private void addFontHighlighter(Level level, Font font) {
		JXTableTools
				.addFontHighlighter(this.table, new PatternPredicate(level.toString(), 2), font);
	}

	public BaseModel getModel() {
		return (BaseModel) this.table.getModel();
	}

	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);
		BaseModel model = this.getModel();
		model.setLocale(l);
		this.table.setLocale(l);
		ColumnFactory cf = this.table.getColumnFactory();
		for (TableColumn tc : this.table.getColumns(true)) {
			cf.configureTableColumn(model, (TableColumnExt) tc);
		}
		// try{
		// if (SearchFactory.getInstance().sh
		// if (SearchFactory.getInstance().getSharedFindPanel() != null) {
		// SearchFactory.getInstance().getSharedFindPanel().setLocale(l);
		// }
		// if (SearchFactory.getInstance().getSharedFindBar() != null) {
		// SearchFactory.getInstance().getSharedFindBar().setLocale(l);
		// }
		// } catch(Exception e){
		// e.printStackTrace();
		// }
	}
}
