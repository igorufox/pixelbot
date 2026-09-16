package pixelbot.gui.settings;

import java.awt.Color;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import pixelbot.gui.PixelBotView;
import pixelbot.misc.Tools;
import pixelbot.script.general.IOSettings;

public class IOSettingsPanel extends JScrollPane {
	private static final long serialVersionUID = 8861125587211226716L;

	private JPanel jPanel_mouse;
	private JPanel jPanel_execution;
	private JPanel jPanel_other;

	private JLabel jLabel_settings_mouse_moving_speed;
	private JLabel jLabel_settings_mouse_click_duration;
	private JLabel jLabel_settings_mouse_pause_after_click;
	private JLabel jLabel_settings_mouse_pause_after_positioning;
	private JLabel jLabel_settings_mouse_moving_speed_trail;
	private JLabel jLabel_settings_mouse_click_duration_trail;
	private JLabel jLabel_settings_mouse_pause_after_click_trail;
	private JLabel jLabel_settings_mouse_pause_after_positioning_trail;

	private JLabel jLabel_settings_check_user_activity;
	private JLabel jLabel_settings_highlight_found_elems;
	private JLabel jLabel_settings_hide_on_execution;
	private JLabel jLabel_settings_minimize_to_system_tray;

	private JLabel jLabel_settings_bot_color;
	private JLabel jLabel_settings_locale;

	protected JSpinner jSpinner_settings_mouse_moving_speed;
	protected JSpinner jSpinner_settings_mouse_click_duration_min;
	protected JSpinner jSpinner_settings_mouse_click_duration_max;
	protected JSpinner jSpinner_settings_mouse_pause_after_click_min;
	protected JSpinner jSpinner_settings_mouse_pause_after_click_max;
	protected JSpinner jSpinner_settings_mouse_pause_after_positioning_min;
	protected JSpinner jSpinner_settings_mouse_pause_after_positioning_max;

	protected JCheckBox jCheckBox_settings_check_user_activity;

	protected JCheckBox jCheckBox_settings_highlight_found_elems;
	protected JCheckBox jCheckBox_settings_hide_on_execution;
	protected JCheckBox jCheckBox_settings_minimize_to_system_tray;

	protected JTextField jTextField_settings_bot_color;
	protected JComboBox<String> jComboBox_settings_locale;

	protected PixelBotView main;
	private IOSettings settings;
	private JPanel panel;

