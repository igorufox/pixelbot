package pixelbot.gui.task;

import java.awt.Color;
import java.awt.event.*;
import java.util.*;
import java.util.prefs.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.text.JTextComponent;

import pixelbot.components.JStatisticsTree;
import pixelbot.components.generic.JListSelector.Source;
import pixelbot.components.generic.*;
import pixelbot.components.generic.JRadioGroup.Layout;
import pixelbot.components.logviewer.*;
import pixelbot.gui.task.ParametersPanel.ParametersType;
import pixelbot.gui.task.ParametersPanel.ScriptParamsChangeListener;
import pixelbot.misc.Tools;
import pixelbot.script.general.*;
import pixelbot.script.general.ScriptManager.ScriptEvent;
import pixelbot.script.general.ScriptManager.ScriptParameters;
import pixelbot.script.general.ScriptManager.ScriptStateListener;
import pixelbot.script.general.ScriptManager.StatisticsEvent;
import pixelbot.script.general.ScriptManager.StatisticsListener;
import pixelbot.script.misc.JSONScriptValue;
import pixelbot.script.scope.general.*;

public class TaskPanel extends JPanel {
	private static final long serialVersionUID = -1597451992824472808L;

	private Preferences prefs;
	private ScriptManager script;

	private JPanel jPanel_job_top;

	private JPanel jPanel_job_task;
	protected JRadioGroup<String> jRadioGroup_do_job;
	protected JComboBox<ScopeJob> jComboBox_tasks;

	private JLabel jLabel_health;
	protected JProgressBar jProgressBar_health;
	private JLabel jLabel_mana;
	protected JProgressBar jProgressBar_mana;

	protected JStatisticsTree jStatisticsTree_statistics;

	protected JTabbedPane jTabbedPane_job_bottom;

	private JLogArea jLogArea_job_log;
	private JPanel jPanel_job_parameters;

	protected JComboBox<String> jComboBox_job_presets;
	private JButton jButton_job_presets_load;
	private JButton jButton_job_presets_save;
	private JButton jButton_job_presets_delete;
	private ParametersPanel jPanel_job_parameters_values;

	public TaskPanel(ScriptManager script, Preferences prefs) {
		super();
		this.script = script;
		this.prefs = prefs;

		this.script.addStateListener(new ScriptStateListener() {
			@Override
			public void stateChanged(ScriptEvent event) {
				switch (event.state) {
				case job_run:
					TaskPanel.this.jRadioGroup_do_job.setSelectedIndex(0);
					break;
				case job_debug:
					TaskPanel.this.jRadioGroup_do_job.setSelectedIndex(0);
					break;
				case job_done:
					TaskPanel.this.jRadioGroup_do_job.setSelectedIndex(1);
					break;
				case jobs_changed:
					TaskPanel.this.updateJobs();
					break;
				}
			}
		});

		this.jPanel_job_top = new JPanel();

		this.jPanel_job_task = new JPanel();
		this.jRadioGroup_do_job = new JRadioGroup<String>(
				Arrays.asList(new String[] { "on", "off" }));

		this.jComboBox_tasks = new JComboBox<ScopeJob>(new SortedComboBoxModel<ScopeJob>());

		this.jLabel_health = new JLabel();
		this.jProgressBar_health = new JProgressBar();
		this.jLabel_mana = new JLabel();
		this.jProgressBar_mana = new JProgressBar();

		this.jStatisticsTree_statistics = new JStatisticsTree();
		this.jTabbedPane_job_bottom = new JTabbedPane();

		this.jLogArea_job_log = new JLogArea(new LoggerModel());
		this.jPanel_job_parameters = new JPanel();

		this.jComboBox_job_presets = new JComboBox<String>(new SortedComboBoxModel<String>());
		this.jButton_job_presets_load = new JButton();
		this.jButton_job_presets_save = new JButton();
		this.jButton_job_presets_delete = new JButton();

		this.jPanel_job_parameters_values = new ParametersPanel(script);

		GroupLayout gl = new GroupLayout(this);
		this.setLayout(gl);
		gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(this.jPanel_job_top).addComponent(this.jTabbedPane_job_bottom));

