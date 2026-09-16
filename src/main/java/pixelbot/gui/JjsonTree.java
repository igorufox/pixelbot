package pixelbot.gui;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.*;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.*;

import pixelbot.components.generic.TreeTableModel;
import pixelbot.json.JSONValue;
import pixelbot.misc.*;
import pixelbot.script.misc.JSONScriptValue;

public class JjsonTree extends JScrollPane {
	private static final long serialVersionUID = -6195739169655729058L;

	protected JXTreeTable tree;

	public JjsonTree() {
		this.tree = new JXTreeTable(new TreeTableModel());
		((DefaultTreeTableModel) this.tree.getTreeTableModel()).setColumnIdentifiers(Arrays
				.asList(new String[] { "Key", "Value" }));
		this.load("");
		try {
			Thread.sleep(100);
		} catch (Exception e) {}
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				TableCellEditor tce = JjsonTree.this.tree.getCellEditor(0, 0);

				DefaultCellEditor ttce = (DefaultCellEditor) tce;

				JComponent c = (JComponent) ttce.getComponent();

				UIDefaults dialogTheme = new UIDefaults();
				dialogTheme.put("TextField.contentMargins", new InsetsUIResource(0, 5, 0, 5));
				c.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
				c.putClientProperty("Nimbus.Overrides", dialogTheme);
			}
		});
		this.tree.setDefaultEditor(Object.class, new MyTableCellEditor());

		this.tree.setRowSelectionAllowed(true);
		this.tree.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.tree.setSelectionBackground(new Color(176, 197, 227));
		this.tree.setSelectionForeground(new Color(0, 0, 128));
		this.tree.setForeground(new Color(0, 0, 128));
		this.tree.setBackground(Color.white);

		this.setViewportView(this.tree);

	}

	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);
		ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, l);
		((DefaultTreeTableModel) this.tree.getTreeTableModel()).setColumnIdentifiers(Arrays
				.asList(new String[] { messages.getString("tuning.configuration.table.key"),
						messages.getString("tuning.configuration.table.value") }));
	}

	private class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
		private static final long serialVersionUID = 482406123702939640L;

		// This is the component that will handle the editing of the cell value
		JTextArea textarea;
		JScrollPane scrollpane;
		EditableObject value;

		public MyTableCellEditor() {
			this.textarea = new JTextArea();
			this.textarea.setTabSize(4);

			UIDefaults dialogTheme = new UIDefaults();
			dialogTheme.put("TextArea.contentMargins", new InsetsUIResource(0, 5, 0, 5));
			this.textarea.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
			this.textarea.putClientProperty("Nimbus.Overrides", dialogTheme);

			this.scrollpane = new JScrollPane(this.textarea) {
				private static final long serialVersionUID = 2640496832456414572L;

				@Override
				public void setBounds(int x, int y, int width, int height) {
					Dimension prefSize = MyTableCellEditor.this.textarea.getPreferredSize();

					int h = Math.max(height, prefSize.height);
					h += 6;

					if (width < prefSize.width)
						h += 15;
					h = Math.min(h, JjsonTree.this.getViewport().getHeight());

					if (h + y > JjsonTree.this.getViewport().getHeight()
							+ JjsonTree.this.getVerticalScrollBar().getValue()) {
						y = JjsonTree.this.getVerticalScrollBar().getValue()
								+ JjsonTree.this.getViewport().getHeight() - h;
					}

					super.setBounds(x, y, width, h);
				}
			};
		}

		// This method is called when a cell value is edited by the user.
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int rowIndex, int vColIndex) {
			this.value = (EditableObject) value;
			this.textarea.setText(this.value.getEditableString());
			return this.scrollpane;
		}

		// This method is called when editing is completed.
		// It must return the new value to be stored in the cell.
		@Override
		public Object getCellEditorValue() {

			Object new_value = JSONValue.parse(this.textarea.getText());
			if (new_value == null) {
				return this.value;
			}
			return new EditableObject(new_value);
		}
	}

	public void loadFile(String file_name) {
		try (Reader fr = new UnicodeReader(new FileInputStream(file_name), "UTF-8")) {
			Object obj = JSONValue.parse(fr);
			this.load(obj);
		} catch (FileNotFoundException e) {} catch (IOException e) {}
	}

	public void load(Object obj) {
		((DefaultTreeTableModel) this.tree.getTreeTableModel()).setRoot(new JSONNode("Root", obj,
				null));
	}

	public void saveFile(String file_name) {
		String value = JSONScriptValue.toJSONString(((DefaultTreeTableModel) this.tree
				.getTreeTableModel()).getRoot().getUserObject());

		value = JSONValue.formatJs(value);
		try (Writer fw = new OutputStreamWriter(new FileOutputStream(file_name), "UTF-8")) {
			fw.write(value);
		} catch (IOException e) {}
	}

	private class JSONNode implements TreeTableNode {
		private String key;
		private Object value;

		private TreeTableNode parent;
		protected JSONNode[] children = null;

		public JSONNode(String key, Object value, TreeTableNode parent) {
			this.key = key;
			this.value = value;
			this.parent = parent;
		}

		@Override
		public String toString() {
			return this.key == null ? "null" : this.key.toString();
		}

		@Override
		public int getChildCount() {
			if (this.value instanceof Map<?, ?>) {
				return ((Map<?, ?>) this.value).size();
			} else if (this.value instanceof List<?>) {
				return ((List<?>) this.value).size();
			} else {
				return 0;
			}
		}

		@Override
		public int getIndex(TreeNode node) {
			int i = 0;
			if (this.children != null) {
				for (JSONNode c : this.children) {
					if (c == node)
						return i;
					++i;
				}
			}
			return -1;
		}

		@Override
		public boolean getAllowsChildren() {
			if (this.value instanceof Map<?, ?>) {
				return true;
			} else if (this.value instanceof List<?>) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean isLeaf() {
			return !this.getAllowsChildren();
		}

		@Override
		public Enumeration<? extends MutableTreeTableNode> children() {
			return null;
		}

		@Override
		public Object getValueAt(int index) {
			if (index == 0) {
				return this.key;
			} else if (index == 1) {
				return new EditableObject(this.value);
			} else {
				return null;
			}
		}

		@Override
		public void setValueAt(Object value, int index) {
			if (index == 0) {
				if (this.parent != null) {
					Object obj = ((EditableObject) this.parent.getValueAt(1)).getValue();
					if (obj instanceof Map<?, ?>) {
						@SuppressWarnings("unchecked") Map<String, Object> map = (Map<String, Object>) obj;
						map.remove(this.key);
						map.put(value.toString(), this.value);
					}
				}
				this.key = value.toString();
			} else if (index == 1) {
				this.value = ((EditableObject) value).getValue();
				if (this.parent != null) {
					Object obj = ((EditableObject) this.parent.getValueAt(1)).getValue();
					if (obj instanceof Map<?, ?>) {
						@SuppressWarnings("unchecked") Map<String, Object> map = (Map<String, Object>) obj;
						map.put(this.key, this.value);
					} else if (obj instanceof List<?>) {
						@SuppressWarnings("unchecked") List<Object> list = (List<Object>) obj;
						list.set(Integer.parseInt(this.key), this.value);
					}
					this.children = null;

					TreeTableModel model = ((TreeTableModel) ((TreeTableModelProvider) JjsonTree.this.tree
							.getModel()).getTreeTableModel());
					TreePath path = new TreePath(model.getPathToRoot(this));
					model.fireTreeStructureChanged(path);

				}
			}
		}

		@Override
		public TreeTableNode getChildAt(int p) {
			if (p < 0 || p > this.getChildCount())
				return null;
			if (this.children == null || p >= this.children.length) {
				this.children = new JSONNode[this.getChildCount()];
			}

			if (this.children[p] == null) {
				if (this.value instanceof Map<?, ?>) {
					Map<?, ?> map = (Map<?, ?>) this.value;
					String[] keys = map.keySet().toArray(new String[] {});
					Arrays.sort(keys);
					this.children[p] = new JSONNode(keys[p], map.get(keys[p]), this);
				} else if (this.value instanceof List<?>) {
					List<?> list = (List<?>) this.value;
					this.children[p] = new JSONNode(Integer.toString(p), list.get(p), this);
				}

			}
			return this.children[p];
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public TreeTableNode getParent() {
			return this.parent;
		}

		@Override
		public boolean isEditable(int index) {
			if (index == 0) {
				if (this.parent != null) {
					Object obj = ((EditableObject) this.parent.getValueAt(1)).getValue();
					if (obj instanceof List<?>) {
						return false;
					}
				}
			}
			return true;
		}

		@Override
		public Object getUserObject() {
			return this.value;
		}

		@Override
		public void setUserObject(Object obj) {
			this.value = obj;
		}

	}

	private class EditableObject {
		private Object value;

		public EditableObject(Object value) {
			this.value = value;
		}

		@Override
		public String toString() {
			if (this.getValue() == null) {
				return "null";
			} else if (this.getValue() instanceof Map<?, ?>) {
				return "{...}";
			} else if (this.getValue() instanceof List<?>) {
				return "[...]";
			} else if (this.getValue() instanceof String) {
				return "\"" + this.getValue() + "\"";
			} else {
				return this.getValue().toString();
			}
		}

		public String getEditableString() {
			String result = JSONScriptValue.toJSONString(this.getValue());

			result = JSONValue.formatJs(result);
			return result;
		}

		public Object getValue() {
			return this.value;
		}

	}

}
