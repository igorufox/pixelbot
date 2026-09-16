package pixelbot.script.parser.js.rhino;

import pixelbot.script.general.*;
import pixelbot.script.parser.general.IScriptParserRegistrator;

public class JsParserRegistrator implements IScriptParserRegistrator {

	@Override
	public void register(ScriptManager manager) {
		try {
			Class.forName("org.mozilla.javascript.Scriptable");
			manager.getScriptParsers().addScriptProvider(new JsParser(manager, true));
			manager.getScriptParsers().addScriptProvider(new JsParser(manager, false));
		} catch (Exception e) {}
	}

}
