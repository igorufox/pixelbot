package pixelbot.script.parser.clazz;

import pixelbot.script.general.ScriptManager;
import pixelbot.script.parser.general.IScriptParserRegistrator;

public class ClassParserRegistator implements IScriptParserRegistrator {

	@Override
	public void register(ScriptManager manager) {
		manager.getScriptParsers().addScriptProvider(new ClassParser(manager));
	}
}
