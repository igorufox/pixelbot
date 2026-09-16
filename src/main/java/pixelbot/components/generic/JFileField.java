package pixelbot.components.generic;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import pixelbot.misc.Tools;

public class JFileField extends JPanel {
	private static final long serialVersionUID = -5919901048127977493L;

	public interface JFileFieldSelectListener extends ActionListener {}

	protected JTextField text = new JTextField();
	protected JButton button = new JButton();
	protected String title = null;
	protected int mode = JFileChooser.FILES_AND_DIRECTORIES;
	protected String directory = null;
	protected ArrayList<FileFilter> filters = new ArrayList<FileFilter>();
	protected ArrayList<JFileFieldSelectListener> selectListeners = new ArrayList<JFileFieldSelectListener>();

	public JFileField() {

		this.button.setText("...");

		Dimension dim = new Dimension(20, 20);
		this.button.setSize(dim);
		this.button.setPreferredSize(dim);
		this.button.setMaximumSize(dim);
		this.button.setMinimumSize(dim);

		this.text.setEditable(false);

		this.setLayout(new BorderLayout());

		this.add(this.text, BorderLayout.CENTER);
		this.add(this.button, BorderLayout.EAST);

		this.button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				String txt = JFileField.this.getText();

				JFileChooser jfc;
				if (JFileField.this.directory != null || !txt.equals("")) {
					if (txt.equals("")) {
						txt = JFileField.this.directory;
					}
					File f = new File(txt);
					if (!f.isDirectory()) {
						txt = f.getParent();
					}
					jfc = new JFileChooser(new File(txt));
				} else {
					jfc = new JFileChooser(Tools.workdir);
				}

				Iterator<?> it = JFileField.this.filters.iterator();
				while (it.hasNext()) {
					jfc.addChoosableFileFilter((FileFilter) it.next());
				}

				jfc.setDialogTitle(JFileField.this.title);
				jfc.setFileSelectionMode(JFileField.this.mode);

				jfc.showOpenDialog(JFileField.this);

				File f = jfc.getSelectedFile();
				if (f != null) {

					String last = f.getAbsolutePath();
					if (f.isFile()) {
						last = f.getParentFile().getAbsolutePath();
					}

					JFileField.this.directory = last;

					JFileField.this.text.setText(f.getAbsolutePath());

					ActionEvent ae = new ActionEvent(this, 0, f.getAbsolutePath());

					it = JFileField.this.selectListeners.iterator();
					while (it.hasNext()) {
						((JFileFieldSelectListener) it.next()).actionPerformed(ae);
					}
				}
			}
		});
	}

	/**
	 * @param value The text for the field.
	 */
	public void setText(final String value) {
		this.text.setText(value.trim());
	}

	/**
	 * @return The text of the text field.
	 */
	public String getText() {
		return this.text.getText().trim();
	}

	/**
	 * <Some description here>
	 * 
	 * @param value
	 * @see javax.swing.JComponent#setToolTipText(java.lang.String)
	 */
	@Override
	public void setToolTipText(final String value) {
		super.setToolTipText(value);

		this.text.setToolTipText(value);
		this.button.setToolTipText(value);
	}

	public void setTitle(String value) {
		this.title = value;

	}

	/**
	 * <Some description here>
	 * 
	 * @param value
	 * @see java.awt.Component#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(final boolean value) {
		super.setEnabled(value);

		this.text.setEnabled(value);
		this.button.setEnabled(value);
	}

	/**
	 * @param value The file selection mode.
	 */
	public void setFileSelectionMode(final int value) {
		this.mode = value;
	}

	/**
	 * @return The file selection mode.
	 */
	public int getFileSelectionMode() {
		return this.mode;
	}

	/**
	 * @param value The current working directory.
	 */
	public void setWorkingDirectory(final String value) {
		this.directory = value;
	}

	/**
	 * @return The current working directory.
	 */
	public String getWorkingDirectory() {
		return this.directory;
	}

	/**
	 * @param value FileFilter to use.
	 */
	public void addFileFilter(final FileFilter value) {
		if (!this.filters.contains(value)) {
			this.filters.add(value);
		}
	}

	/**
	 * @param value The filter to remove.
	 */
	public void removeFileFilter(final FileFilter value) {
		if (this.filters.contains(value)) {
			this.filters.remove(value);
		}
	}

	public void clearFileFilters() {
		this.filters.clear();
	}

	/**
	 * @param value A JFileFieldSelectListener to add.
	 */
	public void addSelectListener(final JFileFieldSelectListener value) {
		if (!this.selectListeners.contains(value)) {
			this.selectListeners.add(value);
		}
	}

	/**
	 * @param value A JFileFieldSelectListener to remove.
	 */
	public void removeSelectListener(final JFileFieldSelectListener value) {
		if (this.selectListeners.contains(value)) {
			this.selectListeners.remove(value);
		}
	}

}
