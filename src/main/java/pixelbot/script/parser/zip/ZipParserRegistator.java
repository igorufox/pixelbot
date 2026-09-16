package pixelbot.script.parser.zip;

import pixelbot.script.general.ScriptManager;
import pixelbot.script.parser.general.IScriptParserRegistrator;

public class ZipParserRegistator implements IScriptParserRegistrator {

	@Override
	public void register(ScriptManager manager) {
		manager.getScriptParsers().addScriptProvider(new ZipParser(manager));
	}
}
