package pixelbot.script.parser.js.nashorn;

import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import pixelbot.log.ScriptLogLevel;
import pixelbot.script.general.ScriptManager;
import pixelbot.script.instantiator.general.IScriptInstantiator;
import pixelbot.script.parser.general.ScriptParser;

/**
 * Parses a script with Nashorn, through the standard {@code javax.script} compilation interface.
 */
public class NashornParser extends ScriptParser {

	private final ScriptEngine engine;

	public NashornParser(ScriptManager manager) {
		super(manager);
		this.engine = new ScriptEngineManager().getEngineByName("nashorn");
	}

	boolean isAvailable() {
		return this.engine instanceof Compilable;
	}

	@Override
	public List<IScriptInstantiator> compile(String name, byte[] data) {
		try {
			CompiledScript compiled = ((Compilable) this.engine).compile(new String(data,
					StandardCharsets.UTF_8));

			List<IScriptInstantiator> result = this.manager.getScriptInstantiator().getList(this,
					Arrays.asList(new Object[] { compiled }));

			this.manager.log(ScriptLogLevel.CompInfo, "Sucessfully parsed: " + name);
			return result;
		} catch (Exception e) {
			this.manager.log(ScriptLogLevel.CompError, e.toString());
			return null;
		}
	}

	@Override
	public String getType() {
		return "djs";
	}

	@Override
	public String getId() {
		return "javascript/nashorn";
	}

	@Override
	public String getName() {
		return "OpenJDK Nashorn JavaScript";
	}

	@Override
	public int getPriority() {
		return 400;
	}
}
