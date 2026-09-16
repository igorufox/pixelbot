package pixelbot.components.logviewer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import pixelbot.misc.Tools;

public class JLogViewer extends JPanel {
	private static final long serialVersionUID = 1L;

	public void logFileParseError(String message) {
		JOptionPane.showMessageDialog(this, message, "Parse error", JOptionPane.ERROR_MESSAGE);

	}

	protected FileFilter xmlLogFileFilter = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".xml") || f.getName().endsWith(".log");
		}

		@Override
		public String getDescription() {
			return "*.xml, *.log";
		}
	};

	/**
	 * This method initializes toolBar
	 * 
	 * @return JToolBar
	 */
	private JToolBar getToolBar() {
		if (this.toolBar == null) {
			this.fileName = new JLabel();
			this.fileName.setFont(new Font("Dialog", Font.BOLD, 12));
			this.fileNameLabel = new JLabel();
			this.fileNameLabel.setText("Log File:");
			this.fileNameLabel.setFont(new Font("Default", Font.PLAIN, 12));

			this.toolBar = new JToolBar();
			this.toolBar.setFloatable(false);
			this.toolBar.add(getOpenButton());
			this.toolBar.add(new JToolBar.Separator());
			this.toolBar.add(getAutorefreshToggleButton());
			this.toolBar.add(this.fileNameLabel);
			this.toolBar.add(this.fileName);
		}
		return this.toolBar;
	}

	/**
	 * This method initializes autorefreshToggleButton
	 * 
	 * @return JToggleButton
	 */
	private JToggleButton getAutorefreshToggleButton() {
		if (this.autorefreshToggleButton == null) {
			this.autorefreshToggleButton = new JToggleButton();
			this.autorefreshToggleButton.setIcon(new ImageIcon(getClass().getResource(
					"/pixelbot/components/logviewer/resources/execrefresh_16.png")));
			this.autorefreshToggleButton.setToolTipText("Autorefresh");
			this.autorefreshToggleButton.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED)

						((FileModel) JLogViewer.this.getLogTable().getModel())
								.setRefreshEnabled(true);
					else
						((FileModel) JLogViewer.this.getLogTable().getModel())
								.setRefreshEnabled(false);
				}
			});
		}
		return this.autorefreshToggleButton;
	}

	/**
	 * This method initializes openButton
	 * 
	 * @return JButton
	 */
	private JButton getOpenButton() {
		if (this.openButton == null) {
			this.openButton = new JButton();

			this.openButton.setIcon(new ImageIcon(this.getClass().getResource(
					"/pixelbot/components/logviewer/resources/open_16.png")));
			this.openButton.setToolTipText("Open log file");
			final JLogViewer logViewer = this;
			this.openButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					logViewer.chooseFile();
				}
			});
		}
		return this.openButton;
	}

	private JLabel fileName = null;
	private JLabel fileNameLabel = null;
	private JLogArea logTable = null;
	private JScrollPane tableScrollPane = null;
	private JToolBar toolBar = null;
	private JToggleButton autorefreshToggleButton = null;
	private JButton openButton = null;

	/**
	 * This is the default constructor
	 */
	public JLogViewer() {
		super();
		initialize();
	}

	protected boolean chooseFile() {
		JFileChooser chooser = new JFileChooser(Tools.workdir);
		chooser.setFileFilter(this.xmlLogFileFilter);
		int ret = chooser.showOpenDialog(this);
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (file == null)
				return false;
			try {
				loadLogFile(file);
			} catch (LogViewerException lve) {
				if (lve.getMessage() != null)
					logFileParseError(lve.getMessage());
				System.err.println("Exception thrown: " + lve);
				lve.printStackTrace();
				return false;
			} catch (IOException ioe) {
				if (ioe.getMessage() != null)
					logFileParseError(ioe.getMessage());
				System.err.println("Exception thrown: " + ioe);
				ioe.printStackTrace();
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	static public class LogViewerException extends Exception {
		static final long serialVersionUID = -7179792029080591273L;

		public LogViewerException(String s) {
			super(s);
		}
	}

	private void loadLogFile(File newFile) throws LogViewerException, IOException {
		if (!((FileModel) this.logTable.getModel()).setFile(newFile))
			throw new LogViewerException("Failed to set log file '" + newFile + "'");
		this.fileName.setText(newFile.getCanonicalPath());
	}

	/**
	 * This method initializes LogTable
	 * 
	 * @return JTable
	 */
	JLogArea getLogTable() {
		if (this.logTable == null) {
			this.logTable = new JLogArea(new FileModel());
			// this.logTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			//
			// this.listSelectionModel = this.logTable.getSelectionModel();
			// this.listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			// this.logTable.setColumnControlVisible(true);
			// this.logTable.setLocale(Locale.FRENCH);
			// this.logTable.setDefaultRenderer(String.class, new LogLevelCellRenderer());
			// this.logTable.setDefaultRenderer(Integer.class, new LogLevelCellRenderer());
			// this.logTable.setDefaultRenderer(Long.class, new LogLevelCellRenderer());
			// this.logTable.setDefaultRenderer(Date.class, new LogLevelCellRenderer());
		}
		return this.logTable;
	}

	/**
	 * This method initializes tableScrollPane
	 * 
	 * @return JScrollPane
	 */
	JScrollPane getTableScrollPane() {
		if (this.tableScrollPane == null) {
			this.tableScrollPane = new JScrollPane();
			this.tableScrollPane.setViewportView(getLogTable());
		}
		return this.tableScrollPane;
	}

	private void initialize() {
		this.setSize(698, 600);
		this.setMinimumSize(new Dimension(297, 150));

		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = GridBagConstraints.NONE;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.weightx = 0.0;
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		gridBagConstraints2.gridwidth = 2;
		gridBagConstraints2.gridx = 0;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.gridy = 2;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;
		gridBagConstraints1.gridheight = 1;
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.gridx = 0;

		this.setLayout(new GridBagLayout());
		this.setPreferredSize(new Dimension(453, 300));
		this.add(getTableScrollPane(), gridBagConstraints1);
		this.add(getToolBar(), gridBagConstraints2);
	}

	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);
		this.logTable.setLocale(l);
		this.fileNameLabel.setText(ResourceBundle.getBundle(Tools.MESSAGES, l).getString(
				"log.file_name"));
	}

}
