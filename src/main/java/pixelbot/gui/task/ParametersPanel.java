package pixelbot.gui.task;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import pixelbot.components.generic.*;
import pixelbot.components.generic.JFileField.JFileFieldSelectListener;
import pixelbot.json.JSONValue;
import pixelbot.misc.*;
import pixelbot.script.general.ScriptManager;
import pixelbot.script.misc.*;
import pixelbot.script.scope.general.*;

public class ParametersPanel extends JScrollPane {
	private static final long serialVersionUID = 1495960715636792000L;

	private JPanel values_panel = new JPanel();
	private ScriptManager script;
	private ScriptParamsChangeListener script_change_listener;

	public ParametersPanel(ScriptManager script) {
		super();
		this.getVerticalScrollBar().setUnitIncrement(16);
		this.setViewportView(this.values_panel);
		this.values_panel.setLayout(new GridBagLayout());
		this.script = script;
	}

	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);

	}

	public interface ScriptParamsChangeListener extends ItemListener, ActionListener,
			JFileFieldSelectListener, ChangeListener {}

	public void setScriptChangeListener(ScriptParamsChangeListener listener) {
		this.script_change_listener = listener;
	}

	private void removeListeners(JComponent component) {
		for (Component child : component.getComponents()) {
			if (child instanceof JTextField) {
				((JTextField) child).removeActionListener(this.script_change_listener);
			} else if (child instanceof JCheckBox) {
				((JCheckBox) child).removeActionListener(this.script_change_listener);
			} else if (child instanceof JFileChooser) {
				((JFileChooser) child).removeActionListener(this.script_change_listener);
			} else if (child instanceof JComboBox) {
				((JComboBox<?>) child).removeItemListener(this.script_change_listener);
			} else if (child instanceof JRadioGroup) {
				((JRadioGroup<?>) child).removeActionListener(this.script_change_listener);
			} else if (child instanceof JCheckGroup) {
				((JCheckGroup<?>) child).removeActionListener(this.script_change_listener);
			} else if (child instanceof JListSelector) {
				((JListSelector<?>) child).removeActionListener(this.script_change_listener);
			} else if (child instanceof JUserList) {
				((JUserList) child).removeActionListener(this.script_change_listener);
			} else if (child instanceof JPanel) {
				this.removeListeners((JComponent) child);
			}
		}
	}

	private enum ParamType {
		hidden, text, spinner, slider, check, file, list, radio, checkgroup, selector, user_list,
		sequence, group
	}

	public enum ParametersType {
		Run, Config
	}

	@SuppressWarnings("unchecked")
	private JComponent getField(IScopeMap row, IScopeMap values) {
		JComponent field = null;
		ParamType ptype = ParamType.valueOf(row.get("type").toString());
		String name = row.get("name").toString();
		IScopeObject value = values.get(name);
		IScopeList vals = (IScopeList) row.get("values");
		if (vals == null) {
			vals = new JScopeList();
		}
		boolean disabled = false;
		if (row.get("disabled") != null) {
			disabled = ((JScopeBoolean) row.get("disabled")).value();
		}

		switch (ptype) {
		case text:
			field = new JTextField();
			if (value != null)
				((JTextField) field).setText(value.toString());
			((JTextField) field).addActionListener(this.script_change_listener);
			break;
		case hidden:
			field = new JTextField();
			if (value != null)
				((JTextField) field).setText(value.toString());
			field.setEnabled(false);
			field.setVisible(false);
			break;
		case spinner: {
			field = new JSpinner();

			int[] vv = new int[4];
			if (value == null) {
				vv[0] = 0;
			} else if (value instanceof IScopeNumber) {
				vv[0] = ((IScopeNumber) value).intValue();
			} else {
				try {
					vv[0] = Integer.parseInt(value.toString());
				} catch (NumberFormatException e) {
					vv[0] = 0;
				}
			}
			if (vals != null && vals.size() == 3) {
				for (int i = 0; i < 3; ++i) {
					if (vals.get(i) instanceof IScopeNumber) {
						vv[i + 1] = ((IScopeNumber) vals.get(i)).intValue();
					} else {
						vv[i + 1] = Integer.parseInt(vals.get(i).toString());
					}
				}

			} else {
				vv[1] = 0;
				vv[2] = 100;
				vv[3] = 1;
			}

			if (vv[0] < vv[1])
				vv[0] = vv[1];
			if (vv[0] > vv[2])
				vv[0] = vv[2];

			((JSpinner) field).setModel(new SpinnerNumberModel(vv[0], vv[1], vv[2], vv[3]));
			((JSpinner) field).addChangeListener(this.script_change_listener);
			break;
		}
		case slider: {
			field = new JSlider();

			int[] vv = new int[4];
			if (value == null) {
				vv[0] = 0;
			} else if (value instanceof IScopeNumber) {
				vv[0] = ((IScopeNumber) value).intValue();
			} else {
				try {
					vv[0] = Integer.parseInt(value.toString());
				} catch (NumberFormatException e) {
					vv[0] = 0;
				}
			}
			if (vals != null && vals.size() == 3) {
				for (int i = 0; i < 3; ++i) {
					if (vals.get(i) instanceof IScopeNumber) {
						vv[i + 1] = ((IScopeNumber) vals.get(i)).intValue();
					} else {
						vv[i + 1] = Integer.parseInt(vals.get(i).toString());
					}
				}

			} else {
				vv[1] = 1;
				vv[2] = 0;
				vv[3] = 100;
			}

			if (vv[0] < vv[2])
				vv[0] = vv[2];
			if (vv[0] > vv[3])
				vv[0] = vv[3];

			// ((JSlider) field).setModel(new DefaultBoundedRangeModel(vv[0], vv[1], vv[2], vv[3]));
			((JSlider) field).setValue(vv[0]);
			if (vv[1] != 1)
				((JSlider) field).setExtent(vv[1]);
			((JSlider) field).setMinimum(vv[2]);
			((JSlider) field).setMaximum(vv[3]);
			((JSlider) field).setMajorTickSpacing((vv[3] - vv[2]) / 8);
			((JSlider) field).setPaintTicks(true);
			((JSlider) field).setPaintTrack(true);
			((JSlider) field).setPaintLabels(true);
			((JSlider) field).setToolTipText(Integer.toString(vv[0]));
			((JSlider) field).addChangeListener(this.script_change_listener);
			break;
		}
		case check:
			field = new JCheckBox();
			if (value != null)
				((JCheckBox) field).setSelected(Boolean.parseBoolean(value.toString()));
			((JCheckBox) field).addActionListener(this.script_change_listener);
			break;

		case file:
			field = new JFileField();
			((JFileField) field).setBackground(this.getBackground());
			if (value != null)
				((JFileField) field).setText(value.toString());
			((JFileField) field).addSelectListener(this.script_change_listener);
			for (Object o : vals) {
				if (o instanceof Map<?, ?>) {
					Map<String, Object> m = (Map<String, Object>) o;
					((JFileField) field).addFileFilter(new FileNameExtensionFilter(m.get("name")
							.toString(), new String[] { m.get("extension").toString() }));
				}
			}
			break;
		case list:
			field = new JSeparatedComboBox<IClassifierItem>();

			((JSeparatedComboBox<IClassifierItem>) field).setList(ParametersPanel
					.convertListToClassifierItems(vals));

			((JSeparatedComboBox<IClassifierItem>) field).setSelectedItem(ParametersPanel
					.getClassifierItem(value));
			((JSeparatedComboBox<IClassifierItem>) field)
					.addItemListener(this.script_change_listener);
			break;

		case radio:
			field = new JRadioGroup<IClassifierItem>(
					ParametersPanel.convertListToClassifierItems(vals));
			((JRadioGroup<IClassifierItem>) field).setBackground(this.getBackground());
			((JRadioGroup<IClassifierItem>) field).setSelectedValue(ParametersPanel
					.getClassifierItem(value));
			((JRadioGroup<IClassifierItem>) field).addActionListener(this.script_change_listener);
			break;

		case checkgroup:
			field = new JCheckGroup<IClassifierItem>(
					ParametersPanel.convertListToClassifierItems(vals));
			((JCheckGroup<IClassifierItem>) field).setBackground(this.getBackground());
			((JCheckGroup<IClassifierItem>) field).setSelectedValues(ParametersPanel
					.parseList(value));
			((JCheckGroup<IClassifierItem>) field).addActionListener(this.script_change_listener);
			break;

		case selector:
			field = new JListSelector<IClassifierItem>();
			((JListSelector<IClassifierItem>) field).setBackground(this.getBackground());
			((JListSelector<IClassifierItem>) field).setSelectedValues(
					ParametersPanel.convertListToClassifierItems(vals),
					ParametersPanel.parseList(value));
			((JListSelector<IClassifierItem>) field).addActionListener(this.script_change_listener);
			break;
		case user_list:
			field = new JUserList();
			((JUserList) field).setBackground(this.getBackground());
			{
				List<String> list = (List<String>) ParametersPanel.parseList(value);
				((JUserList) field).setValues(list);
			}
			((JUserList) field).addActionListener(this.script_change_listener);
			break;
		case sequence:
			field = new JPanel();
			((JPanel) field).setBackground(this.getBackground());
			((JPanel) field).setLayout(new FlowLayout(FlowLayout.LEFT));
			{
				IScopeMap child_map = this.getScopeMap(values.get(name));
				for (IScopeObject v : vals) {
					IScopeMap child_row = (IScopeMap) v;
					JComponent child_field = this.getField(child_row, child_map);
					((JPanel) field).add(child_field);
				}
			}
			break;
		case group:
			field = new JPanel();
			((JPanel) field).setBorder(BorderFactory.createTitledBorder("" + row.get("label")));
			((JPanel) field).setBackground(this.getBackground());
			((JPanel) field).setLayout(new GridBagLayout());
			{
				IScopeMap child_map = this.getScopeMap(values.get(name));
				this.fillPanel((JPanel) field, vals, child_map);
			}
			break;

		default:
			field = new JLabel("error");
			break;

		}

		field.setEnabled(!disabled);
		field.setName(name);
		return field;
	}

	public void clearPanel() {
		this.removeListeners(this.values_panel);
		this.values_panel.removeAll();
		this.validate();
		this.repaint();
	}

	public void fillPanel(IScopeList config, IScopeMap values) {
		this.fillPanel(this.values_panel, config, values);
		this.validate();
		this.repaint();
	}

	private void fillPanel(JPanel pane, IScopeList config, IScopeMap values) {
		this.removeListeners(pane);

		pane.removeAll();

		GridBagConstraints c = new GridBagConstraints();

		JLabel label = null;
		JComponent field = null;

		c.anchor = GridBagConstraints.LINE_START;
		for (IScopeObject obj : config) {
			IScopeMap row = (IScopeMap) obj;
			try {
				label = new JLabel();
				label.setText("<html><p width='100px'>" + row.get("label") + "</p></html>");

				field = this.getField(row, values);

				if (!field.isEnabled()) {
					label.setForeground(Color.darkGray);
				}
				if (!field.isVisible()) {
					label.setVisible(false);
				}

				c.gridy++;
				c.gridx = 0;

				if (field.getBorder() instanceof TitledBorder) {
					c.gridwidth = 2;
					c.fill = GridBagConstraints.HORIZONTAL;
					c.weightx = 1;
					pane.add(field, c);
				} else {
					c.gridwidth = 1;
					c.fill = GridBagConstraints.NONE;
					c.weightx = 0;
					pane.add(label, c);
					c.gridx = 1;
					if (!(field instanceof JSpinner || field instanceof JComboBox<?>))
						c.fill = GridBagConstraints.HORIZONTAL;
					c.weightx = 1;
					pane.add(field, c);
				}

			} catch (Exception e) {
				e.printStackTrace();
				this.script.log(Level.SEVERE, e.toString());
			}

		}

		label = new JLabel();
		label.setText("<html><p width='100px'>" + "" + "</p></html>");

		c.gridy++;

		c.gridx = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1;
		c.weighty = 1;
		pane.add(label, c);

	}

	private static List<?> parseList(Object v) {
		if (v == null) {
			return Arrays.asList(new Object[] {});
		}
		if (v instanceof Object[]) {
			return Arrays.asList((Object[]) v);
		}
		if (v instanceof IScopeList) {
			return Arrays.asList(new JConverter().ScopeObjectArrayToScriptObjectArray(Collections
					.list(((IScopeList) v).enumeration()).toArray(new IScopeObject[] {})));
		}
		if (v instanceof String) {
			v = JSONValue.parse(v.toString());
		}
		if (v instanceof List<?>) {
			return (List<?>) v;
		}

		return Arrays.asList(new Object[] {});
	}

	// private static List<IClassifierItem> parseListAsClassifier(Object v) {
	// List<?> list = PixelBotView.parseList(v);
	// return convertListToClassifierItems(list);
	// }

	private static List<IClassifierItem> convertListToClassifierItems(IScopeList list) {
		List<IClassifierItem> result = new ArrayList<IClassifierItem>();
		for (IScopeObject o : list) {
			result.add(getClassifierItem(o));
		}
		return result;
	}

	private static IClassifierItem getClassifierItem(Object o) {
		if (o == null) {
			return new ScopeClassifierItem(0, "");
		} else if (o instanceof IClassifierItem) {
			return (IClassifierItem) o;
		} else if (o instanceof IScopeNative) {
			return getClassifierItem(((IScopeObject) o).getObject());
		} else if (o instanceof IScopeString) {
			return new ScopeClassifierItem(0, o.toString());
		} else if (o instanceof IScopeMap) {
			IScopeMap m = (IScopeMap) o;
			IScopeNumber code = (IScopeNumber) m.get("code");
			IScopeString text = (IScopeString) m.get("text");
			return new ScopeClassifierItem(code.intValue(), text.value());
		} else {
			return new ScopeClassifierItem(0, o.toString());
		}
	}

	private IScopeMap getScopeMap(IScopeObject obj) {
		if (obj == null) {
			return new JScopeMap();
		} else if (obj instanceof IScopeMap) {
			return (IScopeMap) obj;
		} else if (obj instanceof IScopeString) {
			IScopeObject r = JSONScriptValue.parseForScope(this.script, obj.toString());
			if (r != null && r instanceof IScopeMap) {
				return (IScopeMap) r;
			}

		}
		return new JScopeMap();
	}

	public IScopeMap getParams(ParametersType type) {
		return new JScopeMap(new JConverter(), this.getParams(type, this.values_panel));

	}

	private Map<String, Object> getParams(ParametersType type, JComponent component) {
		Map<String, Object> params = new HashMap<String, Object>();
		for (Component child : component.getComponents()) {
			if (type == ParametersType.Run && !child.isEnabled())
				continue;

			Object obj = null;
			if (child instanceof JTextField) {
				obj = ((JTextField) child).getText();
			} else if (child instanceof JCheckBox) {
				obj = ((JCheckBox) child).isSelected() ? Boolean.TRUE : Boolean.FALSE;
			} else if (child instanceof JSpinner) {
				obj = ((JSpinner) child).getValue();
			} else if (child instanceof JSlider) {
				obj = Integer.valueOf(((JSlider) child).getValue());
				((JSlider) child).setToolTipText(obj.toString());
			} else if (child instanceof JFileField) {
				obj = ((JFileField) child).getText();
			} else if (child instanceof JComboBox) {
				obj = ((JComboBox<?>) child).getSelectedItem() == null ? null
						: ((JComboBox<?>) child).getSelectedItem().toString();
			} else if (child instanceof JRadioGroup) {
				obj = ((JRadioGroup<?>) child).getSelectedValue();
			} else if (child instanceof JCheckGroup) {
				obj = ((JCheckGroup<?>) child).getSelectedValues();
			} else if (child instanceof JListSelector) {
				obj = ((JListSelector<?>) child).getSelectedValues();
			} else if (child instanceof JUserList) {
				obj = ((JUserList) child).getValues();
			} else if (child instanceof JPanel) {
				obj = this.getParams(type, (JComponent) child);
			} else {
				continue;
			}
			params.put(child.getName(), obj);
		}
		return params;
	}
}
