package pixelbot.components.generic;

import java.awt.Component;

import javax.swing.*;
import javax.swing.plaf.ListUI;
import javax.swing.plaf.synth.SynthListUI;

public class SeparatorSynthListUIFactory {

	public static ListUI getUI() {
		return new SeparatorSynthListUI();
	}

	private static class SeparatorSynthListUI extends SynthListUI {

		public SeparatorSynthListUI() {}

		/**
		 * Selected the previous row and force it to be visible.
		 * 
		 * @see JList#ensureIndexIsVisible
		 */
		@Override
		protected void selectPreviousIndex() {
			int s = this.list.getSelectedIndex();
			if (s > 0) {
				if (this.list.getModel().getElementAt(s - 1) instanceof JSeparator)
					s--;
				s -= 1;
				this.list.setSelectedIndex(s);
				this.list.ensureIndexIsVisible(s);
			}
		}

		/**
		 * Selected the previous row and force it to be visible.
		 * 
		 * @see JList#ensureIndexIsVisible
		 */
		@Override
		protected void selectNextIndex() {
			int s = this.list.getSelectedIndex();
			if ((s + 1) < this.list.getModel().getSize()) {
				if (this.list.getModel().getElementAt(s + 1) instanceof JSeparator)
					s++;
				s += 1;
				this.list.setSelectedIndex(s);
				this.list.ensureIndexIsVisible(s);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void installDefaults() {
			super.installDefaults();
			this.list.setCellRenderer(new SeparatedListCellRenderer(this.list.getCellRenderer()));
		}

		@SuppressWarnings("rawtypes")
		private class SeparatedListCellRenderer implements ListCellRenderer {
			private ListCellRenderer other;

			SeparatedListCellRenderer(ListCellRenderer<?> other) {
				this.other = other;

			}

			@SuppressWarnings("unchecked")
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				if (value instanceof JSeparator) {
					return (Component) value;
				}
				return this.other.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
			}

		}
	}
}