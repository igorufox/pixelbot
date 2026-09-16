package pixelbot.script.debuger.js.rhino;

import pixelbot.script.debuger.general.*;
import pixelbot.script.general.ScriptManager;

public class JsDebugerRegistrator implements IScriptDebugerRegistrator {

	@Override
	public void register(ScriptManager manager) {
		try {
			manager.getScriptDebugger().register(
					(DebugHandler) Class.forName("pixelbot.script.debuger.js.rhino.JsDebugger")
							.getConstructor(ScriptDebugerManager.class)
							.newInstance(manager.getScriptDebugger()));
		} catch (Throwable e) {}
	}

}
