package pixelbot.script.parser.jar;

import pixelbot.script.general.ScriptManager;
import pixelbot.script.parser.zip.ZipParser;

public class JarParser extends ZipParser {

	public JarParser(ScriptManager manager) {
		super(manager);
	}

	@Override
	public String getType() {
		return "jar";
	}

	@Override
	public String getId() {
		return "jar/generic";
	}

	@Override
	public String getName() {
		return "JAR files parser";
	}

	@Override
	public int getPriority() {
		return 60;
	}

}
