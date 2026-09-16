package pixelbot.script.parser.js.graal;

import java.nio.charset.StandardCharsets;
import java.util.*;

import org.graalvm.polyglot.Source;

import pixelbot.log.ScriptLogLevel;
import pixelbot.script.general.ScriptManager;
import pixelbot.script.instantiator.general.IScriptInstantiator;
import pixelbot.script.parser.general.ScriptParser;

/**
 * Parses a script with GraalJS.
 * <p>
 * Graal has no separate compile step to expose: a {@link Source} is handed to the instantiator,
 * which evaluates it in the shared context. Syntax errors still surface here, because building the
 * source and evaluating it once is what the instantiator does first.
 */
public class GraalParser extends ScriptParser {

	public GraalParser(ScriptManager manager) {
		super(manager);
	}

	@Override
	public List<IScriptInstantiator> compile(String name, byte[] data) {
		try {
			Source source = Source.newBuilder("js", new String(data, StandardCharsets.UTF_8), name)
					.buildLiteral();

			List<IScriptInstantiator> result = this.manager.getScriptInstantiator().getList(this,
					Arrays.asList(new Object[] { source }));

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
		return "javascript/graal";
	}

	@Override
	public String getName() {
		return "GraalVM JavaScript";
	}

	@Override
	public int getPriority() {
		return 300;
	}
}
