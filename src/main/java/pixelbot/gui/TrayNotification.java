package pixelbot.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import pixelbot.components.generic.JXTrayIcon;
import pixelbot.misc.Tools;
import pixelbot.script.general.IOSettings;

public class TrayNotification {
	private boolean active;
	protected final PixelBotView main;
	private final IOSettings settings;
	private JXTrayIcon trayIcon;

	private JMenuItem mi_start_job;
	private JMenuItem mi_stop_job;
	private JMenuItem mi_minimize;
	private JMenuItem mi_restore;
	private JMenu mnu_settings;
	private JCheckBoxMenuItem cbmi_settings_check_user_activity;
	private JCheckBoxMenuItem cbmi_settings_highlight_found_elems;
	private JCheckBoxMenuItem cbmi_settings_hide_on_execution;
	private JCheckBoxMenuItem cbmi_settings_minimize_to_tray;
	private JMenuItem mi_exit;

	public TrayNotification(PixelBotView main) {
		this.main = main;
		this.settings = this.main.getSettings();
		this.active = SystemTray.isSupported();
		if (this.active) {
			final SystemTray tray = SystemTray.getSystemTray();

			final JPopupMenu popup = new JPopupMenu();
			this.trayIcon = new JXTrayIcon(
					new ImageIcon(PixelBotView.class
							.getResource("/pixelbot/resources/mainicon_016.png")).getImage());

			this.mi_start_job = new JMenuItem("Start job");
			this.mi_stop_job = new JMenuItem("Stop job");
			this.mi_minimize = new JMenuItem("Minimize");
			this.mi_restore = new JMenuItem("Restore");
			this.mnu_settings = new JMenu("Settings");
			this.cbmi_settings_check_user_activity = new JCheckBoxMenuItem("Check user activity");
			this.cbmi_settings_highlight_found_elems = new JCheckBoxMenuItem(
					"Highlight found elems");
			this.cbmi_settings_hide_on_execution = new JCheckBoxMenuItem("Hide on execution");
			this.cbmi_settings_minimize_to_tray = new JCheckBoxMenuItem("Minimize to sysem tray");
			this.mi_exit = new JMenuItem("Exit");

			// Add components to pop-up menu
			popup.add(this.mi_start_job);
			popup.add(this.mi_stop_job);
			popup.addSeparator();
			popup.add(this.mi_minimize);
			popup.add(this.mi_restore);
			popup.addSeparator();
			popup.add(this.mnu_settings);
			this.mnu_settings.add(this.cbmi_settings_check_user_activity);
			this.mnu_settings.add(this.cbmi_settings_highlight_found_elems);
			this.mnu_settings.add(this.cbmi_settings_hide_on_execution);
			this.mnu_settings.add(this.cbmi_settings_minimize_to_tray);
			this.mnu_settings.addSeparator();
			popup.addSeparator();
			popup.add(this.mi_exit);

			this.trayIcon.setJPopupMenu(popup);

			this.cbmi_settings_check_user_activity.setModel(this.settings
					.getCheckUserActivityModel());
			this.cbmi_settings_highlight_found_elems.setModel(this.settings
					.getHighlightElementsModel());
			this.cbmi_settings_hide_on_execution.setModel(this.settings.getHideOnExecutionModel());
			this.cbmi_settings_minimize_to_tray.setModel(this.settings
					.getMinimizeToSystemTrayModel());

			this.mi_start_job.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					TrayNotification.this.main.startWork(false);
				}
			});
			this.mi_stop_job.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					TrayNotification.this.main.stopWork();
				}
			});

			this.mi_minimize.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					TrayNotification.this.main.setExtendedState(Frame.ICONIFIED);
				}
			});
			this.mi_restore.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					TrayNotification.this.main.setVisible(true);
					TrayNotification.this.main.setExtendedState(Frame.NORMAL);
				}
			});

			this.mi_exit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					TrayNotification.this.main.dispose();
				}
			});

			try {
				tray.add(this.trayIcon);
			} catch (AWTException e) {
				System.out.println("TrayIcon could not be added.");
			}
		}
	}

	public void close() {
		if (this.active) {
			final SystemTray tray = SystemTray.getSystemTray();
			tray.remove(this.trayIcon);
		}
	}

	public void setLocale(Locale l) {
		if (this.active) {
			ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, l);

			this.mi_start_job.setText(messages.getString("tray.job_start"));
			this.mi_stop_job.setText(messages.getString("tray.job_stop"));
			this.mi_minimize.setText(messages.getString("tray.minimize"));
			this.mi_restore.setText(messages.getString("tray.restore"));
			this.mnu_settings.setText(messages.getString("tray.settings.title"));
			this.cbmi_settings_check_user_activity.setText(messages
					.getString("tray.settings.check_user_activity"));
			this.cbmi_settings_highlight_found_elems.setText(messages
					.getString("tray.settings.highlight_found_elems"));
			this.cbmi_settings_hide_on_execution.setText(messages
					.getString("tray.settings.hide_on_execution"));
			this.cbmi_settings_minimize_to_tray.setText(messages
					.getString("tray.settings.minimize_to_system_tray"));
			this.mi_exit.setText(messages.getString("tray.exit"));
		}
	}

}
