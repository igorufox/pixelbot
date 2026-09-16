package pixelbot.components.generic;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

public class JXTrayIcon extends TrayIcon {
	private JPopupMenu menu;
	protected static JDialog dialog;
	static {
		dialog = new JDialog((Frame) null);
		dialog.setUndecorated(true);
		dialog.setAlwaysOnTop(true);
	}

	private static PopupMenuListener popupListener = new PopupMenuListener() {
		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			dialog.setVisible(false);
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
			dialog.setVisible(false);
		}
	};

	public JXTrayIcon(Image image) {
		super(image);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				showJPopupMenu(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				showJPopupMenu(e);
			}
		});
	}

	protected void showJPopupMenu(MouseEvent e) {
		if (e.isPopupTrigger() && this.menu != null) {
			Dimension size = this.menu.getPreferredSize();
			showJPopupMenu(e.getX(), e.getY() - size.height);
		}
	}

	protected void showJPopupMenu(int x, int y) {
		dialog.setLocation(x, y);
		dialog.setVisible(true);
		this.menu.show(dialog.getContentPane(), 0, 0);
		// popup works only for focused windows
		dialog.toFront();
	}

	public JPopupMenu getJPopupMenu() {
		return this.menu;
	}

	public void setJPopupMenu(JPopupMenu menu) {
		if (this.menu != null) {
			this.menu.removePopupMenuListener(popupListener);
		}
		this.menu = menu;
		menu.addPopupMenuListener(popupListener);
	}

}
