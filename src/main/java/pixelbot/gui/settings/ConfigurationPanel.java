package pixelbot.gui.settings;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileNameExtensionFilter;

import pixelbot.components.generic.JFileField;
import pixelbot.gui.*;
import pixelbot.misc.Tools;

public class ConfigurationPanel extends JPanel {
	private static final long serialVersionUID = -1276473431907885315L;

	protected JFileField jFileField_configuration_file;
	private JButton jButton_configuration_save;
	private JButton jButton_configuration_load;
	protected JjsonTree jjsonTree_configuration;

	public ConfigurationPanel() {
		super();
		this.jFileField_configuration_file = new JFileField();
		this.jButton_configuration_load = new JButton();
		this.jButton_configuration_save = new JButton();
		this.jjsonTree_configuration = new JjsonTree();

		GroupLayout gl = new GroupLayout(this);
		this.setLayout(gl);
		gl.setHorizontalGroup(gl
				.createParallelGroup()
				.addGroup(
						gl.createSequentialGroup().addComponent(this.jFileField_configuration_file)
								.addComponent(this.jButton_configuration_load)
								.addComponent(this.jButton_configuration_save))
				.addComponent(this.jjsonTree_configuration));
		gl.setVerticalGroup(gl
				.createSequentialGroup()
				.addGroup(
						gl.createParallelGroup()
								.addComponent(this.jFileField_configuration_file,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(this.jButton_configuration_load)
								.addComponent(this.jButton_configuration_save))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(this.jjsonTree_configuration, GroupLayout.DEFAULT_SIZE, 434,
						Short.MAX_VALUE));

		this.jFileField_configuration_file.setWorkingDirectory("config");
		this.jButton_configuration_load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				ConfigurationPanel.this.jjsonTree_configuration
						.loadFile(ConfigurationPanel.this.jFileField_configuration_file.getText());
			}
		});

		this.jButton_configuration_save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigurationPanel.this.jjsonTree_configuration
						.saveFile(ConfigurationPanel.this.jFileField_configuration_file.getText());
			}
		});
	}

	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);

		ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, l);

		this.jFileField_configuration_file.clearFileFilters();
		this.jFileField_configuration_file.addFileFilter(new FileNameExtensionFilter(messages
				.getString("tuning.configuration.dialog.filter"), new String[] { "cfg" }));
		this.jFileField_configuration_file.setTitle(messages
				.getString("tuning.configuration.dialog.load_title"));

		this.jButton_configuration_load.setText(messages
				.getString("tuning.configuration.buttons.load"));
		this.jButton_configuration_save.setText(messages
				.getString("tuning.configuration.buttons.save"));
	}

}