	public IOSettingsPanel(PixelBotView main, IOSettings settings) {
		super(new JPanel());
		this.main = main;
		this.settings = settings;

		this.panel = new JPanel();
		this.setViewportView(this.panel);

		this.jPanel_mouse = new JPanel();
		this.jPanel_execution = new JPanel();
		this.jPanel_other = new JPanel();
		this.jPanel_mouse.setBorder(BorderFactory.createTitledBorder(""));
		this.jPanel_execution.setBorder(BorderFactory.createTitledBorder(""));
		this.jPanel_other.setBorder(BorderFactory.createTitledBorder(""));

		this.jLabel_settings_mouse_moving_speed = new JLabel();
		this.jLabel_settings_mouse_click_duration = new JLabel();
		this.jLabel_settings_mouse_pause_after_click = new JLabel();
		this.jLabel_settings_mouse_pause_after_positioning = new JLabel();
		this.jLabel_settings_mouse_moving_speed_trail = new JLabel();
		this.jLabel_settings_mouse_click_duration_trail = new JLabel();
		this.jLabel_settings_mouse_pause_after_click_trail = new JLabel();
		this.jLabel_settings_mouse_pause_after_positioning_trail = new JLabel();

		this.jLabel_settings_check_user_activity = new JLabel();
		this.jLabel_settings_highlight_found_elems = new JLabel();
		this.jLabel_settings_hide_on_execution = new JLabel();
		this.jLabel_settings_minimize_to_system_tray = new JLabel();

		this.jLabel_settings_bot_color = new JLabel();
		this.jLabel_settings_locale = new JLabel();

		this.jSpinner_settings_mouse_moving_speed = new JSpinner();
		this.jSpinner_settings_mouse_click_duration_min = new JSpinner();
		this.jSpinner_settings_mouse_click_duration_max = new JSpinner();
		this.jSpinner_settings_mouse_pause_after_click_min = new JSpinner();
		this.jSpinner_settings_mouse_pause_after_click_max = new JSpinner();
		this.jSpinner_settings_mouse_pause_after_positioning_min = new JSpinner();
		this.jSpinner_settings_mouse_pause_after_positioning_max = new JSpinner();

		this.jCheckBox_settings_check_user_activity = new JCheckBox();

		this.jCheckBox_settings_highlight_found_elems = new JCheckBox();
		this.jCheckBox_settings_hide_on_execution = new JCheckBox();
		this.jCheckBox_settings_minimize_to_system_tray = new JCheckBox();

		this.jTextField_settings_bot_color = new JTextField();
		this.jComboBox_settings_locale = new JComboBox<String>();

		this.jSpinner_settings_mouse_moving_speed
				.setModel(this.settings.getMouseMovingSpeedModel());
		this.jSpinner_settings_mouse_click_duration_min.setModel(this.settings
				.getMouseClickDurationMinModel());
		this.jSpinner_settings_mouse_click_duration_max.setModel(this.settings
				.getMouseClickDurationMaxModel());
		this.jSpinner_settings_mouse_pause_after_click_min.setModel(this.settings
				.getMousePauseAfterClickMinModel());
		this.jSpinner_settings_mouse_pause_after_click_max.setModel(this.settings
				.getMousePauseAfterClickMaxModel());
		this.jSpinner_settings_mouse_pause_after_positioning_min.setModel(this.settings
				.getMousePauseAfterPositionMinModel());
		this.jSpinner_settings_mouse_pause_after_positioning_max.setModel(this.settings
				.getMousePauseAfterPositionMaxModel());

		this.jCheckBox_settings_check_user_activity.setModel(this.settings
				.getCheckUserActivityModel());
		this.jCheckBox_settings_highlight_found_elems.setModel(this.settings
				.getHighlightElementsModel());
		this.jCheckBox_settings_hide_on_execution.setModel(this.settings.getHideOnExecutionModel());
		this.jCheckBox_settings_minimize_to_system_tray.setModel(this.settings
				.getMinimizeToSystemTrayModel());
		this.jComboBox_settings_locale.setModel(this.settings.getLocaleModel());

		this.jTextField_settings_bot_color.setEditable(false);
		this.jTextField_settings_bot_color.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Color newColor = JColorChooser.showDialog(IOSettingsPanel.this, "выбор цвета бота",
						IOSettingsPanel.this.main.getContentPane().getBackground());
				if (newColor != null)
					IOSettingsPanel.this.main.changeColor(newColor);
			}
		});

		GroupLayout gl_mouse = new GroupLayout(this.jPanel_mouse);
		this.jPanel_mouse.setLayout(gl_mouse);
		gl_mouse.setHorizontalGroup(gl_mouse
				.createSequentialGroup()
				.addGroup(
						gl_mouse.createParallelGroup()
								.addComponent(this.jLabel_settings_mouse_moving_speed, 0,
										GroupLayout.PREFERRED_SIZE, 150)
								.addComponent(this.jLabel_settings_mouse_click_duration, 0,
										GroupLayout.PREFERRED_SIZE, 150)
								.addComponent(this.jLabel_settings_mouse_pause_after_click, 0,
										GroupLayout.PREFERRED_SIZE, 150)
								.addComponent(this.jLabel_settings_mouse_pause_after_positioning,
										0, GroupLayout.PREFERRED_SIZE, 150))
				.addGroup(
						gl_mouse.createParallelGroup()
								.addGroup(
										gl_mouse.createSequentialGroup()
												.addComponent(
														this.jSpinner_settings_mouse_moving_speed,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														this.jLabel_settings_mouse_moving_speed_trail))
								.addGroup(
										gl_mouse.createSequentialGroup()
												.addComponent(
														this.jSpinner_settings_mouse_click_duration_min,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														this.jSpinner_settings_mouse_click_duration_max,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														this.jLabel_settings_mouse_click_duration_trail))
								.addGroup(
										gl_mouse.createSequentialGroup()
												.addComponent(
														this.jSpinner_settings_mouse_pause_after_click_min,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														this.jSpinner_settings_mouse_pause_after_click_max,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														this.jLabel_settings_mouse_pause_after_click_trail))
								.addGroup(
										gl_mouse.createSequentialGroup()
												.addComponent(
														this.jSpinner_settings_mouse_pause_after_positioning_min,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														this.jSpinner_settings_mouse_pause_after_positioning_max,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														this.jLabel_settings_mouse_pause_after_positioning_trail))));

		gl_mouse.setVerticalGroup(gl_mouse
				.createSequentialGroup()
				.addGroup(
						gl_mouse.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.jLabel_settings_mouse_moving_speed)
								.addComponent(this.jSpinner_settings_mouse_moving_speed,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(this.jLabel_settings_mouse_moving_speed_trail))
				.addGroup(
						gl_mouse.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.jLabel_settings_mouse_click_duration)
								.addComponent(this.jSpinner_settings_mouse_click_duration_min,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(this.jSpinner_settings_mouse_click_duration_max,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(this.jLabel_settings_mouse_click_duration_trail))
				.addGroup(
						gl_mouse.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.jLabel_settings_mouse_pause_after_click)
								.addComponent(this.jSpinner_settings_mouse_pause_after_click_min,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(this.jSpinner_settings_mouse_pause_after_click_max,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(this.jLabel_settings_mouse_pause_after_click_trail))
				.addGroup(
						gl_mouse.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.jLabel_settings_mouse_pause_after_positioning)
								.addComponent(
										this.jSpinner_settings_mouse_pause_after_positioning_min,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(
										this.jSpinner_settings_mouse_pause_after_positioning_max,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(
										this.jLabel_settings_mouse_pause_after_positioning_trail)));

		GroupLayout gl_execution = new GroupLayout(this.jPanel_execution);
		this.jPanel_execution.setLayout(gl_execution);
		gl_execution.setHorizontalGroup(gl_execution
				.createSequentialGroup()
				.addGroup(
						gl_execution
								.createParallelGroup()
								.addComponent(this.jLabel_settings_check_user_activity, 0,
										GroupLayout.PREFERRED_SIZE, 150)
								.addComponent(this.jLabel_settings_highlight_found_elems, 0,
										GroupLayout.PREFERRED_SIZE, 150)
								.addComponent(this.jLabel_settings_hide_on_execution, 0,
										GroupLayout.PREFERRED_SIZE, 150)
								.addComponent(this.jLabel_settings_minimize_to_system_tray, 0,
										GroupLayout.PREFERRED_SIZE, 150))
				.addGroup(
						gl_execution.createParallelGroup()
								.addComponent(this.jCheckBox_settings_check_user_activity)
								.addComponent(this.jCheckBox_settings_highlight_found_elems)
								.addComponent(this.jCheckBox_settings_hide_on_execution)
								.addComponent(this.jCheckBox_settings_minimize_to_system_tray)));

		gl_execution.setVerticalGroup(gl_execution
				.createSequentialGroup()
				.addGroup(
						gl_execution.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.jLabel_settings_check_user_activity)
								.addComponent(this.jCheckBox_settings_check_user_activity))
				.addGroup(
						gl_execution.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.jLabel_settings_highlight_found_elems)
								.addComponent(this.jCheckBox_settings_highlight_found_elems))
				.addGroup(
						gl_execution.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.jLabel_settings_hide_on_execution)
								.addComponent(this.jCheckBox_settings_hide_on_execution))
				.addGroup(
						gl_execution.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.jLabel_settings_minimize_to_system_tray)
								.addComponent(this.jCheckBox_settings_minimize_to_system_tray)));

		GroupLayout gl_other = new GroupLayout(this.jPanel_other);
		this.jPanel_other.setLayout(gl_other);
		gl_other.setHorizontalGroup(gl_other
				.createSequentialGroup()
				.addGroup(
						gl_other.createParallelGroup()
								.addComponent(this.jLabel_settings_bot_color, 0,
										GroupLayout.PREFERRED_SIZE, 150)
								.addComponent(this.jLabel_settings_locale, 0,
										GroupLayout.PREFERRED_SIZE, 150))
				.addGroup(
						gl_other.createParallelGroup()
								.addComponent(this.jTextField_settings_bot_color)
								.addComponent(this.jComboBox_settings_locale)));

		gl_other.setVerticalGroup(gl_other
				.createSequentialGroup()
				.addGroup(
						gl_other.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.jLabel_settings_bot_color)
								.addComponent(this.jTextField_settings_bot_color,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
				.addGroup(
						gl_other.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.jLabel_settings_locale)
								.addComponent(this.jComboBox_settings_locale,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)));

		GroupLayout gl = new GroupLayout(this.panel);
		this.panel.setLayout(gl);
		gl.setHorizontalGroup(gl.createParallelGroup().addComponent(this.jPanel_mouse)
				.addComponent(this.jPanel_execution).addComponent(this.jPanel_other));
		gl.setVerticalGroup(gl.createSequentialGroup().addComponent(this.jPanel_mouse)
				.addComponent(this.jPanel_execution).addComponent(this.jPanel_other));
	}

	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);

		ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, l);

		((TitledBorder) this.jPanel_mouse.getBorder()).setTitle(messages
				.getString("tuning.io.mouse"));
		((TitledBorder) this.jPanel_execution.getBorder()).setTitle(messages
				.getString("tuning.io.execution"));
		((TitledBorder) this.jPanel_other.getBorder()).setTitle(messages
				.getString("tuning.io.other"));

		this.jLabel_settings_mouse_moving_speed.setText("<html><p>"
				+ messages.getString("tuning.io.mouse_moving_speed") + "</p></html>");
		this.jLabel_settings_mouse_click_duration.setText("<html><p>"
				+ messages.getString("tuning.io.mouse_click_duration") + "</p></html>");
		this.jLabel_settings_mouse_pause_after_click.setText("<html><p>"
				+ messages.getString("tuning.io.mouse_pause_after_click") + "</p></html>");
		this.jLabel_settings_mouse_pause_after_positioning.setText("<html><p>"
				+ messages.getString("tuning.io.mouse_pause_after_positioning") + "</p></html>");
		this.jLabel_settings_bot_color.setText("<html><p>"
				+ messages.getString("tuning.io.bot_color") + "</p></html>");
		this.jLabel_settings_check_user_activity.setText("<html><p>"
				+ messages.getString("tuning.io.check_user_activity") + "</p></html>");
		this.jLabel_settings_highlight_found_elems.setText("<html><p>"
				+ messages.getString("tuning.io.highlight_found_elems") + "</p></html>");
		this.jLabel_settings_hide_on_execution.setText("<html><p>"
				+ messages.getString("tuning.io.hide_on_execution") + "</p></html>");
		this.jLabel_settings_minimize_to_system_tray.setText("<html><p>"
				+ messages.getString("tuning.io.minimize_to_system_tray") + "</p></html>");
		this.jLabel_settings_locale.setText("<html><p>" + messages.getString("tuning.io.locale")
				+ "</p></html>");

		this.jLabel_settings_mouse_moving_speed_trail.setText("<html><p>"
				+ messages.getString("tuning.io.fast_slow") + "</p></html>");
		this.jLabel_settings_mouse_click_duration_trail.setText(messages.getString("tuning.io.ms"));
		this.jLabel_settings_mouse_pause_after_click_trail.setText(messages
				.getString("tuning.io.ms"));
		this.jLabel_settings_mouse_pause_after_positioning_trail.setText(messages
				.getString("tuning.io.ms"));
	}

	public void setBotColor(Color color) {
		this.jTextField_settings_bot_color.setText(Integer.toHexString(color.getRGB())
				.toUpperCase());
	}
}
