package pixelbot.gui.settings;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.prefs.*;

import javax.swing.*;

import pixelbot.components.Splash;
import pixelbot.components.editor.JTabbedScriptsPane.CancelException;
import pixelbot.gui.PixelBotView;
import pixelbot.json.JSONValue;
import pixelbot.misc.Tools;
import pixelbot.script.general.*;
import pixelbot.script.misc.JSONScriptValue;
import pixelbot.script.scope.general.ScopeJob;

public class SettingsPanel extends JPanel {
	private static final long serialVersionUID = -2316330266897488368L;

	protected JTabbedPane jTabbedPane_settings;
	private IOSettingsPanel jPanel_io_settings;
	private ElementsPanel jPanel_elements_main;
	private ScriptsPanel jPanel_script;
	private ConfigurationPanel jPanel_configuration;
	private InterpreterPanel jPanel_interpreter;

	protected Preferences prefs;

	private PixelBotView main;
	private ScriptManager script;
	private IOSettings settings;

	public SettingsPanel(PixelBotView main, ScriptManager script, IOSettings settings,
			Preferences prefs) {
		super();
		this.main = main;
		this.script = script;
		this.settings = settings;
		this.prefs = prefs;

		this.jTabbedPane_settings = new JTabbedPane();
		this.jPanel_io_settings = new IOSettingsPanel(this.main, this.settings);
		this.jPanel_elements_main = new ElementsPanel(this.script);
		this.jPanel_script = new ScriptsPanel(this.script);
		this.jPanel_configuration = new ConfigurationPanel();
		this.jPanel_interpreter = new InterpreterPanel(this.script);

		this.setLayout(new BoxLayout(this, 2));
		this.add(this.jTabbedPane_settings);

		this.jTabbedPane_settings.addTab("", this.jPanel_io_settings);
		this.jTabbedPane_settings.addTab("", this.jPanel_elements_main);
		this.jTabbedPane_settings.addTab("", this.jPanel_script);
		this.jTabbedPane_settings.addTab("", this.jPanel_configuration);
		this.jTabbedPane_settings.addTab("", this.jPanel_interpreter);

	}

	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);

		ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, l);

		this.jTabbedPane_settings.setTitleAt(0, messages.getString("tuning.io.title"));
		this.jTabbedPane_settings.setTitleAt(1, messages.getString("tuning.elements.title"));
		this.jTabbedPane_settings.setTitleAt(2, messages.getString("tuning.scripts.title"));
		this.jTabbedPane_settings.setTitleAt(3, messages.getString("tuning.configuration.title"));
		this.jTabbedPane_settings.setTitleAt(4, messages.getString("tuning.interpreters.title"));

		this.jPanel_io_settings.setLocale(l);
		this.jPanel_elements_main.setLocale(l);
		this.jPanel_script.setLocale(l);
		this.jPanel_configuration.setLocale(l);
		this.jPanel_interpreter.setLocale(l);

	}

	public void setBotColor(Color color) {
		this.jPanel_io_settings.setBotColor(color);
	}

	public void activateScriptsPane() {
		this.jTabbedPane_settings.setSelectedIndex(3);
		this.jPanel_script.populateStack();
	}

	public void loadPrefs(Splash splash) {

		try {
			splash.process("Fill Elements Tree");
			this.jPanel_elements_main.jTree_elements_fill();

			splash.process("Process Preferences");

			splash.process("Load Interpreters Prefs");
			@SuppressWarnings("unchecked")
			Map<String, Object> interpreters_params = (Map<String, Object>) JSONValue
					.parse(this.prefs.node("script").get("interpreters", "{}"));
			this.jPanel_interpreter.setInterpretersPrefs(interpreters_params);

			splash.process("Get Script Names");
			Preferences subprefs = this.prefs.node("script");

			@SuppressWarnings("unchecked")
			List<String> stored = (List<String>) JSONValue.parse(subprefs.get("files", "[]"));

			List<String> file_names = new ArrayList<String>();
			if (stored != null) {
				file_names.addAll(stored);
				file_names.remove("scripts.jar");
			}

			if (file_names.isEmpty()) {
				if (new File(Tools.workdir, "scripts.jar").exists()) {
					file_names.add("scripts.jar");
				} else {
					// listFiles() returns null when there is no scripts directory next to the
					// application, which is the normal state of a fresh install.
					File[] scripts = new File(Tools.workdir, "scripts").listFiles(new FileFilter() {
						@Override
						public boolean accept(File pathname) {
							return pathname.getName().endsWith(".djs")
									|| pathname.getName().endsWith(".djava");
						}
					});
					if (scripts != null) {
						for (File file : scripts) {
							String relative = Tools.workdir.getCanonicalFile().toURI()
									.relativize(file.getCanonicalFile().toURI()).getPath();
							file_names.add(relative);
						}
					}
				}
			}

			splash.process("Load Scripts");
			String active = subprefs.get("active", null);
			Object breakpoints = JSONValue.parse(subprefs.get("breakponts", null));
			this.jPanel_script.loadScripts(splash, file_names, active, breakpoints);

			splash.process("Activate Job");
			String job = subprefs.get("job", null);
			this.main.setActiveJob(job);
		} catch (Exception e) {
			e.printStackTrace();
		}

		splash.process("Init IO Settings");

		try {
			Preferences subprefs = this.prefs.node("bot");

			this.settings.setCheckUserActivity(subprefs.getBoolean("check_user_activity", true));
			this.settings.setHighlightElements(subprefs.getBoolean("highlight_found_elems", true));
			this.settings.setHideOnExecution(subprefs.getBoolean("hide_on_execution", false));
			this.settings.setMinimizeToSystemTray(subprefs.getBoolean("minimize_to_system_tray",
					false));

			this.settings.setLocale(subprefs.get("locale", "RU"));

			Preferences mouse = subprefs.node("mouse");
			this.settings.setMouseMovingSpeed(mouse.getInt("moving_speed", 50));
			this.settings.setMouseClickDurationMin(mouse.getInt("click_duration_min", 100));
			this.settings.setMouseClickDurationMax(mouse.getInt("click_duration_max", 300));
			this.settings.setMousePauseAfterClickMin(mouse.getInt("pause_after_click_min", 100));
			this.settings.setMousePauseAfterClickMax(mouse.getInt("pause_after_click_max", 300));
			this.settings.setMousePauseAfterPositionMin(mouse.getInt("pause_after_positioning_min",
					100));
			this.settings.setMousePauseAfterPositionMax(mouse.getInt("pause_after_positioning_max",
					300));

			this.main.changeColor(new Color((int) Long.parseLong(subprefs.get("color", "ffff6666"),
					16)));

			Rectangle r = new Rectangle();
			r.x = subprefs.node("bounds").getInt("x", 400);
			r.y = subprefs.node("bounds").getInt("y", 200);
			r.width = subprefs.node("bounds").getInt("width", 600);
			r.height = subprefs.node("bounds").getInt("height", 600);

			this.main.setBounds(r);
		} catch (Exception e) {}
		splash.process("Translate");

		String lang = this.prefs.get("locale", "ru");
		this.main.translate(lang);
	}

	public boolean savePrefs() {
		try {
			this.prefs.clear();
		} catch (BackingStoreException e) {}

		try {
			Preferences subprefs = this.prefs.node("script");
			subprefs.clear();
			ScopeJob job = this.main.getActiveJob();
			if (job != null) {
				subprefs.put("job", job.getId());
			}

			subprefs.put("breakponts",
					JSONScriptValue.toJSONString(this.script.getScriptDebugger().getBreakPoints()));

			String active = this.jPanel_script.getActive();
			if (active != null) {
				subprefs.put("active", active);
			}

			List<File> files = this.jPanel_script.closeScripts();
			ArrayList<String> file_names = new ArrayList<String>();
			for (File file : files) {
				if (file != null) {
					String relative = Tools.workdir.getCanonicalFile().toURI()
							.relativize(file.getCanonicalFile().toURI()).getPath();
					file_names.add(relative);
				}
			}
			subprefs.put("files", JSONScriptValue.toJSONString(file_names));

		} catch (CancelException e) {
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.prefs.put("locale", this.getLocale().getLanguage());
		try {
			Preferences subprefs = this.prefs.node("bot");
			subprefs.clear();

			subprefs.put("color",
					Integer.toHexString(this.main.getContentPane().getBackground().getRGB()));
			subprefs.putBoolean("check_user_activity", this.settings.isCheckUserActivity());
			subprefs.putBoolean("highlight_found_elems", this.settings.isHighlightElements());
			subprefs.putBoolean("hide_on_execution", this.settings.isHideOnExecution());
			subprefs.putBoolean("minimize_to_system_tray", this.settings.isMinimizeToSystemTray());
			subprefs.put("locale", this.settings.getLocale());

			subprefs = this.prefs.node("bot.mouse");
			subprefs.clear();

			subprefs.putInt("moving_speed", this.settings.getMouseMovingSpeed());
			subprefs.putInt("click_duration_min", this.settings.getMouseClickDurationMin());
			subprefs.putInt("click_duration_max", this.settings.getMouseClickDurationMax());
			subprefs.putInt("pause_after_click_min", this.settings.getMousePauseAfterClickMin());
			subprefs.putInt("pause_after_click_max", this.settings.getMousePauseAfterClickMax());
			subprefs.putInt("pause_after_positioning_min",
					this.settings.getMousePauseAfterPositionMin());
			subprefs.putInt("pause_after_positioning_max",
					this.settings.getMousePauseAfterPositionMax());

			subprefs = this.prefs.node("bot.bounds");
			Rectangle r = this.main.getBounds();
			subprefs.putInt("x", r.x);
			subprefs.putInt("y", r.y);
			subprefs.putInt("width", r.width);
			subprefs.putInt("height", r.height);
		} catch (Exception e) {}
		Map<String, Object> interpreters_params = this.jPanel_interpreter.getInterpretersPrefs();
		this.prefs.node("script").put("interpreters",
				JSONScriptValue.toJSONString(interpreters_params));
		return true;
	}
}
