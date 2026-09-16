package pixelbot.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

import org.jdesktop.swingx.JXTreeTable;

import pixelbot.components.Splash;
import pixelbot.components.editor.ScriptEditor;
import pixelbot.components.logviewer.*;
import pixelbot.elements.*;
import pixelbot.gui.settings.SettingsPanel;
import pixelbot.gui.task.TaskPanel;
import pixelbot.log.ScriptLogLevel;
import pixelbot.misc.Tools;
import pixelbot.prefs.FilePreferencesFactory;
import pixelbot.script.general.*;
import pixelbot.script.general.ScriptManager.BreakPointEvent;
import pixelbot.script.general.ScriptManager.BreakPointListener;
import pixelbot.script.general.ScriptManager.ScriptEvent;
import pixelbot.script.general.ScriptManager.ScriptStateListener;
import pixelbot.script.scope.general.ScopeJob;

public class PixelBotView extends JFrame {
	private static final long serialVersionUID = -3541833608890814798L;

	protected IOSettings settings = new IOSettings();
	protected ScriptManager script = new ScriptManager(this.settings);
	protected Preferences prefs;

	private JMenuBar jMainMenuBar;
	private JMenu mnControl;
	private JMenu mnSettings;
	private JMenu mnSyslog;
	private JMenu mnLanguages;
	private ButtonGroup mnLanguagesButtonGroup;
	protected JCheckBoxMenuItem mntmcb_russian;
	protected JCheckBoxMenuItem mntmcb_english;
	private JMenu mnDebug;
	protected JMenuItem mntmRun;
	protected JMenuItem mntmDebug;
	private JSeparator mntmSeparator;
	protected JMenuItem mntmResume;
	protected JMenuItem mntmStepInto;
	protected JMenuItem mntmStepOver;
	protected JMenuItem mntmStepReturn;
	protected JMenuItem mntmSuspend;
	protected JMenuItem mntmTerminate;
	private JMenu mnExit;

	protected JPanel botPanel;

	protected TaskPanel jPanel_task;
	protected SettingsPanel jPanel_settings;
	protected JLogViewer jPanel_system_log;

	private TrayNotification tray;

