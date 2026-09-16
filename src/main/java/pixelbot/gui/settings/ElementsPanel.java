package pixelbot.gui.settings;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import pixelbot.components.generic.*;
import pixelbot.components.generic.JSelectablePicture.StateEvent;
import pixelbot.components.generic.JSelectablePicture.StateListener;
import pixelbot.elements.*;
import pixelbot.elements.IElementDescriptor.ColorType;
import pixelbot.misc.Tools;
import pixelbot.script.general.ScriptManager;

public class ElementsPanel extends JPanel {
	private static final long serialVersionUID = 3977739001093084667L;

	private class ElementTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -2505483013058400312L;

		private List<ElementDescriptorBase> data = new Vector<>();

		@Override
		public int getRowCount() {
			return this.data.size();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return Icon.class;
			case 1:
				return Boolean.class;
			case 2:
				return Boolean.class;
			}
			return super.getColumnClass(columnIndex);
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "Element";
			case 1:
				return "RU";
			case 2:
				return "EN";
			}
			return super.getColumnName(column);
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ElementDescriptorBase ed = this.data.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return ed;
			case 1:
				return Boolean.valueOf(ed.getLocale().contains(IElementDescriptor.Locale.ru));
			case 2:
				return Boolean.valueOf(ed.getLocale().contains(IElementDescriptor.Locale.en));
			}
			return null;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

			ElementDescriptorBase ed = this.data.get(rowIndex);
			switch (columnIndex) {
			case 1:
				if (Boolean.TRUE.equals(aValue)) {
					ed.getLocale().add(IElementDescriptor.Locale.ru);
				} else {
					ed.getLocale().remove(IElementDescriptor.Locale.ru);
				}
				break;
			case 2:
				if (Boolean.TRUE.equals(aValue)) {
					ed.getLocale().add(IElementDescriptor.Locale.en);
				} else {
					ed.getLocale().remove(IElementDescriptor.Locale.en);
				}
				break;
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return false;
			case 1:
			case 2:
				return true;
			}
			return super.isCellEditable(rowIndex, columnIndex);
		}

		public void clear() {
			this.data.clear();
			this.fireTableDataChanged();
		}

		public void addElement(ElementDescriptorBase elem) {
			this.data.add(elem);
			this.fireTableRowsInserted(this.data.size() - 1, this.data.size() - 1);
		}

	}

	private JToolBar jToolBar_elements;
	private JButton jButton_elements_grab;
	private JButton jButton_elements_add;
	private JButton jButton_elements_replace;
	private JButton jButton_elements_remove;
	private JButton jButton_elements_test;
	private JButton jButton_elements_save;
	private JButton jButton_elements_impexp;
	protected JComboBox<String> jComboBox_learn_model;
	protected JSpinner jSpinner_element_order;

	private JPanel jPanel_elements_TL_labels;
	protected JLabel jLabel_mouse_coordinates;
	private JLabel jLabel_ms;
	protected JLabel jLabel_search_time_data;
	private JLabel jLabel_coordinates;
	private JLabel jLabel_search_time;
	private JLabel jLabel_selected_color;
	private JLabel jLabel_color_under_cursor;
	protected JLabel jLabel_found_elems;
	protected JPanel jPanel_color_under_cursor;
	protected JPanel jPanel_color_selected_color;
	protected JPicture jPicture_new_element;

	protected JScrollPane jScrollPane_elements_grab;
	protected JSelectablePicture jSelectablePicture_elements_grab;

	private JPanel jPanel_elements_bottom;
	protected JTextField jTextField_element_category;
	protected JTree jTree_elements;
	private JScrollPane jScrollPane_elements_list;
	private JScrollPane jScrollPane_cur_element;
	protected JTable jTable_cur_element;
	protected ElementDescriptorBase ed_elements_new;

	protected ScriptManager script;

	public ElementsPanel(ScriptManager script) {
		super();
		this.script = script;

		this.jToolBar_elements = new JToolBar();
		this.jButton_elements_grab = new JButton();
		this.jButton_elements_add = new JButton();
		this.jButton_elements_replace = new JButton();
		this.jButton_elements_remove = new JButton();
		this.jButton_elements_test = new JButton();
		this.jButton_elements_save = new JButton();
		this.jButton_elements_impexp = new JButton();
		this.jComboBox_learn_model = new JComboBox<String>();
		this.jSpinner_element_order = new JSpinner(new SpinnerNumberModel(0, -100, 100000, 1));

		this.jPanel_elements_TL_labels = new JPanel();
		this.jPanel_color_under_cursor = new JPanel();
		this.jPanel_color_selected_color = new JPanel();
		this.jLabel_selected_color = new JLabel();
		this.jLabel_color_under_cursor = new JLabel();
		this.jLabel_search_time_data = new JLabel();
		this.jLabel_mouse_coordinates = new JLabel();
		this.jLabel_coordinates = new JLabel();
		this.jLabel_search_time = new JLabel();
		this.jLabel_ms = new JLabel();
		this.jLabel_found_elems = new JLabel();
		this.jScrollPane_elements_grab = new JScrollPane();
		this.jSelectablePicture_elements_grab = new JSelectablePicture();
		this.jPicture_new_element = new JPicture();

		this.jPanel_elements_bottom = new JPanel();
		this.jTextField_element_category = new JTextField();
		this.jScrollPane_elements_list = new JScrollPane();
		this.jTree_elements = new JTree();

		this.jScrollPane_cur_element = new JScrollPane();
		this.jTable_cur_element = new JTable(new ElementTableModel());
		((DefaultTableColumnModel) this.jTable_cur_element.getColumnModel()).getColumn(1)
				.setMinWidth(30);
		((DefaultTableColumnModel) this.jTable_cur_element.getColumnModel()).getColumn(1)
				.setMaxWidth(30);
		((DefaultTableColumnModel) this.jTable_cur_element.getColumnModel()).getColumn(2)
				.setMinWidth(30);
		((DefaultTableColumnModel) this.jTable_cur_element.getColumnModel()).getColumn(2)
				.setMaxWidth(30);

		GroupLayout gl = new GroupLayout(this);
		this.setLayout(gl);
		gl.setHorizontalGroup(gl
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(this.jToolBar_elements, 0, GroupLayout.PREFERRED_SIZE,
						Short.MAX_VALUE)
				.addGroup(
						GroupLayout.Alignment.TRAILING,
						gl.createSequentialGroup()
								.addComponent(this.jPanel_elements_TL_labels, 120, 120, 120)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(this.jScrollPane_elements_grab))
				.addComponent(this.jPanel_elements_bottom, GroupLayout.Alignment.TRAILING,
						GroupLayout.DEFAULT_SIZE, 914, Short.MAX_VALUE));

		gl.setVerticalGroup(gl
				.createSequentialGroup()
				.addComponent(this.jToolBar_elements, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGroup(
						gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(this.jPanel_elements_TL_labels,
										GroupLayout.Alignment.LEADING, 0, 100, Short.MAX_VALUE)
								.addComponent(this.jScrollPane_elements_grab, 0, 100,
										Short.MAX_VALUE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(this.jPanel_elements_bottom, GroupLayout.PREFERRED_SIZE, 215,
						GroupLayout.PREFERRED_SIZE));

		this.jToolBar_elements.setFloatable(false);

		this.jButton_elements_grab.setIcon(new ImageIcon(this.getClass().getResource(
				"/pixelbot/resources/elements/grab.png")));
		this.jButton_elements_add.setIcon(new ImageIcon(this.getClass().getResource(
				"/pixelbot/resources/elements/add.png")));
		this.jButton_elements_replace.setIcon(new ImageIcon(this.getClass().getResource(
				"/pixelbot/resources/elements/replace.png")));
		this.jButton_elements_remove.setIcon(new ImageIcon(this.getClass().getResource(
				"/pixelbot/resources/elements/delete.png")));
		this.jButton_elements_test.setIcon(new ImageIcon(this.getClass().getResource(
				"/pixelbot/resources/elements/test.png")));
		this.jButton_elements_save.setIcon(new ImageIcon(this.getClass().getResource(
				"/pixelbot/resources/elements/save.png")));
		this.jButton_elements_impexp.setIcon(new ImageIcon(this.getClass().getResource(
				"/pixelbot/resources/elements/export.png")));

		this.jButton_elements_grab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BufferedImage image = ElementsPanel.this.script.getHelper().getScreen();
				ElementsPanel.this.jSelectablePicture_elements_grab.setIcon(new ImageIcon(image));
			}
		});

		this.jButton_elements_add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				String key = ElementsPanel.this.jTextField_element_category.getText();
				if (ElementsPanel.this.ed_elements_new == null)
					return;

				Elements elements = ElementsPanel.this.script.getHelper().elements;
				IElementDescriptor elem = elements.get(key);
				if (elem == null) {
					elements.put(
							key,
							new ElementDescriptor(
									ColorType.values()[ElementsPanel.this.jComboBox_learn_model
											.getSelectedIndex()],
									((Number) (ElementsPanel.this.jSpinner_element_order.getValue()))
											.intValue(), new Date(),
									ElementsPanel.this.ed_elements_new));
				} else if (elem instanceof ElementDescriptor) {
					elements.put(
							key,
							new ElementList(
									ColorType.values()[ElementsPanel.this.jComboBox_learn_model
											.getSelectedIndex()],
									((Number) (ElementsPanel.this.jSpinner_element_order.getValue()))
											.intValue(), new Date(), (ElementDescriptor) elem,
									ElementsPanel.this.ed_elements_new));
				} else if (elem instanceof ElementList) {
					((ElementList) elem).add(ElementsPanel.this.ed_elements_new);
				}

				ElementsPanel.this.jTree_elements_fill();
			}
		});

		this.jButton_elements_replace.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				String key = ElementsPanel.this.jTextField_element_category.getText();
				ElementsPanel.this.script.getHelper().elements.put(
						key,
						new ElementDescriptor(
								ColorType.values()[ElementsPanel.this.jComboBox_learn_model
										.getSelectedIndex()],
								((Number) (ElementsPanel.this.jSpinner_element_order.getValue()))
										.intValue(), new Date(), ElementsPanel.this.ed_elements_new));
				ElementsPanel.this.jTree_elements_fill();
			}
		});

		this.jButton_elements_remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				String key = ElementsPanel.this.jTree_elements_get_selection_key();
				if (key != null) {
					Elements elements = ElementsPanel.this.script.getHelper().elements;

					ElementDescriptorBase elem = null;
					if (ElementsPanel.this.jTable_cur_element.getSelectedRow() != -1) {
						elem = (ElementDescriptorBase) ElementsPanel.this.jTable_cur_element
								.getModel().getValueAt(
										ElementsPanel.this.jTable_cur_element.getSelectedRow(), 0);
					}

					if (elem == null) {
						if (elements.containsKey(key)) {
							elements.remove(key);
						} else {
							for (String k : new HashSet<>(elements.keySet())) {
								if (k.startsWith(key)) {
									elements.remove(k);
								}
							}
						}
					} else {
						IElementDescriptor ed = elements.get(key);
						if (ed instanceof ElementDescriptor) {
							elements.remove(key);
						} else if (ed instanceof ElementList) {
							((ElementList) ed).remove(elem);
							if (((ElementList) ed).size() == 1) {
								elements.put(key, ((ElementList) ed).getDescriptor());
							}
						}
					}

					ElementsPanel.this.jTree_elements_fill();
				}

			}
		});

		this.jButton_elements_test.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				String key = ElementsPanel.this.jTree_elements_get_selection_key();
				if (key != null) {
					long start_time = System.nanoTime();
					List<Rectangle> l = ElementsPanel.this.script.getHelper().findElements(key);
					ElementsPanel.this.jLabel_search_time_data.setText(String.format(
							"%d",
							new Object[] { Long.valueOf((System.nanoTime() - start_time) / 1000000L) }));
					ElementsPanel.this.jLabel_found_elems.setText(l == null ? "<null>" : Integer
							.toString(l.size()));
				}

			}
		});

		this.jButton_elements_save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				ElemTools.saveElements(ElementsPanel.this.script.getHelper().elements, new File(
						Tools.workdir, "elements.elz"), true);
				ElementsPanel.this.jTree_elements_fill();
			}
		});

		this.jButton_elements_impexp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				ImportDialog id = new ImportDialog(ElementsPanel.this.script);
				id.setLocationRelativeTo(ElementsPanel.this);
				id.setVisible(true);
				ElementsPanel.this.jTree_elements_fill();
			}
		});

		this.jComboBox_learn_model
				.setModel(new DefaultComboBoxModel<String>(new String[] { "Все цвета", "Один цвет",
						"Распределение", "Монотонный", "One color (Dispersion)" }));

		this.jSpinner_element_order.setPreferredSize(new Dimension(100,
				(int) this.jSpinner_element_order.getPreferredSize().getHeight()));

		this.jToolBar_elements.add(this.jButton_elements_grab);
		this.jToolBar_elements.add(new JToolBar.Separator());
		this.jToolBar_elements.add(this.jButton_elements_add);
		this.jToolBar_elements.add(this.jButton_elements_replace);
		this.jToolBar_elements.add(this.jButton_elements_remove);
		this.jToolBar_elements.add(new JToolBar.Separator());
		this.jToolBar_elements.add(this.jButton_elements_test);
		this.jToolBar_elements.add(this.jButton_elements_save);
		this.jToolBar_elements.add(this.jButton_elements_impexp);
		this.jToolBar_elements.add(new JToolBar.Separator());
		this.jToolBar_elements.add(this.jComboBox_learn_model);
		this.jToolBar_elements.add(this.jSpinner_element_order);

		this.jPanel_color_under_cursor.setBorder(BorderFactory.createEtchedBorder());

		GroupLayout gl_jPanel_color_under_cursor = new GroupLayout(this.jPanel_color_under_cursor);
		this.jPanel_color_under_cursor.setLayout(gl_jPanel_color_under_cursor);
		gl_jPanel_color_under_cursor
				.setHorizontalGroup(gl_jPanel_color_under_cursor.createParallelGroup(
						GroupLayout.Alignment.LEADING).addGap(0, 100, Short.MAX_VALUE));

		gl_jPanel_color_under_cursor.setVerticalGroup(gl_jPanel_color_under_cursor
				.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));

		this.jPanel_color_selected_color.setBorder(BorderFactory.createEtchedBorder());

		GroupLayout gl_jPanel_color_selected_color = new GroupLayout(
				this.jPanel_color_selected_color);
		this.jPanel_color_selected_color.setLayout(gl_jPanel_color_selected_color);
		gl_jPanel_color_selected_color
				.setHorizontalGroup(gl_jPanel_color_selected_color.createParallelGroup(
						GroupLayout.Alignment.LEADING).addGap(0, 100, Short.MAX_VALUE));

		gl_jPanel_color_selected_color.setVerticalGroup(gl_jPanel_color_selected_color
				.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 15, Short.MAX_VALUE));
		this.jLabel_mouse_coordinates.setText("0,0");

		GroupLayout gl_jPanel_elements_TL_labels = new GroupLayout(this.jPanel_elements_TL_labels);
		this.jPanel_elements_TL_labels.setLayout(gl_jPanel_elements_TL_labels);
		gl_jPanel_elements_TL_labels.setHorizontalGroup(gl_jPanel_elements_TL_labels
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(this.jLabel_color_under_cursor)
				.addComponent(this.jPanel_color_under_cursor)
				.addComponent(this.jLabel_coordinates)
				.addComponent(this.jLabel_mouse_coordinates)
				.addComponent(this.jLabel_selected_color)
				.addComponent(this.jPanel_color_selected_color)
				.addGroup(
						gl_jPanel_elements_TL_labels.createSequentialGroup()
								.addComponent(this.jLabel_search_time)
								.addComponent(this.jLabel_search_time_data)
								.addComponent(this.jLabel_ms))
				.addComponent(this.jLabel_found_elems)

				.addComponent(this.jPicture_new_element));

		gl_jPanel_elements_TL_labels.setVerticalGroup(gl_jPanel_elements_TL_labels
				.createSequentialGroup()
				.addComponent(this.jLabel_color_under_cursor)
				.addComponent(this.jPanel_color_under_cursor, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.jLabel_coordinates)
				.addComponent(this.jLabel_mouse_coordinates)
				.addComponent(this.jLabel_selected_color)
				.addComponent(this.jPanel_color_selected_color, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGroup(
						gl_jPanel_elements_TL_labels
								.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(this.jLabel_search_time)
								.addComponent(this.jLabel_search_time_data)
								.addComponent(this.jLabel_ms))
				.addComponent(this.jLabel_found_elems)

				.addComponent(this.jPicture_new_element));

		this.jSelectablePicture_elements_grab.addStateListener(new StateListener() {
			@Override
			public void action(StateEvent event) {
				ElementsPanel.this.jLabel_mouse_coordinates.setText(String.format("%d, %d",
						event.x, event.y));
				if (event.type == pixelbot.components.generic.JSelectablePicture.StateEvent.Type.a) {
					ElementsPanel.this.jPanel_color_under_cursor.setBackground(event.color);
				} else {
					Rectangle rect = ElementsPanel.this.jSelectablePicture_elements_grab
							.getSelrect();
					if (rect.width <= 1 && rect.height <= 1) {
						ElementsPanel.this.jPanel_color_selected_color.setBackground(event.color);
					}
				}
				if (ElementsPanel.this.jSelectablePicture_elements_grab.mpressed) {
					BufferedImage image = ElementsPanel.this.jSelectablePicture_elements_grab
							.getSelImage();
					ElementsPanel.this.ed_elements_new = ElementsPanel.this.script.getHelper()
							.learnElement(
									image,
									ColorType.values()[ElementsPanel.this.jComboBox_learn_model
											.getSelectedIndex()],
									ElementsPanel.this.jPanel_color_selected_color.getBackground()
											.getRGB());
					ElementsPanel.this.jPicture_new_element
							.setIcon(ElementsPanel.this.ed_elements_new);
				}
			}
		});

		this.jScrollPane_elements_grab.setViewportView(this.jSelectablePicture_elements_grab);

		this.jScrollPane_elements_list.setViewportView(this.jTree_elements);
		this.jTree_elements.setModel(new DefaultTreeModel(new SortableTreeNode(null)));
		this.jTree_elements.setRootVisible(false);
		this.jTree_elements.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.jTree_elements.setEditable(true);

		this.jTree_elements.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent paramTreeSelectionEvent) {
				ElementsPanel.this.jTree_elements_ValueChanged();
			}
		});

		this.jTree_elements.getModel().addTreeModelListener(new TreeModelListener() {

			@Override
			public void treeStructureChanged(TreeModelEvent event) {}

			@Override
			public void treeNodesRemoved(TreeModelEvent event) {}

			@Override
			public void treeNodesInserted(TreeModelEvent event) {}

			@Override
			public void treeNodesChanged(TreeModelEvent event) {
				jTree_elements_inline_node_edited(event.getChildren()[0]);
			}
		});

		this.jScrollPane_cur_element.setViewportView(this.jTable_cur_element);

		this.jTable_cur_element.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (ElementsPanel.this.jTable_cur_element.getSelectedRow() != -1) {
							ElementsPanel.this.ed_elements_new = (ElementDescriptorBase) ElementsPanel.this.jTable_cur_element
									.getModel().getValueAt(
											ElementsPanel.this.jTable_cur_element.getSelectedRow(),
											0);
							ElementsPanel.this.jPicture_new_element
									.setIcon(ElementsPanel.this.ed_elements_new);
						}
					}
				});

		GroupLayout gl_jPanel_elements_bottom = new GroupLayout(this.jPanel_elements_bottom);
		this.jPanel_elements_bottom.setLayout(gl_jPanel_elements_bottom);
		gl_jPanel_elements_bottom.setHorizontalGroup(gl_jPanel_elements_bottom
				.createSequentialGroup()
				.addGap(10)
				.addGroup(
						gl_jPanel_elements_bottom
								.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.jTextField_element_category,
										GroupLayout.DEFAULT_SIZE, 200, 400)
								.addComponent(this.jScrollPane_elements_list,
										GroupLayout.DEFAULT_SIZE, 200, 400))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(this.jScrollPane_cur_element, GroupLayout.DEFAULT_SIZE, 200,
						Short.MAX_VALUE));
		gl_jPanel_elements_bottom.setVerticalGroup(gl_jPanel_elements_bottom
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						gl_jPanel_elements_bottom
								.createSequentialGroup()
								.addComponent(this.jTextField_element_category,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(this.jScrollPane_elements_list,
										GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))
				.addComponent(this.jScrollPane_cur_element, GroupLayout.DEFAULT_SIZE,
						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

	}

	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);

		ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, l);

		this.jButton_elements_grab.setToolTipText(messages
				.getString("tuning.elements.buttons.grab"));
		this.jButton_elements_add.setToolTipText(messages.getString("tuning.elements.buttons.add"));
		this.jButton_elements_replace.setToolTipText(messages
				.getString("tuning.elements.buttons.replace"));
		this.jButton_elements_remove.setToolTipText(messages
				.getString("tuning.elements.buttons.remove"));
		this.jButton_elements_test.setToolTipText(messages
				.getString("tuning.elements.buttons.test"));
		this.jButton_elements_save.setToolTipText(messages
				.getString("tuning.elements.buttons.save"));
		this.jButton_elements_impexp.setToolTipText(messages
				.getString("tuning.elements.buttons.impexp"));

		this.jLabel_color_under_cursor.setText(messages
				.getString("tuning.elements.learning.color_under_cursor"));
		this.jLabel_coordinates.setText(messages.getString("tuning.elements.learning.coordinates"));
		this.jLabel_selected_color.setText(messages
				.getString("tuning.elements.learning.selected_color"));
		this.jLabel_search_time.setText(messages.getString("tuning.elements.learning.search_time"));
		this.jLabel_ms.setText(messages.getString("tuning.elements.learning.ms"));

		this.jComboBox_learn_model.removeAllItems();
		this.jComboBox_learn_model.addItem(messages.getString("tuning.elements.learn_model.0"));
		this.jComboBox_learn_model.addItem(messages.getString("tuning.elements.learn_model.1"));
		this.jComboBox_learn_model.addItem(messages.getString("tuning.elements.learn_model.2"));
		this.jComboBox_learn_model.addItem(messages.getString("tuning.elements.learn_model.3"));
		this.jComboBox_learn_model.addItem(messages.getString("tuning.elements.learn_model.4"));

	}

	protected String jTree_elements_get_selection_key() {
		if (this.jTree_elements.getSelectionCount() > 0) {
			return ElementsPanel.jTree_get_key(((DefaultMutableTreeNode) this.jTree_elements
					.getSelectionPath().getLastPathComponent()));
		}
		return null;
	}

	private static String jTree_get_key(DefaultMutableTreeNode node) {
		Object[] path = node.getUserObjectPath();
		StringBuffer sb = new StringBuffer();
		for (Object o : path) {
			if (o != null) {
				sb.append(o);
				sb.append('.');
			}
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	public void jTree_elements_fill() {
		DefaultTreeModel model = (DefaultTreeModel) this.jTree_elements.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		root.removeAllChildren();

		String selection = ElementsPanel.this.jTextField_element_category.getText();
		DefaultMutableTreeNode sel_node = null;
		for (String key : this.script.getHelper().elements.keySet()) {
			String[] string_path = key.split("\\.");
			DefaultMutableTreeNode cur_node = root;
			for (String cur_path : string_path) {
				DefaultMutableTreeNode child = null;
				for (int i = 0; i < cur_node.getChildCount(); ++i) {
					if (((DefaultMutableTreeNode) cur_node.getChildAt(i)).getUserObject().equals(
							cur_path)) {
						child = (DefaultMutableTreeNode) cur_node.getChildAt(i);
						break;
					}
				}
				if (child == null) {
					child = new SortableTreeNode(cur_path);
					cur_node.add(child);
				}
				cur_node = child;
			}
			if (key.equals(selection)) {
				sel_node = cur_node;
			}

		}
		model.nodeStructureChanged(root);
		try {
			TreePath path = null;
			if (sel_node == null) {
				String[] ss = selection.split("\\.");
				List<TreeNode> lpath = new ArrayList<TreeNode>();
				DefaultMutableTreeNode cur_node = root;
				lpath.add(cur_node);
				boolean f = false;
				for (String s : ss) {
					f = false;
					for (int i = 0; i < cur_node.getChildCount(); ++i) {
						if (((DefaultMutableTreeNode) cur_node.getChildAt(i)).getUserObject()
								.equals(s)) {
							cur_node = (DefaultMutableTreeNode) cur_node.getChildAt(i);
							lpath.add(cur_node);
							f = true;
							break;
						}
					}

					if (!f) {
						break;
					}
				}

				path = new TreePath(lpath.toArray(new TreeNode[] {}));
			} else {
				path = new TreePath(sel_node.getPath());
			}

			this.jTree_elements.setSelectionPath(path);
			this.jTree_elements.expandPath(path);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void jTree_elements_ValueChanged() {
		String key = this.jTree_elements_get_selection_key();
		if (key == null)
			return;
		IElementDescriptor elem = this.script.getHelper().elements.get(key);
		this.jTextField_element_category.setText(key);
		ElementTableModel model = (ElementTableModel) this.jTable_cur_element.getModel();

		model.clear();

		if (elem == null)
			return;

		if (elem instanceof ElementDescriptor) {
			model.addElement((ElementDescriptor) elem);
		} else if (elem instanceof ElementList) {
			for (ElementDescriptorBase ed : ((ElementList) elem)) {
				model.addElement(ed);
			}
		}

		this.jComboBox_learn_model.setSelectedIndex(elem.getColorType().ordinal());
		this.jSpinner_element_order.setValue(Integer.valueOf(elem.getOrder()));
	}

	protected void jTree_elements_inline_node_edited(Object o) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
		String new_key = ElementsPanel.jTree_get_key(node);
		String old_key = this.jTextField_element_category.getText();

		boolean found = false;
		do {
			found = false;
			try {
				for (String key : this.script.getHelper().elements.keySet()) {
					if (key.startsWith(old_key)) {
						IElementDescriptor ed = this.script.getHelper().elements.remove(key);
						this.script.getHelper().elements.put(key.replace(old_key, new_key), ed);
					}
				}
			} catch (Exception e) {
				found = true;
			}
		} while (found);
		this.jTree_elements_fill();
	}

}
