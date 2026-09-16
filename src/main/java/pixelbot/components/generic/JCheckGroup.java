package pixelbot.components.generic;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

public class JCheckGroup<T> extends JPanel {
	private static final long serialVersionUID = 352200071022346722L;

	public enum Layout {
		Vertical, Horizontal;
	}

	public JCheckGroup() {
		this.setLayout(Layout.Vertical);
	}

	public JCheckGroup(List<T> values) {
		this();
		this.setItems(values);
	}

	public void setLayout(Layout l) {
		switch (l) {
		case Vertical:
			this.setLayout(new VerticalLayout(0, VerticalLayout.LEFT));
			break;
		case Horizontal:
			this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			break;
		}

	}

	@SuppressWarnings("unchecked")
	public List<T> getValue() {
		List<T> result = new ArrayList<T>();
		for (int i = 0; i < this.getComponentCount(); ++i) {
			if (((JCheckBox) this.getComponent(i)).isSelected()) {
				result.add((T) ((JCheckBox) this.getComponent(i)).getClientProperty("value"));
			}
		}
		return result;
	}

	public List<String> getSelectedValues() {
		List<T> values = this.getValue();
		List<String> result = new ArrayList<String>();
		for (T value : values) {
			result.add(value == null ? null : value.toString());
		}
		return result;
	}

	public void setValue(List<T> value) {
		this.setSelectedValues(value);
	}

	public void setSelectedValues(List<?> value) {
		for (Component c : getComponents()) {
			JCheckBox cb = (JCheckBox) c;
			boolean selected = false;
			for (Object v : value) {
				if (cb.getClientProperty("value").equals(v)) {
					selected = true;
					break;
				}
			}
			cb.setSelected(selected);
		}
	}

	public void setItems(Collection<T> values) {
		for (T value : values) {
			JCheckBox cb = new JCheckBox(value.toString());
			cb.putClientProperty("value", value);
			cb.setEnabled(this.isEnabled());
			this.add(cb);

			cb.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		for (ActionListener listener : this.listenerList.getListeners(ActionListener.class)) {
			for (Component c : getComponents()) {
				((JCheckBox) c).addActionListener(listener);
			}
		}
	}

	public void setTexts(String[] texts) {
		for (int i = 0; i < this.getComponentCount() || i < texts.length; ++i) {
			((JCheckBox) this.getComponent(i)).setText(texts[i]);
		}
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(-1, -1);
	}

	public synchronized void addActionListener(ActionListener listener) {
		this.listenerList.add(ActionListener.class, listener);
		for (Component c : getComponents()) {
			((JCheckBox) c).addActionListener(listener);
		}

	}

	public synchronized void removeActionListener(ActionListener listener) {
		this.listenerList.remove(ActionListener.class, listener);
		for (Component c : getComponents()) {
			((JCheckBox) c).removeActionListener(listener);
		}
	}

	public synchronized void addItemListener(ItemListener listener) {
		this.listenerList.add(ItemListener.class, listener);
		for (Component c : getComponents()) {
			((JCheckBox) c).addItemListener(listener);
		}

	}

	public synchronized void removeItemListener(ItemListener listener) {
		this.listenerList.remove(ItemListener.class, listener);
		for (Component c : getComponents()) {
			((JCheckBox) c).removeItemListener(listener);
		}
	}

	public int getSelectedIndex() {
		int i = 0;
		for (Component c : getComponents()) {
			if (((JCheckBox) c).isSelected()) {
				return i;
			}
			++i;
		}
		return -1;
	}

	public void setSelectedIndex(int i) {
		((JCheckBox) this.getComponents()[i]).setSelected(true);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (Component c : getComponents()) {
			((JCheckBox) c).setEnabled(enabled);
		}
	}

}
