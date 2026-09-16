package pixelbot.script.parser.js.graal;

import pixelbot.script.general.ScriptManager;
import pixelbot.script.parser.general.IScriptParserRegistrator;

public class GraalParserRegistrator implements IScriptParserRegistrator {

	@Override
	public void register(ScriptManager manager) {
		try {
			Class.forName("org.graalvm.polyglot.Context");
			manager.getScriptParsers().addScriptProvider(new GraalParser(manager));
		} catch (Throwable e) {
			// GraalJS is optional.
		}
	}
}
