package pixelbot.components.generic;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.*;

public class JCustomDialog {
	public enum DialogResult {
		Button1, Button2, Button3, Button4, Cancel
	}

	public static class DialogDescription {
		public enum DialogType {
			Info, Question, Warning, Error
		}

		public String[] options;
		public String text;
		public String title;
		public DialogType type = DialogType.Question;

		protected int getType() {
			switch (this.type) {
			case Info:
				return JOptionPane.INFORMATION_MESSAGE;
			case Question:
				return JOptionPane.QUESTION_MESSAGE;
			case Warning:
				return JOptionPane.WARNING_MESSAGE;
			case Error:
				return JOptionPane.ERROR_MESSAGE;
			default:
				return JOptionPane.PLAIN_MESSAGE;
			}
		}
	}

	public static DialogResult showDialog(Component owner, DialogDescription dd) {
		JTextArea textArea = new JTextArea(dd.text);
		textArea.setColumns(30);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setSize(textArea.getPreferredSize().width, textArea.getPreferredSize().height);
		textArea.setEditable(false);
		textArea.setBorder(null);

		JOptionPane pane = new JOptionPane(textArea, dd.getType(), JOptionPane.DEFAULT_OPTION);

		pane.setOptions(dd.options);
		pane.setInitialValue(dd.options[0]);

		Color bg = new Color(((ColorUIResource) UIManager.get("nimbusBase")).getRGB());
		JCustomDialog.setBackground(pane, bg);
		JDialog dialog = pane.createDialog(owner, dd.title);
		for (Component c : dialog.getComponents()) {
			c.setBackground(bg);
		}
		dialog.setVisible(true);

		dialog.dispose();
		if (pane.getValue() == null || pane.getValue().equals(Integer.valueOf(-1))) {
			return DialogResult.Cancel;
		} else if (dd.options[0].equals(pane.getValue())) {
			return DialogResult.Button1;
		} else if (dd.options[1].equals(pane.getValue())) {
			return DialogResult.Button2;
		} else if (dd.options[2].equals(pane.getValue())) {
			return DialogResult.Button3;
		} else if (dd.options[3].equals(pane.getValue())) {
			return DialogResult.Button4;
		} else {
			return DialogResult.Cancel;
		}

	}

	private static void setBackground(Component comp, Color color) {
		comp.setBackground(color);
		if (comp instanceof Container)
			for (Component child : ((Container) comp).getComponents()) {
				JCustomDialog.setBackground(child, color);
			}
	}
}
