package pixelbot.script.parser.js.rhino;

import java.util.*;

import org.mozilla.javascript.*;

import pixelbot.log.ScriptLogLevel;
import pixelbot.script.general.ScriptManager;
import pixelbot.script.instantiator.general.IScriptInstantiator;
import pixelbot.script.parser.general.ScriptParser;

public class JsParser extends ScriptParser {
	private boolean debug = false;

	public JsParser(ScriptManager manager, boolean debug) {
		super(manager);
		this.debug = debug;
	}

	@Override
	public List<IScriptInstantiator> compile(String name, byte[] data) {
		List<IScriptInstantiator> result = null;

		try {
			Context cx = Context.enter();
			if (this.debug) {
				cx.setOptimizationLevel(-1);
				cx.setGeneratingDebug(true);
				// cx.setDebugger(this.debuger, null);
			} else {
				cx.setOptimizationLevel(9);
				cx.setGeneratingDebug(false);
				// cx.setDebugger(null, null);
			}
			final Script script = cx.compileString(new String(data, "UTF-8"), name, 1, null);
			result = JsParser.this.manager.getScriptInstantiator().getList(this,
					Arrays.asList(new Object[] { script }));

			this.manager.log(ScriptLogLevel.CompInfo, "Sucessfully parsed: " + name);
		} catch (Exception e) {
			e.printStackTrace();
			this.manager.log(ScriptLogLevel.CompError, e.toString());
		} finally {
			Context.exit();
		}
		return result;
	}

	@Override
	public String getType() {
		return "djs";
	}

	@Override
	public String getId() {
		return this.debug ? "javascript/rhino/debug" : "javascript/rhino/compile";
	}

	@Override
	public String getName() {
		return this.debug ? "Mozilla Rhino JavaScript in debug mode"
				: "Mozilla Rhino JavaScript in compilation mode";
	}

	@Override
	public int getPriority() {
		return this.debug ? 100 : 200;
	}

}