	public void translate(String lang) {
		this.setLocale(new Locale(lang));
		Locale.setDefault(this.getLocale());
		if ("ru".equals(lang)) {
			this.mntmcb_russian.setSelected(true);
		} else if ("en".equals(lang)) {
			this.mntmcb_english.setSelected(true);
		}
	}

	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);

		ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, l);

		if (this.mnControl != null) {

			this.mnControl.setText(messages.getString("menu.management"));
			this.mnSettings.setText(messages.getString("menu.tuning"));
			this.mnSyslog.setText(messages.getString("menu.log"));
			this.mnDebug.setText(messages.getString("menu.debug"));
			this.mntmDebug.setText(messages.getString("menu.debug.debug"));
			this.mntmRun.setText(messages.getString("menu.debug.run"));
			this.mntmResume.setText(messages.getString("menu.debug.resume"));
			this.mntmStepInto.setText(messages.getString("menu.debug.step into"));
			this.mntmStepOver.setText(messages.getString("menu.debug.step over"));
			this.mntmStepReturn.setText(messages.getString("menu.debug.step return"));
			this.mntmSuspend.setText(messages.getString("menu.debug.suspend"));
			this.mntmTerminate.setText(messages.getString("menu.debug.terminate"));
			this.mnExit.setText(messages.getString("menu.exit"));

			this.jPanel_task.setLocale(l);
			this.jPanel_settings.setLocale(l);
			this.jPanel_system_log.setLocale(l);

			this.tray.setLocale(l);

		}
	}

	public void changeColor(Color color) {
		this.setComponentsBackGround(this.getContentPane(), color);

		this.jPanel_settings.setBotColor(color);

		UIManager.put("nimbusBase", new ColorUIResource(color.darker()));
		UIManager.put("background", new ColorUIResource(color));
		UIManager.put("control", new ColorUIResource(color));
		UIManager.put("Button.background", new ColorUIResource(color));
		UIManager.put("Panel.background", new ColorUIResource(color));
		UIManager.put("Panel.disabled", new ColorUIResource(color));

		UIManager.put("TaskPane.titleBackgroundGradientStart", color.brighter());
		UIManager.put("TaskPane.titleBackgroundGradientEnd", color.darker());

		// UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		// defaults.put("Panel.background", Color.blue);

		// Button.background
		try {
			UIManager.setLookAndFeel(UIManager.getLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {}
		this.jMainMenuBar.repaint(0);
		SwingUtilities.updateComponentTreeUI(this);

	}

	protected void setComponentsBackGround(Component cmp, Color col) {
		if (!(cmp instanceof JComponent))
			return;
		if (cmp instanceof JComboBox)
			return;
		if (cmp instanceof JTextField)
			return;
		if (cmp instanceof JList)
			return;
		if (cmp instanceof JTree)
			return;
		if (cmp instanceof JXTreeTable)
			return;
		if (cmp instanceof JTextArea)
			return;
		if (cmp instanceof ScriptEditor)
			return;
		if (cmp instanceof JLogArea)
			return;
		cmp.setBackground(col);

		for (int i = 0; i < ((JComponent) cmp).getComponentCount(); i++) {
			Component cmp1 = ((JComponent) cmp).getComponent(i);
			setComponentsBackGround(cmp1, col);
		}
	}

	public PixelBotView() throws AWTException {
		super();
		Splash splash = new Splash();
		splash.show(30);
		splash.process("Init Icons");

		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setIconImages(Arrays.asList(new Image[] {
				new ImageIcon(PixelBotView.class.getResource("/pixelbot/resources/mainicon_016.png"))
						.getImage(),
				new ImageIcon(PixelBotView.class.getResource("/pixelbot/resources/mainicon_032.png"))
						.getImage(),
				new ImageIcon(PixelBotView.class.getResource("/pixelbot/resources/mainicon_048.png"))
						.getImage(),
				new ImageIcon(PixelBotView.class.getResource("/pixelbot/resources/mainicon_064.png"))
						.getImage(),
				new ImageIcon(PixelBotView.class.getResource("/pixelbot/resources/mainicon_096.png"))
						.getImage(),
				new ImageIcon(PixelBotView.class.getResource("/pixelbot/resources/mainicon_128.png"))
						.getImage() }));

		splash.process("Load Preferences");
		System.setProperty("java.util.prefs.PreferencesFactory",
				FilePreferencesFactory.class.getName());
		this.prefs = Preferences.systemRoot().node("/pixelbot");

		splash.process("Init Components");
		this.initComponents();

		for (String property : new String[] { "java.version", "java.vm.version",
				"java.runtime.version" }) {
			this.script.log(ScriptLogLevel.Info, property + ": " + System.getProperty(property));
		}

		String version = "";
		try (InputStream is = PixelBotView.class.getResourceAsStream("/pixelbot/version.txt");
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			int len = 0;
			byte[] bytes2 = new byte[1024];

			while ((len = is.read(bytes2)) > 0) {
				baos.write(bytes2, 0, len);
			}

			version = new String(baos.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.setTitle("PixelBot" + version);

		splash.process("Load Elements");
		this.script.getHelper().elements = ElemTools.loadElements();

		splash.process("Process Elements");
		if (this.script.getHelper().elements == null) {
			this.script.getHelper().elements = new Elements();
		}
		while (this.script.getHelper().elements.get("") != null)
			this.script.getHelper().elements.remove("");

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				PixelBotView.this.windowClosing();
			}

		});

		splash.process("Init Tray");
		this.tray = new TrayNotification(this);

		splash.process("Process Preferences");
		this.jPanel_settings.loadPrefs(splash);
		splash.hide();

		this.setVisible(true);
	}

	private void initComponents() {

		this.jPanel_task = new TaskPanel(this.script, this.prefs);
		this.jPanel_settings = new SettingsPanel(this, this.script, this.settings, this.prefs);
		this.jPanel_system_log = new JLogViewer();

		this.botPanel = new JPanel();

		this.jMainMenuBar = new JMenuBar();
		this.mnControl = new JMenu("Control");
		this.mnSettings = new JMenu("Settings");
		this.mnSyslog = new JMenu("Syslog");
		this.mnLanguages = new JMenu("Languages");
		this.mntmcb_russian = new JCheckBoxMenuItem("Russian");
		this.mntmcb_english = new JCheckBoxMenuItem("English");
		this.mnLanguagesButtonGroup = new ButtonGroup();
		this.mntmRun = new JMenuItem("Run");
		this.mntmDebug = new JMenuItem("Debug");
		this.mntmSeparator = new JSeparator();
		this.mnDebug = new JMenu("Debug");
		this.mntmResume = new JMenuItem("Resume");
		this.mntmStepInto = new JMenuItem("Step into");
		this.mntmStepOver = new JMenuItem("Step over");
		this.mntmStepReturn = new JMenuItem("Step return");
		this.mntmSuspend = new JMenuItem("Suspend");
		this.mntmTerminate = new JMenuItem("Terminate");
		this.mnExit = new JMenu("Exit");

		this.botPanel.setLayout(new BoxLayout(this.botPanel, BoxLayout.LINE_AXIS));
		// this.mainPanel.setLayout(new BorderLayout());

		this.script.addStateListener(new ScriptStateListener() {
			@Override
			public void stateChanged(ScriptEvent event) {
				switch (event.state) {
				case job_run:
					PixelBotView.this.mntmDebug.setEnabled(false);
					PixelBotView.this.mntmRun.setEnabled(false);
					PixelBotView.this.mntmSuspend.setEnabled(false);
					PixelBotView.this.mntmTerminate.setEnabled(true);
					if (PixelBotView.this.settings.isHideOnExecution()) {
						PixelBotView.this.setExtendedState(Frame.ICONIFIED);
					}
					break;
				case job_debug:
					PixelBotView.this.mntmDebug.setEnabled(false);
					PixelBotView.this.mntmRun.setEnabled(false);
					PixelBotView.this.mntmSuspend.setEnabled(true);
					PixelBotView.this.mntmTerminate.setEnabled(true);
					if (PixelBotView.this.settings.isHideOnExecution()) {
						PixelBotView.this.setExtendedState(Frame.ICONIFIED);
					}
					break;
				case job_done:
					PixelBotView.this.mntmDebug.setEnabled(true);
					PixelBotView.this.mntmRun.setEnabled(true);
					PixelBotView.this.mntmResume.setEnabled(false);
					PixelBotView.this.mntmStepOver.setEnabled(false);
					PixelBotView.this.mntmStepReturn.setEnabled(false);
					PixelBotView.this.mntmStepInto.setEnabled(false);
					PixelBotView.this.mntmSuspend.setEnabled(false);
					PixelBotView.this.mntmTerminate.setEnabled(false);
					PixelBotView.this.setVisible(true);
					PixelBotView.this.setExtendedState(Frame.NORMAL);
					break;
				default:
					break;
				}
			}
		});
		PixelBotView.this.script.addBreakPointListener(new BreakPointListener() {
			@Override
			public void breakpoint(BreakPointEvent event) {
				switch (event.getType()) {
				case Pause:
					PixelBotView.this.mntmResume.setEnabled(true);
					PixelBotView.this.mntmStepInto.setEnabled(true);
					PixelBotView.this.mntmStepOver.setEnabled(true);
					PixelBotView.this.mntmStepReturn.setEnabled(true);
					PixelBotView.this.mntmSuspend.setEnabled(false);
					PixelBotView.this.jPanel_settings.setVisible(true);
					PixelBotView.this.jPanel_task.setVisible(false);
					PixelBotView.this.jPanel_settings.activateScriptsPane();
					break;
				case Resume:
					PixelBotView.this.mntmResume.setEnabled(false);
					PixelBotView.this.mntmStepInto.setEnabled(false);
					PixelBotView.this.mntmStepOver.setEnabled(false);
					PixelBotView.this.mntmStepReturn.setEnabled(false);
					PixelBotView.this.mntmSuspend.setEnabled(true);
					break;
				}
			}
		});

		this.jPanel_task.setVisible(true);
		this.jPanel_settings.setVisible(false);
		this.jPanel_system_log.setVisible(false);

		this.botPanel.add(this.jPanel_settings);
		this.botPanel.add(this.jPanel_task);
		this.botPanel.add(this.jPanel_system_log);

		// this.mainPanel.add(this.botPanel, BorderLayout.LINE_END);
		// this.mainPanel.add(this.browser, BorderLayout.CENTER);
		// this.mainPanel.setLeftComponent(this.browser);
		// this.mainPanel.setRightComponent(this.botPanel);
		// this.botPanel.setPreferredSize(new Dimension(150, 0));
		// this.mainPanel.setResizeWeight(1);
		// this.mainPanel.setDividerSize(3);

		this.setContentPane(this.botPanel);

		this.mnControl.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				PixelBotView.this.jPanel_task.setVisible(true);
				PixelBotView.this.jPanel_settings.setVisible(false);
				PixelBotView.this.jPanel_system_log.setVisible(false);
			}
		});
		this.jMainMenuBar.add(this.mnControl);

		this.mnSettings.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				PixelBotView.this.jPanel_task.setVisible(false);
				PixelBotView.this.jPanel_settings.setVisible(true);
				PixelBotView.this.jPanel_system_log.setVisible(false);
			}
		});
		this.jMainMenuBar.add(this.mnSettings);

		this.mnSyslog.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				PixelBotView.this.jPanel_task.setVisible(false);
				PixelBotView.this.jPanel_settings.setVisible(false);
				PixelBotView.this.jPanel_system_log.setVisible(true);
			}
		});
		this.jMainMenuBar.add(this.mnSyslog);

		this.mnDebug.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent paramItemEvent) {
				PixelBotView.this.validate();
			}
		});

		this.mnLanguages.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent paramItemEvent) {
				PixelBotView.this.validate();
			}
		});

		this.mnLanguagesButtonGroup.add(this.mntmcb_russian);
		this.mntmcb_russian.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (PixelBotView.this.mntmcb_russian.isSelected())
					translate("ru");
			}
		});
		this.mnLanguages.add(this.mntmcb_russian);

		this.mnLanguagesButtonGroup.add(this.mntmcb_english);
		this.mntmcb_english.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent evt) {
				if (PixelBotView.this.mntmcb_english.isSelected())
					translate("en");
			}
		});
		this.mnLanguages.add(this.mntmcb_english);
		this.jMainMenuBar.add(this.mnLanguages);
		this.jMainMenuBar.add(this.mnDebug);

		this.mntmRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, InputEvent.CTRL_MASK));
		this.mntmRun.setIcon(new ImageIcon(PixelBotView.class
				.getResource("/pixelbot/resources/run.png")));
		this.mntmRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PixelBotView.this.startWork(false);
			}
		});
		this.mntmDebug.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
		this.mntmDebug.setIcon(new ImageIcon(PixelBotView.class
				.getResource("/pixelbot/resources/debug.png")));
		this.mntmDebug.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PixelBotView.this.startWork(true);
			}
		});

		this.mntmResume.setEnabled(false);
		this.mntmResume.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
		this.mntmResume.setIcon(new ImageIcon(PixelBotView.class
				.getResource("/pixelbot/resources/resume.png")));
		this.mntmResume.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PixelBotView.this.script.getScriptDebugger().resume();
			}
		});

		this.mntmStepInto.setEnabled(false);
		this.mntmStepInto.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		this.mntmStepInto.setIcon(new ImageIcon(PixelBotView.class
				.getResource("/pixelbot/resources/stepinto.png")));
		this.mntmStepInto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PixelBotView.this.script.getScriptDebugger().step_into();
			}
		});

		this.mntmStepOver.setEnabled(false);
		this.mntmStepOver.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
		this.mntmStepOver.setIcon(new ImageIcon(PixelBotView.class
				.getResource("/pixelbot/resources/stepover.png")));
		this.mntmStepOver.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PixelBotView.this.script.getScriptDebugger().step_over();
			}
		});

		this.mntmStepReturn.setEnabled(false);
		this.mntmStepReturn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
		this.mntmStepReturn.setIcon(new ImageIcon(PixelBotView.class
				.getResource("/pixelbot/resources/stepreturn.png")));
		this.mntmStepReturn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PixelBotView.this.script.getScriptDebugger().step_return();
			}
		});

		this.mntmSuspend.setEnabled(false);
		this.mntmSuspend.setIcon(new ImageIcon(PixelBotView.class
				.getResource("/pixelbot/resources/suspend.png")));
		this.mntmSuspend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PixelBotView.this.script.getScriptDebugger().suspend();
			}
		});

		this.mntmTerminate.setEnabled(false);
		this.mntmTerminate.setIcon(new ImageIcon(PixelBotView.class
				.getResource("/pixelbot/resources/terminate.png")));
		this.mntmTerminate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2,
				InputEvent.CTRL_MASK));
		this.mntmTerminate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PixelBotView.this.stopWork();
			}
		});

		this.mnDebug.add(this.mntmRun);
		this.mnDebug.add(this.mntmDebug);
		this.mnDebug.add(this.mntmSeparator);
		this.mnDebug.add(this.mntmResume);
		this.mnDebug.add(this.mntmStepInto);
		this.mnDebug.add(this.mntmStepOver);
		this.mnDebug.add(this.mntmStepReturn);
		this.mnDebug.add(this.mntmSuspend);
		this.mnDebug.add(this.mntmTerminate);

		this.mnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				PixelBotView.this.windowClosing();
			}
		});

		this.jMainMenuBar.add(this.mnExit);

		setJMenuBar(this.jMainMenuBar);

		this.addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent e) {
				if (PixelBotView.this.settings.isMinimizeToSystemTray()
						&& (e.getNewState() & ICONIFIED) == ICONIFIED) {
					setVisible(false);
				}
			}
		});

	}

	protected void windowClosing() {
		boolean saved;
		try {
			saved = this.jPanel_settings.savePrefs();
		} catch (Throwable e) {
			// Failing to write the preferences must not leave a window that cannot be closed.
			this.script.log(ScriptLogLevel.Error, "Could not save preferences: " + e);
			saved = true;
		}

		if (saved) {
			this.script.stopWork();
			this.dispose();
		}
	}

	@Override
	public void dispose() {
		// this.browser.dispose();
		super.dispose();
		this.tray.close();
		this.script.getHelper().dispose();
	}

	public void startWork(boolean debug) {
		this.jPanel_task.startWork(debug);
	}

	public void stopWork() {
		this.jPanel_task.stopWork();
	}

	public IOSettings getSettings() {
		return this.settings;
	}

	public void setActiveJob(String job) {
		this.jPanel_task.setActiveJob(job);
	}

	public ScopeJob getActiveJob() {
		return this.jPanel_task.getActiveJob();
	}
}
