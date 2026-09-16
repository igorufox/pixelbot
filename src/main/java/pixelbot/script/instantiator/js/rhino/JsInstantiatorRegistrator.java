package pixelbot.script.instantiator.js.rhino;

import pixelbot.script.general.ScriptManager;
import pixelbot.script.instantiator.general.IScriptInstantiatorRegistrator;

public class JsInstantiatorRegistrator implements IScriptInstantiatorRegistrator {

	@Override
	public void register(ScriptManager manager) {
		try {
			manager.getScriptInstantiator().register(
					Class.forName("org.mozilla.javascript.Script"),
					new JsInstantiatorFactory(manager));
		} catch (Throwable e) {}
	}

}
