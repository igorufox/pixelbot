package pixelbot.script.instantiator.general;

import java.awt.event.*;
import java.util.*;

import javax.swing.JCheckBox;

import pixelbot.script.parser.general.IScriptParser;

public class ScriptInstantiatorManager implements ActionListener {
	private final Map<Class<?>, IScriptInstantiatorFactory> instantiators = new HashMap<Class<?>, IScriptInstantiatorFactory>();

	public ScriptInstantiatorManager() {}

	public void register(Class<?> clazz, IScriptInstantiatorFactory factory) {
		this.instantiators.put(clazz, factory);
	}

	public IScriptInstantiator get(IScriptParser parser, Object script) throws Exception {
		for (Map.Entry<Class<?>, IScriptInstantiatorFactory> e : this.instantiators.entrySet()) {
			if (e.getValue().isEnabled() && e.getKey().isAssignableFrom(script.getClass())) {
				return e.getValue().produce(parser.getType(), script);
			}
		}

		return null;
	}

	public List<IScriptInstantiator> getList(IScriptParser parser, Collection<Object> scripts)
			throws Exception {
		List<IScriptInstantiator> result = new ArrayList<IScriptInstantiator>();
		for (Object script : scripts) {
			IScriptInstantiator instantiator = this.get(parser, script);
			if (instantiator != null) {
				result.add(instantiator);
			}
		}
		return result;
	}

	public boolean canInstantiate(Class<?> c) {
		for (Class<?> k : this.instantiators.keySet()) {
			if (k.isAssignableFrom(c)) {
				return true;
			}
		}
		return false;
	}

	public List<IScriptInstantiatorFactory> list() {
		List<IScriptInstantiatorFactory> result = new ArrayList<IScriptInstantiatorFactory>();
		result.addAll(this.instantiators.values());
		Collections.sort(result);
		return result;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		((IScriptInstantiatorFactory) (((JCheckBox) e.getSource()).getClientProperty("value")))
				.setEnabled(((JCheckBox) e.getSource()).isSelected());
	}

}
