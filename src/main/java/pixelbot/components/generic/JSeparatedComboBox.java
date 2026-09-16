package pixelbot.components.generic;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.plaf.synth.SynthComboBoxUI;

import pixelbot.misc.IClassifierItem;

public class JSeparatedComboBox<T extends IClassifierItem> extends JComboBox<T> {
	private static final long serialVersionUID = 2710902785246786776L;
	protected final static JSeparator SEPARATOR = new JSeparator();

	public JSeparatedComboBox() {
		super();
		this.setUI(new SeparatorComboBoxUI());
		this.addActionListener(new BlockComboListener());
	}

	@SuppressWarnings("unchecked")
	public void setList(List<T> items) {
		Vector<Object> result = new Vector<Object>();
		if (items.size() != 0) {
			int order = items.get(0).getOrder();
			for (IClassifierItem item : items) {
				if (order != item.getOrder()) {
					order = item.getOrder();
					result.add(SEPARATOR);
				}
				result.add(item);
			}
		}
		super.setModel(new DefaultComboBoxModel<T>((Vector<T>) (Vector<?>) result));

	}

	@Override
	@Deprecated
	public void setModel(ComboBoxModel<T> aModel) {
		super.setModel(aModel);
	}

	private class BlockComboListener implements ActionListener {
		private Object currentItem;

		BlockComboListener() {
			this.currentItem = JSeparatedComboBox.this.getSelectedItem();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object tempItem = JSeparatedComboBox.this.getSelectedItem();
			if (JSeparatedComboBox.SEPARATOR.equals(tempItem)) {
				JSeparatedComboBox.this.setSelectedItem(this.currentItem);
			} else {
				this.currentItem = tempItem;
			}
		}
	}

	private class SeparatorComboBoxUI extends SynthComboBoxUI {
		public SeparatorComboBoxUI() {}

		@Override
		protected void selectNextPossibleValue() {
			int i = this.comboBox.getSelectedIndex();
			if (i < this.comboBox.getModel().getSize() - 1) {
				if (JSeparatedComboBox.SEPARATOR.equals(this.comboBox.getItemAt(i + 1)))
					i++;
				this.comboBox.setSelectedIndex(i + 1);
				this.comboBox.repaint();
			}
		}

		@Override
		protected void selectPreviousPossibleValue() {
			int i = this.comboBox.getSelectedIndex();
			if (i > 0) {
				if (JSeparatedComboBox.SEPARATOR.equals(this.comboBox.getItemAt(i - 1)))
					i--;
				this.comboBox.setSelectedIndex(i - 1);
				this.comboBox.repaint();
			}
		}

		@Override
		protected ListCellRenderer<Object> createRenderer() {
			return new SeparatedListCellRenderer(super.createRenderer());
		}

		private class SeparatedListCellRenderer implements ListCellRenderer<Object> {
			private ListCellRenderer<Object> other;

			SeparatedListCellRenderer(ListCellRenderer<Object> other) {
				this.other = other;

			}

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				if (value instanceof JSeparator) {
					return (Component) value;
				}
				return this.other.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
			}

		}
	}
}