package pixelbot.components;

import java.awt.*;
import java.io.File;

import javax.swing.*;

import pixelbot.misc.Tools;

public class Splash {
	protected Window dialog = null;
	private JLabel label_i;
	private JPanel panel;
	private ImageIcon ii;
	private JProgressBar pb;
	private int pp = 0;

	public Splash() {}

	public void show(int total) {
		if (this.dialog != null) {
			this.hide();
		}
		this.dialog = new JWindow();
		this.dialog.setAlwaysOnTop(true);

		try {
			this.dialog.setBackground(new Color(0, 0, 0, 0));
		} catch (Exception e) {}

		this.dialog.setBounds(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

		this.label_i = new JLabel();

		this.ii = new ImageIcon(new File(Tools.workdir, "media/splash.gif").getAbsolutePath());
		this.label_i.setIcon(this.ii);

		this.panel = new JPanel();
		this.panel.setBackground(new Color(0, 0, 0, 0));

		this.pb = new JProgressBar();
		this.pb.setMaximum(total);
		this.pb.setStringPainted(true);

		GroupLayout gl = new GroupLayout(this.panel);
		this.panel.setLayout(gl);
		gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(this.label_i).addComponent(this.pb));

		// , GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE

		gl.setVerticalGroup(gl.createSequentialGroup().addComponent(this.label_i)
				.addComponent(this.pb));
		// gl.linkSize(SwingConstants.HORIZONTAL, label_i, label_t);

		this.dialog.setLayout(new GridBagLayout());
		this.dialog.add(this.panel);
		this.dialog.setVisible(true);
	}

	public void process(String text) {
		this.pb.setString(text);
		this.pb.setValue(++this.pp);
	}

	public void hide() {
		if (this.dialog == null)
			return;
		this.dialog.setVisible(false);
		this.dialog.dispose();
		this.dialog = null;
	}
}
