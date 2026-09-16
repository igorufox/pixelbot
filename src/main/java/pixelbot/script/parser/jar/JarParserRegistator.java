package pixelbot.script.parser.jar;

import pixelbot.script.general.ScriptManager;
import pixelbot.script.parser.general.IScriptParserRegistrator;

public class JarParserRegistator implements IScriptParserRegistrator {

	@Override
	public void register(ScriptManager manager) {
		manager.getScriptParsers().addScriptProvider(new JarParser(manager));
	}
}
