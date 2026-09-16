package pixelbot.gui.settings;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.ColorUIResource;

import pixelbot.components.generic.*;
import pixelbot.components.generic.JCustomDialog.DialogDescription;
import pixelbot.components.generic.JCustomDialog.DialogResult;
import pixelbot.components.generic.JListSelector.Action;
import pixelbot.components.generic.JListSelector.DublicateHandler;
import pixelbot.components.generic.JListSelector.SelectionEvent;
import pixelbot.components.generic.JListSelector.SelectionListener;
import pixelbot.components.generic.JListSelector.Source;
import pixelbot.elements.*;
import pixelbot.misc.Tools;
import pixelbot.script.general.ScriptManager;

public class ImportDialog extends JDialog {
	private static final long serialVersionUID = 4770576535260223905L;
	protected static ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES);

	protected Elements elems_my;
	protected Elements elems_oth;
	protected JListSelector<String> list;

	public ImportDialog(final ScriptManager script) {
		super();
		this.setSize(600, 400);
		this.setMinimumSize(new Dimension(300, 300));
		this.setModal(true);
		this.elems_my = (Elements) script.getHelper().elements.clone();
		this.elems_oth = new Elements();

		JLabel lblInternalObjects = new JLabel(
				messages.getString("tuning.elements.import.internal_objects"));
		JLabel lblExternalObjects = new JLabel(
				messages.getString("tuning.elements.import.external_objects"));

		final JPicture pic_l = new JPicture();
		final JPicture pic_r = new JPicture();

		this.list = new JListSelector<String>();
		this.list.setRight(this.getList(this.elems_my));

		this.list.addSelectionListener(new SelectionListener() {
			@Override
			public void valueChanged(SelectionEvent event) {
				switch (event.getSource()) {
				case Left:
					if (event.getData() == null) {
						pic_l.setIcon(null);
					} else {
						pic_l.setIcon(new ImageIcon(ImportDialog.this.elems_oth
								.get(event.getData()).getImage()));
					}
					break;
				case Right:
					if (event.getData() == null) {
						pic_r.setIcon(null);
					} else {
						pic_r.setIcon(new ImageIcon(ImportDialog.this.elems_my.get(event.getData())
								.getImage()));
					}
					break;
				}
			}
		});

		this.list.setDublicateHandler(new DublicateHandler() {

			private boolean inBatch = false;
			private Result prevAnswer = null;

			@Override
			public void startBatch() {
				this.inBatch = true;
				this.prevAnswer = null;
			}

			@Override
			public void endBatch() {
				this.inBatch = false;
				this.prevAnswer = null;
			}

			@Override
			public Result onDublicate(Source source, Object o) {
				Result result = Result.Cancel;
				DialogDescription dd = new DialogDescription();
				dd.title = messages.getString("tuning.elements.import.dublicate.title");
				dd.text = String.format(
						messages.getString("tuning.elements.import.dublicate.text"), o);
				if (this.inBatch) {
					dd.options = new String[] {
							messages.getString("tuning.elements.import.dublicate.yes_all"),
							messages.getString("tuning.elements.import.dublicate.yes"),
							messages.getString("tuning.elements.import.dublicate.no"),
							messages.getString("tuning.elements.import.dublicate.no_all"),
							messages.getString("tuning.elements.import.dublicate.cancel") };
					if (this.prevAnswer != null) {
						result = this.prevAnswer;
					} else {
						DialogResult r = JCustomDialog.showDialog(ImportDialog.this, dd);
						switch (r) {
						case Button1:
							result = Result.Replace;
							this.prevAnswer = result;
							break;
						case Button2:
							result = Result.Replace;
							break;
						case Button3:
							result = Result.Cancel;
							break;
						case Button4:
							result = Result.Cancel;
							this.prevAnswer = result;
							break;
						case Cancel:
							result = Result.Stop;
							this.prevAnswer = result;
							break;
						}
					}
				} else {
					dd.options = new String[] {
							messages.getString("tuning.elements.import.dublicate.yes"),
							messages.getString("tuning.elements.import.dublicate.no") };

					DialogResult r = JCustomDialog.showDialog(ImportDialog.this, dd);
					switch (r) {
					case Button1:
						result = Result.Replace;
						break;
					case Button2:
					default:
						result = Result.Cancel;
						break;
					}
				}
				return result;
			}
		});

		this.list.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch ((Source) e.getSource()) {
				case Left:
					switch (Action.values()[e.getModifiers()]) {
					case Add:
						ImportDialog.this.elems_oth.put(e.getActionCommand(),
								ImportDialog.this.elems_my.get(e.getActionCommand()));
						break;
					case Remove:
						ImportDialog.this.elems_oth.remove(e.getActionCommand());
						break;
					default:
						break;
					}
					break;
				case Right:
					switch (Action.values()[e.getModifiers()]) {
					case Add:
						ImportDialog.this.elems_my.put(e.getActionCommand(),
								ImportDialog.this.elems_oth.get(e.getActionCommand()));
						break;
					case Remove:
						ImportDialog.this.elems_my.remove(e.getActionCommand());
						break;
					default:
						break;
					}
					break;
				}
			}
		});

		JButton btnLoad = new JButton(messages.getString("tuning.elements.import.buttons.load"));
		JButton btnSave = new JButton(messages.getString("tuning.elements.import.buttons.save"));
		JButton btnApply = new JButton(messages.getString("tuning.elements.import.buttons.apply"));
		JButton btnCancel = new JButton(messages.getString("tuning.elements.import.buttons.cancel"));

		btnLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(Tools.workdir);
				chooser.setDialogTitle(ImportDialog.messages
						.getString("tuning.elements.import.file_choose.title"));

				chooser.setAcceptAllFileFilterUsed(true);
				chooser.setMultiSelectionEnabled(false);
				chooser.addChoosableFileFilter(new FileNameExtensionFilter(ImportDialog.messages
						.getString("tuning.elements.import.file_choose.type_elz"), "elz"));
				chooser.addChoosableFileFilter(new FileNameExtensionFilter(ImportDialog.messages
						.getString("tuning.elements.import.file_choose.type_ser"), "ser"));
				chooser.addChoosableFileFilter(new FileNameExtensionFilter(ImportDialog.messages
						.getString("tuning.elements.import.file_choose.type_all"), "ser", "elz"));
				if (chooser.showOpenDialog(ImportDialog.this) == 0) {
					ImportDialog.this.elems_oth = ElemTools.loadElements(chooser.getSelectedFile(), false);
					ImportDialog.this.list.setLeft(ImportDialog.this
							.getList(ImportDialog.this.elems_oth));

				}
			}
		});

		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(Tools.workdir);
				chooser.setDialogTitle(ImportDialog.messages
						.getString("tuning.elements.import.file_choose.title"));

				chooser.setAcceptAllFileFilterUsed(true);
				chooser.setMultiSelectionEnabled(false);
				chooser.addChoosableFileFilter(new FileNameExtensionFilter(ImportDialog.messages
						.getString("tuning.elements.import.file_choose.type_elz"), "elz"));
				if (chooser.showSaveDialog(ImportDialog.this) == 0) {
					ElemTools.saveElements(ImportDialog.this.elems_oth, chooser.getSelectedFile(),
							true);
				}
			}
		});

		btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				script.getHelper().elements = ImportDialog.this.elems_my;
				setVisible(false);
			}
		});

		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
						groupLayout
								.createParallelGroup(Alignment.LEADING)
								.addGroup(
										Alignment.TRAILING,
										groupLayout
												.createSequentialGroup()
												.addComponent(lblExternalObjects, 0, 0,
														Short.MAX_VALUE)
												.addGap(60)
												.addComponent(lblInternalObjects, 0, 0,
														Short.MAX_VALUE))
								.addComponent(this.list, Alignment.TRAILING, 0, 0, Short.MAX_VALUE)
								.addGroup(
										Alignment.TRAILING,
										groupLayout.createSequentialGroup()
												.addComponent(pic_l, 0, 0, Short.MAX_VALUE)
												.addGap(60)
												.addComponent(pic_r, 0, 0, Short.MAX_VALUE))
								.addGroup(
										Alignment.TRAILING,
										groupLayout
												.createSequentialGroup()
												.addComponent(btnLoad)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(btnSave)
												.addPreferredGap(ComponentPlacement.RELATED, 0,
														Short.MAX_VALUE).addComponent(btnApply)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(btnCancel))).addContainerGap());
		groupLayout
				.setVerticalGroup(groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblExternalObjects)
										.addComponent(lblInternalObjects))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(this.list)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(pic_l, 0, 0, Short.MAX_VALUE)
										.addComponent(pic_r, 0, 0, Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(btnCancel).addComponent(btnApply)
										.addComponent(btnLoad).addComponent(btnSave))
						.addContainerGap());
		getContentPane().setLayout(groupLayout);

		Color bg = new Color(((ColorUIResource) UIManager.get("nimbusBase")).getRGB());
		this.getContentPane().setBackground(bg);
		pic_l.setBackground(bg);
		pic_r.setBackground(bg);
		this.list.setBackground(bg);
		btnLoad.setBackground(bg);
		btnSave.setBackground(bg);
		btnApply.setBackground(bg);
		btnCancel.setBackground(bg);

	}

	protected List<String> getList(Elements elems) {
		List<String> result = new ArrayList<String>();
		result.addAll(elems.keySet());

		return result;
	}

	public static void translate(ResourceBundle value) {
		ImportDialog.messages = value;
	}
}
