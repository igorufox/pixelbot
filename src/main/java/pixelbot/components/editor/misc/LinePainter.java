package pixelbot.components.editor.misc;

import java.awt.*;

import javax.swing.plaf.TextUI;
import javax.swing.text.*;
import javax.swing.text.Highlighter.HighlightPainter;

public class LinePainter implements HighlightPainter {
	private Color color;
	private boolean visible = true;

	public LinePainter() {}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return this.color;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void paint(Graphics g, int p0, int p1, Shape shape, JTextComponent text) {
		try {
			if (!this.visible)
				return;
			Color c = this.getColor();
			if (c != null) {

				TextUI ui = text.getUI();

				Document doc = text.getDocument();
				Element element = doc.getDefaultRootElement();

				Element line = element.getElement(p0);
				int start = line.getStartOffset();

				line = element.getElement(p1);
				int end = line.getEndOffset() - 1;

				Rectangle start_r = ui.modelToView(text, start);
				Rectangle end_r = ui.modelToView(text, end);
				Rectangle total = start_r.union(end_r);

				g.setColor(this.getColor());
				g.fillRect(0, total.y, text.getWidth(), total.height);

			}
		} catch (BadLocationException e) {}
	}

}