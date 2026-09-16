package pixelbot.components.editor.misc;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.text.*;

import pixelbot.components.editor.ScriptEditor.ScriptTextPane;

public class JFindPanel extends JPanel {
	private static final long serialVersionUID = 572119361597174066L;
	// private static ResourceBundle resources = ResourceBundle
	// .getBundle("pixelbot.components.editor.resources.messages");
	private static final String resources_p = "pixelbot.components.editor.resources.messages";

	protected ScriptTextPane pane = null;
	protected JTextField txtFind;
	private JButton btnPrevious;
	private JButton btnNext;
	protected JLabel lblFind;
	private JCheckBox chckbxMatchCase;
	private JCheckBox chckbxRegularExptessions;

	public JFindPanel(ScriptTextPane pane) {
		this.pane = pane;
		this.setVisible(false);
		this.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));

		this.lblFind = new JLabel();
		this.lblFind.setIcon(new ImageIcon(this.getClass().getResource(
				"/pixelbot/resources/close-icon.png")));
		this.lblFind.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				Rectangle rect = new Rectangle();
				rect.height = JFindPanel.this.lblFind.getIcon().getIconHeight();
				rect.width = JFindPanel.this.lblFind.getIcon().getIconWidth();
				if (rect.contains(e.getX(), e.getY())) {
					JFindPanel.this.setVisible(false);
				}
			}
		});
		add(this.lblFind);

		this.txtFind = new JTextField();
		this.lblFind.setLabelFor(this.txtFind);
		add(this.txtFind);
		this.txtFind.setColumns(10);
		this.txtFind.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					find(true);
				}
			}
		});

		this.btnPrevious = new JButton();
		this.btnPrevious.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				find(false);
			}
		});
		add(this.btnPrevious);

		this.btnNext = new JButton();
		this.btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				find(true);
			}
		});
		add(this.btnNext);

		this.chckbxMatchCase = new JCheckBox();
		this.chckbxMatchCase.setSelected(true);
		add(this.chckbxMatchCase);

		this.chckbxRegularExptessions = new JCheckBox();
		this.chckbxRegularExptessions.setSelected(true);
		add(this.chckbxRegularExptessions);

//		this.translate();
	}

	public void showPanel() {
		this.setVisible(true);
		this.txtFind.requestFocus();
	}

	
	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);
		ResourceBundle messages = ResourceBundle.getBundle(resources_p, this.getLocale());

		this.lblFind.setText(messages.getString("find.lblFind"));
		this.btnPrevious.setText(messages.getString("find.btnPrevious"));
		this.btnNext.setText(messages.getString("find.btnNext"));
		this.chckbxMatchCase.setText(messages.getString("find.chckbxMatchCase"));
		this.chckbxRegularExptessions.setText(messages.getString("find.chckbxRegularExptessions"));
	}

	protected void find(boolean forward) {
		if (forward) {
			try {
				Segment s = new Segment();
				s.setPartialReturn(true);
				JFindPanel.this.pane.getDocument().getText(0,
						JFindPanel.this.pane.getDocument().getEndPosition().getOffset(), s);
				Matcher m = Pattern.compile(JFindPanel.this.txtFind.getText()).matcher(s);
				if (m.find(JFindPanel.this.pane.getCaretPosition())) {
					JFindPanel.this.pane.setCaretPosition(m.start());
					JFindPanel.this.pane.moveCaretPosition(m.end());
					JFindPanel.this.pane.requestFocus();
				}
			} catch (BadLocationException ex) {}
		} else {
			try {
				Segment s = new Segment();
				s.setPartialReturn(true);
				JFindPanel.this.pane.getDocument().getText(0,
						JFindPanel.this.pane.getDocument().getEndPosition().getOffset(), s);
				Matcher m = Pattern.compile(JFindPanel.this.txtFind.getText()).matcher(s);
				m.region(0, JFindPanel.this.pane.getCaretPosition() - 1);
				int start = -1;
				int end = -1;
				while (m.find()) {
					start = m.start();
					end = m.end();
				}
				if (start >= 0) {
					JFindPanel.this.pane.setCaretPosition(start);
					JFindPanel.this.pane.moveCaretPosition(end);
					JFindPanel.this.pane.requestFocus();
				}
			} catch (BadLocationException ex) {}

		}
	}

}
