package pixelbot.script.parser.js.nashorn;

import pixelbot.script.general.ScriptManager;
import pixelbot.script.parser.general.IScriptParserRegistrator;

public class NashornParserRegistrator implements IScriptParserRegistrator {

	@Override
	public void register(ScriptManager manager) {
		try {
			Class.forName("org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory");
			NashornParser parser = new NashornParser(manager);
			if (parser.isAvailable()) {
				manager.getScriptParsers().addScriptProvider(parser);
			}
		} catch (Throwable e) {
			// Nashorn is optional.
		}
	}
}