		gl.setVerticalGroup(gl.createSequentialGroup()
				.addComponent(this.jPanel_job_top, -2, 250, -2)
				.addComponent(this.jTabbedPane_job_bottom));

		GroupLayout gl_jPanel_job_top = new GroupLayout(this.jPanel_job_top);
		this.jPanel_job_top.setLayout(gl_jPanel_job_top);
		gl_jPanel_job_top.setHorizontalGroup(gl_jPanel_job_top
				.createParallelGroup()
				.addComponent(this.jPanel_job_task)
				.addGroup(
						gl_jPanel_job_top.createSequentialGroup()
								.addComponent(this.jLabel_health, 100, 100, 100)
								.addComponent(this.jProgressBar_health, 0, 0, 32767))
				.addGroup(
						gl_jPanel_job_top.createSequentialGroup()
								.addComponent(this.jLabel_mana, 100, 100, 100)
								.addComponent(this.jProgressBar_mana, 0, 0, 32767))
				.addComponent(this.jStatisticsTree_statistics));

		gl_jPanel_job_top.setVerticalGroup(gl_jPanel_job_top
				.createSequentialGroup()
				.addComponent(this.jPanel_job_task)
				.addGroup(
						gl_jPanel_job_top.createParallelGroup()
								.addComponent(this.jLabel_health, 18, 18, 18)
								.addComponent(this.jProgressBar_health))
				.addGroup(
						gl_jPanel_job_top.createParallelGroup()
								.addComponent(this.jLabel_mana, 18, 18, 18)
								.addComponent(this.jProgressBar_mana))
				.addComponent(this.jStatisticsTree_statistics));

		this.jProgressBar_health.setForeground(new Color(255, 0, 0));
		this.jProgressBar_health.setStringPainted(true);
		this.jProgressBar_mana.setForeground(new Color(0, 0, 255));
		this.jProgressBar_mana.setStringPainted(true);

		// this.jPanel_job_top.setPreferredSize(new Dimension(300, 111));
		this.jPanel_job_task.setBorder(BorderFactory.createTitledBorder("Current job"));

