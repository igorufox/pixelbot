package pixelbot.gui.settings;

import java.awt.Component;
import java.util.*;
import java.util.logging.Level;

import javax.swing.*;
import javax.swing.GroupLayout.Group;
import javax.swing.border.TitledBorder;

import pixelbot.components.generic.*;
import pixelbot.misc.Tools;
import pixelbot.script.debuger.general.DebugHandler;
import pixelbot.script.general.ScriptManager;
import pixelbot.script.instantiator.general.IScriptInstantiatorFactory;
import pixelbot.script.parser.general.*;
import pixelbot.script.parser.general.ScriptParserManager.ScriptParserList;

public class InterpreterPanel extends JPanel {
	private static final long serialVersionUID = -1599121007001436322L;

	private JPanel jPanel_interpreter_parser;
	private JPanel jPanel_interpreter_instantiator;
	private JPanel jPanel_interpreter_debugger;

	private ScriptManager script;

	public InterpreterPanel(ScriptManager script) {
		super();
		this.script = script;

		this.jPanel_interpreter_parser = new JPanel();
		this.jPanel_interpreter_instantiator = new JPanel();
		this.jPanel_interpreter_debugger = new JPanel();

		GroupLayout gl = new GroupLayout(this);
		this.setLayout(gl);
		gl.setHorizontalGroup(gl.createParallelGroup()
				.addComponent(this.jPanel_interpreter_parser, 0, 0, Short.MAX_VALUE)
				.addComponent(this.jPanel_interpreter_instantiator, 0, 0, Short.MAX_VALUE)
				.addComponent(this.jPanel_interpreter_debugger, 0, 0, Short.MAX_VALUE));
		gl.setVerticalGroup(gl.createSequentialGroup().addComponent(this.jPanel_interpreter_parser)
				.addComponent(this.jPanel_interpreter_instantiator)
				.addComponent(this.jPanel_interpreter_debugger));
		this.jPanel_interpreter_parser.setBorder(BorderFactory.createTitledBorder("Parser"));
		this.jPanel_interpreter_instantiator.setBorder(BorderFactory
				.createTitledBorder("Instantiator"));
		this.jPanel_interpreter_debugger.setBorder(BorderFactory.createTitledBorder("Debugger"));
		this.jPanel_interpreter_parser.setLayout(new GroupLayout(this.jPanel_interpreter_parser));
		this.jPanel_interpreter_instantiator.setLayout(new GroupLayout(
				this.jPanel_interpreter_instantiator));

		this.jPanel_interpreter_debugger
				.setLayout(new GroupLayout(this.jPanel_interpreter_debugger));
	}

	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);

		ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, l);

		((TitledBorder) this.jPanel_interpreter_parser.getBorder()).setTitle(messages
				.getString("tuning.interpreters.parsers"));
		((TitledBorder) this.jPanel_interpreter_instantiator.getBorder()).setTitle(messages
				.getString("tuning.interpreters.instantiators"));
		((TitledBorder) this.jPanel_interpreter_debugger.getBorder()).setTitle(messages
				.getString("tuning.interpreters.debuggers"));
	}

	public void setInterpretersPrefs(Map<String, Object> params) {
		@SuppressWarnings("unchecked")
		Map<String, Object> params_parsers = params.containsKey("parser")
				? (Map<String, Object>) params.get("parser") : new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		Map<String, Object> params_instantiators = params.containsKey("instantiator")
				? (Map<String, Object>) params.get("instantiator") : new HashMap<String, Object>();

		GroupLayout layout = (GroupLayout) this.jPanel_interpreter_parser.getLayout();

		Group h_group = layout.createSequentialGroup();
		Group h_group_l = layout.createParallelGroup();
		Group h_group_r = layout.createParallelGroup();
		h_group.addGroup(h_group_l).addGroup(h_group_r);

		Group v_group = layout.createSequentialGroup();

		JLabel label = null;
		JComboBox<IScriptParser> field = null;

		for (Map.Entry<String, ScriptParserList> row : this.script.getScriptParsers()) {
			try {

				label = new JLabel();
				label.setText("<html><p>" + row.getKey() + "</p></html>");

				field = new JComboBox<IScriptParser>();
				field.setModel(new SortedListModel<IScriptParser>(row.getValue().asList()));

				((JComboBox<?>) field).addItemListener(row.getValue());
				try {
					field.setSelectedItem(row.getValue().find(
							(String) params_parsers.get(row.getKey())));
				} catch (Exception e) {
					e.printStackTrace();
				}

				field.setName(row.getKey());

				h_group_l.addComponent(label);
				h_group_r.addComponent(field);

				v_group.addGroup(layout
						.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(label)
						.addComponent(field, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE));

			} catch (Exception e) {
				this.script.log(Level.SEVERE, e.toString());
			}

		}
		layout.setHorizontalGroup(h_group);
		layout.setVerticalGroup(v_group);
		this.jPanel_interpreter_parser.repaint();

		// ------------
		JCheckGroup<IScriptInstantiatorFactory> instantiator_field = new JCheckGroup<IScriptInstantiatorFactory>();

		layout = (GroupLayout) this.jPanel_interpreter_instantiator.getLayout();

		h_group = layout.createSequentialGroup();
		v_group = layout.createSequentialGroup();

		h_group.addComponent(instantiator_field);
		v_group.addComponent(instantiator_field);

		List<IScriptInstantiatorFactory> instantiators = this.script.getScriptInstantiator().list();
		instantiator_field.setItems(instantiators);
		Iterator<IScriptInstantiatorFactory> iter = instantiators.iterator();
		while (iter.hasNext()) {
			IScriptInstantiatorFactory f = iter.next();
			Object o = params_instantiators.get(f.getId());
			if (o != null && "false".equals(o.toString())) {
				iter.remove();
			}
		}

		instantiator_field.setValue(instantiators);
		instantiator_field.addActionListener(this.script.getScriptInstantiator());

		layout.setHorizontalGroup(h_group);
		layout.setVerticalGroup(v_group);
		this.jPanel_interpreter_instantiator.repaint();

		// ------------
		JRadioGroup<DebugHandler> debugger_field = new JRadioGroup<DebugHandler>();

		layout = (GroupLayout) this.jPanel_interpreter_debugger.getLayout();

		h_group = layout.createSequentialGroup();
		v_group = layout.createSequentialGroup();

		h_group.addComponent(debugger_field);
		v_group.addComponent(debugger_field);

		List<DebugHandler> debuggers = this.script.getScriptDebugger().list();
		debugger_field.setItems(debuggers);
		debugger_field.setSelectedValue(params.get("debugger"));
		debugger_field.addActionListener(this.script.getScriptDebugger());

		layout.setHorizontalGroup(h_group);
		layout.setVerticalGroup(v_group);
		this.jPanel_interpreter_debugger.repaint();

	}

	public Map<String, Object> getInterpretersPrefs() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Object> params_parsers = new HashMap<String, Object>();
		HashMap<String, Object> params_instantiators = new HashMap<String, Object>();

		params.put("parser", params_parsers);
		params.put("instantiator", params_instantiators);

		for (Component child : this.jPanel_interpreter_parser.getComponents()) {
			if (child instanceof JComboBox) {
				params_parsers.put(child.getName(),
						((IScriptParser) ((JComboBox<?>) child).getSelectedItem()).getId());
			}
		}

		for (IScriptInstantiatorFactory instantiator : this.script.getScriptInstantiator().list()) {
			params_instantiators.put(instantiator.getId(),
					Boolean.valueOf(instantiator.isEnabled()));
		}

		params.put("debugger", this.script.getScriptDebugger().getActive().getId());

		return params;
	}
}
