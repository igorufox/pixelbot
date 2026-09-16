package pixelbot.components;

import java.awt.Color;
import java.util.*;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.*;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.*;
import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.treetable.TreeTableNode;

import pixelbot.components.generic.TreeTableModel;
import pixelbot.misc.*;

public class JStatisticsTree extends JScrollPane {
	private static final long serialVersionUID = 6967894103263990485L;
	protected JXTreeTable tree;
	private boolean extended = false;

	public boolean isExtended() {
		return this.extended;
	}

	public void setExtended(boolean extended) {
		this.extended = extended;
	}

	public JStatisticsTree() {
		this.tree = new JXTreeTable(new TreeTableModel());
		getTreeModel().setRoot(new StatRootNode());

		// this.tree.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.tree.getTableHeader().setReorderingAllowed(false);

		this.getTreeModel().setColumnIdentifiers(
				Arrays.asList(this.extended ? new String[] { "Event", "Success", "Fail",
						"Total Success", "Total Fail" }
						: new String[] { "Event", "Success", "Fail" }));

		// this.tree.addHighlighter(new ToolTipHighlighter(new AndHighlightPredicate(Arrays
		// .asList(new HighlightPredicate[] { new ColumnHighlightPredicate(3),
		// HighlightPredicate.IS_TEXT_TRUNCATED }))));
		this.tree.addHighlighter(new ToolTipHighlighter(HighlightPredicate.IS_TEXT_TRUNCATED));

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JStatisticsTree.this.tree.getColumnModel().getColumn(0).setPreferredWidth(200);

				TableCellRenderer tcr = JStatisticsTree.this.tree.getCellRenderer(0, 0);
				JComponent c = (JComponent) tcr;
				UIDefaults dialogTheme = new UIDefaults();
				dialogTheme.put("Tree.leftChildIndent", Integer.valueOf(10));
				dialogTheme.put("Tree.rightChildIndent", Integer.valueOf(0));

				c.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
				c.putClientProperty("Nimbus.Overrides", dialogTheme);
			}
		});

		this.tree.setClosedIcon(IconValue.NULL_ICON);
		this.tree.setOpenIcon(IconValue.NULL_ICON);
		this.tree.setLeafIcon(IconValue.NULL_ICON);

		this.setViewportView(this.tree);
	}

	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		if (this.tree != null)
			this.tree.setBackground(bg);
	}

	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);

		ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, l);
		// TableColumnModel tcm = this.tree.getColumnModel();
		// tcm.getColumn(0).setHeaderValue(messages.getString("control.statistics.event"));
		// tcm.getColumn(1).setHeaderValue(messages.getString("control.statistics.success"));
		// tcm.getColumn(2).setHeaderValue(messages.getString("control.statistics.fail"));
		// if (this.extended) {
		// tcm.getColumn(3).setHeaderValue(messages.getString("control.statistics.total_success"));
		// tcm.getColumn(4).setHeaderValue(messages.getString("control.statistics.total_fail"));
		// }

		this.getTreeModel().setColumnIdentifiers(
				Arrays.asList(this.extended ? new String[] {
						messages.getString("control.statistics.event"),
						messages.getString("control.statistics.success"),
						messages.getString("control.statistics.fail"),
						messages.getString("control.statistics.total_success"),
						messages.getString("control.statistics.total_fail") } : new String[] {
						messages.getString("control.statistics.event"),
						messages.getString("control.statistics.success"),
						messages.getString("control.statistics.fail") }));
	}

	private abstract class StatNode implements TreeTableNode {
		private String name = "";
		private TreeTableNode parent;

		public StatNode(String name, StatNode parent) {
			this.name = name;
			this.parent = parent;
		}

		public String getName() {
			return this.name;
		}

		@Override
		public TreeTableNode getParent() {
			return this.parent;
		}

		@Override
		public final Object getUserObject() {
			return null;
		}

		@Override
		public final void setUserObject(Object obj) {}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public boolean isEditable(int index) {
			return false;
		}

		public abstract void restart();

		public abstract int getSuccesss();

		public abstract int getFails();

		@Override
		public Object getValueAt(int index) {
			switch (index) {
			case 0:
				return this.getName();
			case 1:
				return Integer.toString(this.getSuccesss());
			case 2:
				return Integer.toString(this.getFails());
			default:
				return null;
			}
		}

		@Override
		public final void setValueAt(Object value, int index) {}

	}

	private class StatLeafNode extends StatNode implements TreeTableNode {
		private int sucess = 0;
		private int fails = 0;

		public StatLeafNode(String name, StatNode parent) {
			super(name, parent);
		}

		public void consider(int value) {
			if (value > 0) {
				this.sucess += value;
			} else {
				this.fails -= value;
			}
		}

		@Override
		public void restart() {
			this.sucess = 0;
			this.fails = 0;
		}

		@Override
		public int getChildCount() {
			return 0;
		}

		@Override
		public int getIndex(TreeNode node) {
			return -1;
		}

		@Override
		public boolean getAllowsChildren() {
			return false;
		}

		@Override
		public boolean isLeaf() {
			return true;
		}

		@Override
		public Enumeration<? extends TreeTableNode> children() {
			return null;
		}

		@Override
		public TreeTableNode getChildAt(int p) {
			return null;
		}

		@Override
		public int getSuccesss() {
			return this.sucess;
		}

		@Override
		public int getFails() {
			return this.fails;
		}

	}

	private class StatBranchNode extends StatLeafNode implements TreeTableNode {
		protected ArrayList<StatNode> children = new ArrayList<StatNode>();

		public StatBranchNode(String name, StatNode parent) {
			super(name, parent);
		}

		@Override
		public int getChildCount() {
			return this.children.size();
		}

		@Override
		public int getIndex(TreeNode node) {
			int i = 0;
			for (StatNode c : this.children) {
				if (c == node)
					return i;
				++i;
			}
			return -1;
		}

		@Override
		public boolean getAllowsChildren() {
			return true;
		}

		@Override
		public boolean isLeaf() {
			return this.children.isEmpty();
		}

		@Override
		public Enumeration<? extends TreeTableNode> children() {
			return new Enumerator<StatNode>(this.children);
		}

		@Override
		public TreeTableNode getChildAt(int p) {
			if (p < 0 || p > this.getChildCount())
				return null;

			return this.children.get(p);
		}

		@Override
		public int getColumnCount() {
			return 5;
		}

		@Override
		public boolean isEditable(int index) {
			return false;
		}

		@Override
		public int getSuccesss() {
			int result = super.getSuccesss();
			for (StatNode node : this.children)
				result += node.getSuccesss();
			return result;
		}

		@Override
		public int getFails() {
			int result = super.getFails();
			for (StatNode node : this.children)
				result += node.getFails();
			return result;
		}

		protected StatNode getChildNode(String name) {
			for (StatNode ttn : this.children) {
				if (ttn.getName().equals(name)) {
					return ttn;
				}
			}
			return null;
		}

		public void addStatistics(List<String> path, int i) {
			List<String> path_ = new ArrayList<String>(path);
			String p = path_.remove(0);
			StatNode sn = this.getChildNode(p);
			if (path_.size() == 0) {
				if (sn == null) {
					sn = new StatLeafNode(p, this);
					this.children.add(sn);
					TreePath tp = new TreePath(getTreeModel().getPathToRoot(this));
					getTreeModel().fireChildAdded(tp, this.children.size() - 1, sn);
				}
				if (sn instanceof StatLeafNode) {
					((StatLeafNode) sn).consider(i);
				}
				TreePath tp = new TreePath(getTreeModel().getPathToRoot(sn));
				getTreeModel().firePathChanged(tp);
			} else {
				if (sn == null) {
					sn = new StatBranchNode(p, this);
					this.children.add(sn);
					TreePath tp = new TreePath(getTreeModel().getPathToRoot(this));
					getTreeModel().fireChildAdded(tp, this.children.size() - 1, sn);
				}
				((StatBranchNode) sn).addStatistics(path_, i);
				TreePath tp = new TreePath(getTreeModel().getPathToRoot(sn));
				getTreeModel().firePathChanged(tp);
			}

		}

		@Override
		public void restart() {
			super.restart();
			this.children.clear();
			TreePath tp = new TreePath(JStatisticsTree.this.getTreeModel().getPathToRoot(this));
			JStatisticsTree.this.getTreeModel().fireTreeStructureChanged(tp);
		}

	}

	private class StatRootNode extends StatBranchNode {
		public StatRootNode() {
			super("", null);
		}
	}

	protected TreeTableModel getTreeModel() {
		return (TreeTableModel) this.tree.getTreeTableModel();
	}

	protected StatRootNode getTreeRoot() {
		return (StatRootNode) this.getTreeModel().getRoot();
	}

	public void addStatistics(String data, int i) {
		this.getTreeRoot().addStatistics(Arrays.asList(data.split("\\.")), i);
	}

	public void restart() {
		this.getTreeRoot().restart();
	}

}