		GroupLayout gl_jPanel_current_task = new GroupLayout(this.jPanel_job_task);
		this.jPanel_job_task.setLayout(gl_jPanel_current_task);
		gl_jPanel_current_task.setHorizontalGroup(gl_jPanel_current_task
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						gl_jPanel_current_task.createSequentialGroup().addComponent(
								this.jRadioGroup_do_job))
				.addComponent(this.jComboBox_tasks, 0, 191, 32767));

		gl_jPanel_current_task.setVerticalGroup(gl_jPanel_current_task
				.createSequentialGroup()
				.addGroup(
						gl_jPanel_current_task.createParallelGroup().addComponent(
								this.jRadioGroup_do_job)).addComponent(this.jComboBox_tasks));

		this.jRadioGroup_do_job.setSelectedIndex(1);
		this.jRadioGroup_do_job.setLayout(Layout.Horizontal);

		this.jRadioGroup_do_job.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (TaskPanel.this.jRadioGroup_do_job.getSelectedIndex() == 0) {
					TaskPanel.this.startWork(false);
				} else {
					TaskPanel.this.stopWork();
				}
			}
		});
		this.jRadioGroup_do_job.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (((JRadioButton) e.getItem()).getClientProperty("value").equals("on")) {
						TaskPanel.this.jStatisticsTree_statistics.restart();
						TaskPanel.this.jTabbedPane_job_bottom.setSelectedIndex(0);
					} else {
						TaskPanel.this.jTabbedPane_job_bottom.setSelectedIndex(1);
					}
				}
			}
		});

		this.jTabbedPane_job_bottom.addTab("Log", this.jLogArea_job_log);
		this.jTabbedPane_job_bottom.addTab("Params", this.jPanel_job_parameters);

		GroupLayout gl_jPanel_job_params = new GroupLayout(this.jPanel_job_parameters);
		this.jPanel_job_parameters.setLayout(gl_jPanel_job_params);
		gl_jPanel_job_params.setHorizontalGroup(gl_jPanel_job_params
				.createParallelGroup()
				.addGroup(
						gl_jPanel_job_params.createSequentialGroup()
								.addComponent(this.jComboBox_job_presets, 0, 0, 32000)
								.addComponent(this.jButton_job_presets_load, -2, -1, -2)
								.addComponent(this.jButton_job_presets_save, -2, -1, -2)
								.addComponent(this.jButton_job_presets_delete, -2, -1, -2))
				.addComponent(this.jPanel_job_parameters_values));

		gl_jPanel_job_params.setVerticalGroup(gl_jPanel_job_params
				.createSequentialGroup()
				.addGroup(
						gl_jPanel_job_params.createParallelGroup()
								.addComponent(this.jComboBox_job_presets, -2, -1, -2)
								.addComponent(this.jButton_job_presets_load, -2, -1, -2)
								.addComponent(this.jButton_job_presets_save, -2, -1, -2)
								.addComponent(this.jButton_job_presets_delete, -2, -1, -2))
				.addComponent(this.jPanel_job_parameters_values));

		this.jComboBox_job_presets.setEditable(true);

		this.jButton_job_presets_load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (TaskPanel.this.jComboBox_job_presets.getSelectedItem() != null) {
					ScopeJob job = (ScopeJob) TaskPanel.this.jComboBox_tasks.getSelectedItem();
					loadConfiguration(job, TaskPanel.this.jComboBox_job_presets.getSelectedItem()
							.toString());
				}
			}
		});

		this.jButton_job_presets_save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String leaf = "";
				if (TaskPanel.this.jComboBox_job_presets.getSelectedIndex() == -1) {
					leaf = ((JTextComponent) TaskPanel.this.jComboBox_job_presets.getEditor()
							.getEditorComponent()).getText();
					if (leaf != null && leaf.length() != 0) {
						TaskPanel.this.jComboBox_job_presets.addItem(leaf);
					}
				} else {
					leaf = TaskPanel.this.jComboBox_job_presets.getSelectedItem().toString();
				}
				if (leaf != null && leaf.length() != 0) {
					ScopeJob job = (ScopeJob) TaskPanel.this.jComboBox_tasks.getSelectedItem();
					saveConfiguration(job.getId(), leaf);
				}
			}
		});

		this.jButton_job_presets_delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (TaskPanel.this.jComboBox_job_presets.getSelectedItem() != null) {
					ScopeJob job = (ScopeJob) TaskPanel.this.jComboBox_tasks.getSelectedItem();
					TaskPanel.this.deleteConfiguration(job.getId(),
							TaskPanel.this.jComboBox_job_presets.getSelectedItem().toString());
					TaskPanel.this.jComboBox_job_presets
							.removeItem((TaskPanel.this.jComboBox_job_presets.getSelectedItem()));
				}
			}
		});

		this.jPanel_job_parameters_values.setScriptChangeListener(new ScriptParamsChangeListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				boolean load = true;
				if (event.getSource() instanceof Source
						&& (Source) event.getSource() == Source.Left) {
					load = false;
				}
				this.action(load);
			}

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					this.action(true);
				}
			}

			@Override
			public void stateChanged(ChangeEvent e) {
				if (!(e.getSource() instanceof JSlider)) {
					this.action(true);
				} else {
					this.action(false);
				}
			}

			private void action(boolean load) {
				ScopeJob job = TaskPanel.this.getActiveJob();
				if (job != null) {
					saveConfiguration(job.getId(), "default");
					if (load) {
						loadConfiguration(job, "default");
					}
				}
			}

		});

		this.script.addStatisticsListener(new StatisticsListener() {

			@Override
			public void log(StatisticsEvent event) {
				switch (event.getDest()) {
				case state:
					try {
						@SuppressWarnings("unchecked") List<Number> data = (List<Number>) event
								.getData();

						TaskPanel.this.jProgressBar_health.setMaximum(data.get(1).intValue());
						TaskPanel.this.jProgressBar_health.setValue(data.get(0).intValue());
						TaskPanel.this.jProgressBar_health.setString(data.get(0).intValue() + "/"
								+ data.get(1).intValue());

						TaskPanel.this.jProgressBar_mana.setMaximum(data.get(3).intValue());
						TaskPanel.this.jProgressBar_mana.setValue(data.get(2).intValue());
						TaskPanel.this.jProgressBar_mana.setString(data.get(2).intValue() + "/"
								+ data.get(3).intValue());
					} catch (Exception e) {}
					break;
				case statistics:
					TaskPanel.this.jStatisticsTree_statistics.addStatistics(event.getData()
							.toString(), event.getValue());
					break;
				default:
					break;
				}
			}
		});

		this.jComboBox_tasks.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				ScopeJob job = (ScopeJob) event.getItem();
				if (event.getStateChange() == ItemEvent.DESELECTED) {
					saveConfiguration(job.getId(), "default");
					TaskPanel.this.stopWork();
				} else if (event.getStateChange() == ItemEvent.SELECTED) {
					loadConfiguration(job, "default");

					TaskPanel.this.jComboBox_job_presets.removeAllItems();
					for (String item : listConfiguration(job.getId())) {
						if (!"default".equals(item)) {
							TaskPanel.this.jComboBox_job_presets.addItem(item);
						}
					}

				}
			}
		});

	}

	protected void loadConfiguration(ScopeJob job, String leaf) {
		try {
			ScriptParameters params = this.script.decodeParams(
					job,
					this.prefs.node("script.params." + job.getId().replace(".", "#")).get(leaf,
							null));
			this.jPanel_job_parameters_values.fillPanel(params.config, params.values);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private IScopeMap getParams(ParametersType type) {
		return this.jPanel_job_parameters_values.getParams(type);
	}

	protected void saveConfiguration(String node, String leaf) {
		this.prefs.node("script.params." + node.replace(".", "#")).put(leaf,
				JSONScriptValue.toJSONString(this.getParams(ParametersType.Config)));
	}

	protected void deleteConfiguration(String node, String leaf) {
		this.prefs.node("script.params." + node.replace(".", "#")).remove(leaf);
	}

	protected String[] listConfiguration(String node) {
		try {
			return this.prefs.node("script.params." + node.replace(".", "#")).keys();
		} catch (BackingStoreException e) {
			return new String[] {};
		}
	}

	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);
		ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, l);

		((TitledBorder) this.jPanel_job_task.getBorder()).setTitle(messages
				.getString("control.job.current"));
		this.jRadioGroup_do_job.setTexts(new String[] { messages.getString("control.job.on"),
				messages.getString("control.job.off") });
		this.jLabel_health.setText(messages.getString("control.top.health"));
		this.jLabel_mana.setText(messages.getString("control.top.mana"));

		this.jTabbedPane_job_bottom.setTitleAt(0, messages.getString("control.log.title"));
		this.jTabbedPane_job_bottom.setTitleAt(1, messages.getString("control.params.title"));

		this.jButton_job_presets_load.setText(messages.getString("control.params.presets.load"));
		this.jButton_job_presets_save.setText(messages.getString("control.params.presets.save"));
		this.jButton_job_presets_delete
				.setText(messages.getString("control.params.presets.delete"));

		this.jStatisticsTree_statistics.setLocale(l);

		this.jLogArea_job_log.setLocale(l);
		this.jPanel_job_parameters_values.setLocale(l);
	}

	protected void updateJobs() {
		ScopeJob s_main = (ScopeJob) this.jComboBox_tasks.getSelectedItem();
		this.jComboBox_tasks.removeAllItems();
		for (ScopeJob job : this.script.getJobs()) {
			this.jComboBox_tasks.addItem(job);
		}
		if (s_main != null) {
			this.setActiveJob(s_main.getId());
		}

	}

	public void setActiveJob(String job) {
		for (int i = 0; i < this.jComboBox_tasks.getItemCount(); ++i) {
			if (this.jComboBox_tasks.getItemAt(i).getId().equals(job)) {
				this.jComboBox_tasks.setSelectedIndex(i);
				break;
			}
		}

	}

	public ScopeJob getActiveJob() {
		return (ScopeJob) this.jComboBox_tasks.getSelectedItem();
	}

	public void startWork(boolean debug) {
		ScopeJob job = this.getActiveJob();
		this.script.startWork(job, this.getParams(ParametersType.Run), debug);
	}

	public void stopWork() {
		this.script.stopWork();
	}

}
