package pixelbot.components.editor.hint;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import pixelbot.components.editor.ScriptEditor.ScriptTextPane;
import pixelbot.components.editor.hint.event.*;
import pixelbot.components.editor.hint.event.HintEvent.Status;

public class ScriptHint extends JScrollPane implements KeyListener, MouseListener {
	private static final long serialVersionUID = 1972550525399557185L;

	private JList<?> hintList;
	private int offset;
	private ScriptTextPane pane;

	public ScriptHint(ScriptTextPane pane) {
		try {
			this.pane = pane;
			this.setSize(200, 120);
			this.setOffset(0);
			this.hintList = new JList<String>(new HintListModel());
			this.hintList.setFocusable(false);
			this.hintList.setFont(new Font("Dialog", Font.PLAIN, 12));
			this.setViewportView(this.hintList);

			this.hintList.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() >= 2) {
						ScriptHint.this.setValue();
					}
				}
			});
			this.setVisible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setValue() {
		String value = this.hintList.getSelectedValue().toString();
		try {
			this.pane.removeString(this.getOffset(),
					this.pane.getCaretPosition() - this.getOffset());
			this.pane.insertString(this.getOffset(), value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.hideJsHint();
	}

	public void showJsHint(char next) {
		try {
			int x = this.pane.getCaretPosition();
			HintEvent he = new HintEvent();
			Singleton sgt = Singleton.init(this.pane, next);

			if (this.pane.getScriptManager().fillHintModel(sgt,
					(HintListModel) this.hintList.getModel())) {

				Dimension pref_size = this.hintList.getPreferredSize();
				pref_size.width = pref_size.width + 25;
				pref_size.height = 120;

				this.setSize(pref_size);

				Rectangle caretCoords = this.pane.modelToView(x);

				int y = this.pane.getEditor().getHeight() < caretCoords.y + 135
						? caretCoords.y - 120 : caretCoords.y + 15;
				this.setLocation(caretCoords.x, y);

				this.setVisible(true);
				this.setOffset(x - sgt.getMember().length() + (next == 0 ? 0 : 1));

				this.hintList.setSelectedIndex(0);
				this.hintList.scrollRectToVisible(this.hintList.getCellBounds(0, 0));

				he.setStatus(Status.Success);
			} else {
				he.setStatus(Status.Fail);
			}
			this.pane.getEditor().fireHintInitializationEvent(he);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void hideJsHint() {
		this.setVisible(false);
		this.setOffset(0);
		this.pane.requestFocus();
	}

	public void shiftSelection(boolean up) {
		int pos = this.hintList.getSelectedIndex();
		if (up)
			++pos;
		else
			--pos;
		if (pos >= 0 && pos < this.hintList.getModel().getSize()) {
			this.hintList.setSelectedIndex(pos);
			this.hintList.scrollRectToVisible(this.hintList.getCellBounds(pos, pos));
		}
	}

	private void setOffset(int offset) {
		this.offset = offset;
	}

	private int getOffset() {
		return this.offset;
	}

	@Override
	public void keyPressed(KeyEvent e) {

		switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER: // Enter
			if (this.isVisible() && !e.isShiftDown()) {
				this.setValue();
				e.consume();
			}
			this.hideJsHint();
			break;
		case KeyEvent.VK_ESCAPE: // Escape
			this.hideJsHint();
			break;
		case KeyEvent.VK_LEFT:// <--
			if (this.isVisible()) {
				this.showJsHint('\0');
			}
			break;
		case KeyEvent.VK_RIGHT:// -->
			if (this.isVisible()) {
				this.showJsHint('\0');
			}
			break;
		case KeyEvent.VK_UP:// --V
			if (this.isVisible()) {
				this.shiftSelection(false);
				e.consume();
			}
			break;
		case KeyEvent.VK_DOWN:// --Λ
			if (this.isVisible()) {
				this.shiftSelection(true);
				e.consume();
			}
			break;
		case KeyEvent.VK_BACK_SPACE:// backspace
			if (this.isVisible()) {
				this.showJsHint('\0');
			}
			break;
		case KeyEvent.VK_PERIOD: // .
			this.showJsHint('.');
			break;
		case KeyEvent.VK_SPACE:
			if (e.isControlDown())
				this.showJsHint('\0');
			break;
		}
		if (this.isVisible()) {
			char ch = e.getKeyChar();
			if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9')
					|| ch == '_' || ch == '$') {
				this.showJsHint(ch);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (this.isVisible()) {
			this.hideJsHint();
		}
	}

	@Override
	public void mousePressed(MouseEvent paramMouseEvent) {}

	@Override
	public void mouseReleased(MouseEvent paramMouseEvent) {}

	@Override
	public void mouseEntered(MouseEvent paramMouseEvent) {}

	@Override
	public void mouseExited(MouseEvent paramMouseEvent) {}

	@Override
	public void keyTyped(KeyEvent paramKeyEvent) {}

	@Override
	public void keyReleased(KeyEvent paramKeyEvent) {}

}
