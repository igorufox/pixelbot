package pixelbot.script.instantiator.js.nashorn;

import pixelbot.script.general.ScriptManager;
import pixelbot.script.instantiator.general.IScriptInstantiatorRegistrator;

public class NashornInstantiatorRegistrator implements IScriptInstantiatorRegistrator {

	@Override
	public void register(ScriptManager manager) {
		try {
			manager.getScriptInstantiator().register(Class.forName("javax.script.CompiledScript"),
					new NashornInstantiatorFactory(manager));
		} catch (Throwable e) {
			// Nashorn is optional: without it this backend simply does not appear.
		}
	}
}
