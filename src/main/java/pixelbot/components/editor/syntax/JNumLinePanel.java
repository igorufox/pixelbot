package pixelbot.components.editor.syntax;

import java.awt.*;
import java.awt.event.*;
import java.util.BitSet;

import javax.swing.*;
import javax.swing.text.*;

import pixelbot.components.editor.ScriptEditor;
import pixelbot.components.editor.ScriptEditor.MarkerEvent;

public class JNumLinePanel extends JPanel {
	private static final long serialVersionUID = -850723105907822562L;
	protected ScriptEditor editor;
	protected BitSet markers = new BitSet();

	public JNumLinePanel(ScriptEditor editor) {
		this.editor = editor;
		this.setMinimumSize(new Dimension(40, 30));
		this.setPreferredSize(new Dimension(40, 30));
		this.setMinimumSize(new Dimension(40, 30));
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() % 2 == 0) {
					int pos = JNumLinePanel.this.editor.getTextPane().viewToModel(
							new Point(0, e.getPoint().y
									+ JNumLinePanel.this.editor.getScrollPane().getViewport()
											.getViewPosition().y));

					Document doc = JNumLinePanel.this.editor.getTextPane().getDocument();
					int line = doc.getDefaultRootElement().getElementIndex(pos) + 1;

					JNumLinePanel.this.markers.flip(line);
					JNumLinePanel.this.editor.fireMarkerChange(new MarkerEvent(line,
							JNumLinePanel.this.markers.get(line)));
					JNumLinePanel.this.repaint();
				}
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int start = this.editor.getTextPane().viewToModel(
				this.editor.getScrollPane().getViewport().getViewPosition());
		int end = this.editor.getTextPane().viewToModel(
				new Point(0, this.editor.getScrollPane().getViewport().getViewPosition().y
						+ this.editor.getTextPane().getHeight()));
		Document doc = this.editor.getTextPane().getDocument();
		int startline = doc.getDefaultRootElement().getElementIndex(start);
		int endline = doc.getDefaultRootElement().getElementIndex(end);

		for (int line = startline + 1; line <= endline; ++line) {
			try {
				Rectangle r = this.editor.getTextPane().modelToView(
						doc.getDefaultRootElement().getElement(line).getStartOffset());
				g.drawString(Integer.toString(line), 2, r.y
						- this.editor.getScrollPane().getViewport().getViewPosition().y);
				if (this.markers.get(line)) {
					ImageIcon image = new ImageIcon(this.getClass().getResource(
							"/pixelbot/components/editor/resources/brkp_obj.png"));
					g.drawImage(image.getImage(), 25, r.y
							- this.editor.getScrollPane().getViewport().getViewPosition().y - 12,
							null);
				}
			} catch (Exception e) {}
		}
	}

	public void setMarker(int line) {
		JNumLinePanel.this.markers.set(line);
		JNumLinePanel.this.editor.fireMarkerChange(new MarkerEvent(line, true));
		JNumLinePanel.this.repaint();
	}
}
