package pixelbot.script.instantiator.js.graal;

import pixelbot.script.general.ScriptManager;
import pixelbot.script.instantiator.general.IScriptInstantiatorRegistrator;

public class GraalInstantiatorRegistrator implements IScriptInstantiatorRegistrator {

	@Override
	public void register(ScriptManager manager) {
		try {
			manager.getScriptInstantiator().register(
					Class.forName("org.graalvm.polyglot.Source"),
					new GraalInstantiatorFactory(manager));
		} catch (Throwable e) {
			// GraalJS is optional: without it on the classpath this backend simply does not appear.
		}
	}
}
