package pixelbot.components.generic;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.List;

public class JRadioGroup<T> extends JPanel {
	private static final long serialVersionUID = 352200071022346722L;

	public enum Layout {
		Vertical, Horizontal;
	}

	private ButtonGroup bg = new ButtonGroup();

	public JRadioGroup() {
		this.setLayout(Layout.Vertical);
	}

	public JRadioGroup(List<T> values) {
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
	public T getValue() {
		for (Component c : getComponents()) {
			if (((JRadioButton) c).isSelected()) {
				return (T) ((JRadioButton) c).getClientProperty("value");
			}
		}
		return null;
	}

	public String getSelectedValue() {
		T v = this.getValue();
		return v == null ? null : v.toString();
	}

	public void setValue(T value) {
		this.setSelectedValue(value);
	}

	public void setSelectedValue(Object value) {
		for (Component c : getComponents()) {
			if (((JRadioButton) c).getClientProperty("value").equals(value)) {
				((JRadioButton) c).setSelected(true);
			}
		}
	}

	public void setItems(List<T> values) {
		for (T v : values) {
			JRadioButton rg = new JRadioButton(v.toString());
			rg.putClientProperty("value", v);
			this.add(rg);

			this.bg.add(rg);

			rg.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}

		JRadioButton radio = (JRadioButton) getComponent(0);
		radio.setSelected(true);

		for (ActionListener listener : this.listenerList.getListeners(ActionListener.class)) {
			for (Component c : getComponents()) {
				((JRadioButton) c).addActionListener(listener);
			}
		}
		for (ItemListener listener : this.listenerList.getListeners(ItemListener.class)) {
			for (Component c : getComponents()) {
				((JRadioButton) c).addItemListener(listener);
			}
		}
	}

	public void setTexts(String[] texts) {
		for (int i = 0; i < this.getComponentCount() || i < texts.length; ++i) {
			((JRadioButton) this.getComponent(i)).setText(texts[i]);
		}
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(-1, -1);
	}

	public synchronized void addActionListener(ActionListener listener) {
		this.listenerList.add(ActionListener.class, listener);
		for (Component c : getComponents()) {
			((JRadioButton) c).addActionListener(listener);
		}

	}

	public synchronized void removeActionListener(ActionListener listener) {
		this.listenerList.remove(ActionListener.class, listener);
		for (Component c : getComponents()) {
			((JRadioButton) c).removeActionListener(listener);
		}
	}

	public synchronized void addItemListener(ItemListener listener) {
		this.listenerList.add(ItemListener.class, listener);
		for (Component c : getComponents()) {
			((JRadioButton) c).addItemListener(listener);
		}

	}

	public synchronized void removeItemListener(ItemListener listener) {
		this.listenerList.remove(ItemListener.class, listener);
		for (Component c : getComponents()) {
			((JRadioButton) c).removeItemListener(listener);
		}
	}

	public int getSelectedIndex() {
		int i = 0;
		for (Component c : getComponents()) {
			if (((JRadioButton) c).isSelected()) {
				return i;
			}
			++i;
		}
		return -1;
	}

	public void setSelectedIndex(int i) {
		this.bg.setSelected(((JRadioButton) this.getComponents()[i]).getModel(), true);
	}

}
