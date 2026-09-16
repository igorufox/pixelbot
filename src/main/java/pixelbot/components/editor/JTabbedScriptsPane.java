package pixelbot.components.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.filechooser.FileFilter;

import pixelbot.components.Splash;
import pixelbot.components.editor.hint.event.*;
import pixelbot.components.generic.*;
import pixelbot.components.generic.FileWatcher.FileModificationEvent;
import pixelbot.components.generic.FileWatcher.FileModificationEventListener;
import pixelbot.components.generic.JCustomDialog.DialogDescription;
import pixelbot.components.generic.JCustomDialog.DialogResult;
import pixelbot.misc.Tools;
import pixelbot.script.general.*;
import pixelbot.script.general.ScriptManager.BreakPointEvent;
import pixelbot.script.general.ScriptManager.BreakPointListener;

public class JTabbedScriptsPane extends JTabbedPane {
	private static final long serialVersionUID = -3878356519831235166L;

	private class ScriptListener implements KeyListener, HintEventListener {
		public ScriptListener() {}

		@Override
		public void keyTyped(KeyEvent paramKeyEvent) {}

		@Override
		public void keyReleased(KeyEvent paramKeyEvent) {}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
				saveScript(JTabbedScriptsPane.this.getSelectedIndex());
			}
		}

		@Override
		public void initialized(HintEvent event) {
			JTabbedScriptsPane.this.script.log(Level.INFO, event.getStatus().toString());
		}
	}

	protected ScriptManager script;
	private FileWatcher watcher = new FileWatcher();
	private ScriptListener script_listener = new ScriptListener();

	public JTabbedScriptsPane(ScriptManager script) {
		this.script = script;
		this.script.addBreakPointListener(new BreakPointListener() {

			@Override
			public void breakpoint(BreakPointEvent event) {
				switch (event.getType()) {
				case Pause:
					String src = event.getSource();
					ScriptEditor sta = null;
					for (int i = 0; i < JTabbedScriptsPane.this.getTabCount(); ++i) {
						sta = (ScriptEditor) JTabbedScriptsPane.this.getComponentAt(i);
						if (sta.getFile().getName().equals(src)) {
							JTabbedScriptsPane.this.setSelectedIndex(i);
							break;
						} else {
							sta = null;
						}
					}
					if (sta != null) {
						sta.getTextPane().setLineMarker(event.getLine() - 1);
					}
					break;
				case Resume:
					for (int i = 0; i < JTabbedScriptsPane.this.getTabCount(); ++i) {
						sta = (ScriptEditor) JTabbedScriptsPane.this.getComponentAt(i);
						sta.getTextPane().removeLineMarker();
					}
					break;
				}

			}
		});

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int tabNumber = JTabbedScriptsPane.this.getUI().tabForCoordinate(
						JTabbedScriptsPane.this, e.getX(), e.getY());
				if (tabNumber < 0)
					return;
				Rectangle rect = JTabbedScriptsPane.this.getTabComponentAt(tabNumber).getBounds();
				int width = ((JLabel) (JTabbedScriptsPane.this.getTabComponentAt(tabNumber)))
						.getIcon().getIconWidth();
				rect.translate(rect.width - width, 0);
				rect.width = width;
				if (rect.contains(e.getX(), e.getY())) {
					closeScript(tabNumber, true);
				}
			}
		});

		this.watcher.addFileModificationEvent(new FileModificationEventListener() {
			@Override
			public void onFileModification(FileModificationEvent event) {
				ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES,
						JTabbedScriptsPane.this.getLocale());

				DialogDescription dd = new DialogDescription();

				dd.title = messages.getString("tuning.scripts.modification.title");
				dd.text = String.format(
						messages.getString("tuning.scripts.modification.external_text"),
						event.file.getName());
				dd.options = new String[] { messages.getString("tuning.scripts.modification.yes"),
						messages.getString("tuning.scripts.modification.no") };
				if (JCustomDialog.showDialog(JTabbedScriptsPane.this, dd) == DialogResult.Button1) {
					JTabbedScriptsPane.this.loadScript(event.file);
				}
			}
		});
	}

	@Override
	public void addTab(String title, Component component) {
		super.addTab(title, component);
		JLabel lbl = new JLabel(title);
		Icon icon = new ImageIcon(this.getClass().getResource("/pixelbot/resources/close-icon.png"));
		lbl.setIcon(icon);
		// Add some spacing between text and icon, and position text to the RHS.
		lbl.setIconTextGap(5);
		lbl.setHorizontalTextPosition(SwingConstants.LEFT);
		this.setTabComponentAt(this.getTabCount() - 1, lbl);
	}

	public class CancelException extends Exception {
		private static final long serialVersionUID = -1835765841904169522L;
	}

	public void newScript() {
		ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, this.getLocale());

		ScriptEditor sta = new ScriptEditor(this.script);
		this.addTab(messages.getString("tuning.scripts.new") + (this.getTabCount() + 1), sta);
		this.setSelectedComponent(sta);
		sta.getTextPane().addKeyListener(this.script_listener);
		sta.addHintInitializationEvent(this.script_listener);
	}

	public void loadScripts(Splash splash, List<String> files) {
		this.script.scopeReset();
		int i = 0;
		for (String file : files) {
			if (splash != null) {
				splash.process("Load Scripts / " + new File(file).getName() + " (" + ++i + "/"
						+ files.size() + ")");
			}
			try {
				loadScript(new File(Tools.workdir, file).getCanonicalFile());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void loadScript() {
		ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, this.getLocale());

		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(Tools.workdir, "scripts"));
		chooser.setDialogTitle(messages.getString("tuning.scripts.dialog.load_title"));

		chooser.setAcceptAllFileFilterUsed(true);
		chooser.setMultiSelectionEnabled(true);

		chooser.addChoosableFileFilter(new FileNameExtensionFilter(messages
				.getString("tuning.scripts.dialog.filter.java"), new String[] { "djava" }));
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(messages
				.getString("tuning.scripts.dialog.filter.js"), new String[] { "djs" }));
		chooser.setFileFilter(new FileNameExtensionFilter(messages
				.getString("tuning.scripts.dialog.filter.all_supported"), new String[] { "djava",
				"djs" }));
		if (chooser.showOpenDialog(this) == 0) {
			for (File f : chooser.getSelectedFiles()) {
				loadScript(f);
			}
		}
	}

	protected void loadScript(File f) {
		ScriptEditor sta = null;
		for (int i = 0; i < this.getTabCount(); ++i) {
			sta = (ScriptEditor) this.getComponentAt(i);
			if (sta.getFile().equals(f)) {
				break;
			} else {
				sta = null;
			}
		}

		if (sta == null) {
			sta = new ScriptEditor(this.script);
			sta.getTextPane().addKeyListener(this.script_listener);
			sta.addHintInitializationEvent(this.script_listener);
			this.addTab(f.getName(), sta);
			sta.setFile(f);
			this.watcher.addWatch(f);
		}
		try (FileInputStream fis = new FileInputStream(f)) {
			byte[] fileBArray = new byte[(int) f.length()];
			fis.read(fileBArray);
			sta.setData(fileBArray);
			sta.setModified(false);
		} catch (Exception ex) {}

		this.script.parse(sta.getFile().getName(), sta.getData());
		this.setSelectedComponent(sta);
	}

	public void saveScript() {
		this.saveScript(this.getSelectedIndex());
	}

	public void saveScript(int pos) {
		ScriptEditor sta = (ScriptEditor) JTabbedScriptsPane.this.getComponentAt(pos);
		if (sta.getFile() == null) {
			saveAsScript();
			return;
		}
		try (FileOutputStream fos = new FileOutputStream(sta.getFile())) {
			this.watcher.removeWatch(sta.getFile());
			fos.write(sta.getData());
			sta.setModified(false);
			this.watcher.addWatch(sta.getFile());
		} catch (Exception ex) {}
		this.script.parse(sta.getFile().getName(), sta.getData());
	}

	public void saveAsScript() {
		ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, this.getLocale());

		ScriptEditor sta = (ScriptEditor) this.getSelectedComponent();
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(Tools.workdir, "scripts"));
		chooser.setDialogTitle(messages.getString("tuning.scripts.dialog.save_tilte"));

		chooser.setAcceptAllFileFilterUsed(true);
		chooser.setMultiSelectionEnabled(false);
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(messages
				.getString("tuning.scripts.dialog.filter.java"), new String[] { "djava" }));
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(messages
				.getString("tuning.scripts.dialog.filter.js"), new String[] { "djs" }));
		if (chooser.showSaveDialog(this) == 0) {
			File f = chooser.getSelectedFile();
			FileFilter ff = chooser.getFileFilter();
			if (ff instanceof FileNameExtensionFilter) {
				f = this.script.addExtension(f, ((FileNameExtensionFilter) ff).getExtensions()[0]);
			}
			this.watcher.removeWatch(sta.getFile());
			sta.setFile(f);
			this.setTitleAt(this.getSelectedIndex(), f.getName());
			this.saveScript();
		}

	}

	public List<File> closeScripts() throws IOException, CancelException {
		ArrayList<File> result = new ArrayList<File>();
		while (this.getTabCount() > 0) {
			ScriptEditor mta = (ScriptEditor) this.getComponentAt(0);
			result.add(mta.getFile());
			if (!this.closeScript(0, false)) {
				throw new CancelException();
			}
		}
		return result;

	}

	public boolean closeScript(int pos, boolean parse) {
		ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, this.getLocale());

		ScriptEditor sta = (ScriptEditor) JTabbedScriptsPane.this.getComponentAt(pos);

		if (sta.isModified()) {
			DialogDescription dd = new DialogDescription();

			dd.title = messages.getString("tuning.scripts.modification.title");
			dd.text = String.format(
					messages.getString("tuning.scripts.modification.internal_text"), sta.getFile()
							.getName());
			dd.options = new String[] { messages.getString("tuning.scripts.modification.yes"),
					messages.getString("tuning.scripts.modification.no"),
					messages.getString("tuning.scripts.modification.cancel") };
			switch (JCustomDialog.showDialog(this, dd)) {
			case Button1:
				this.saveScript(pos);
				break;
			case Button2:
				break;
			default:
				return false;
			}
		}
		this.watcher.removeWatch(sta.getFile());
		this.removeTabAt(pos);
		if (parse) {
			this.script.scopeReset();
			for (int i = 0; i < this.getTabCount(); ++i) {
				sta = (ScriptEditor) JTabbedScriptsPane.this.getComponentAt(i);
				this.script.parse(sta.getFile().getName(), sta.getData());
			}
		}
		return true;
	}

	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);
		for (int i = 0; i < this.getTabCount(); ++i) {
			ScriptEditor sta = (ScriptEditor) this.getComponentAt(i);
			sta.setLocale(l);
		}
	}

}
