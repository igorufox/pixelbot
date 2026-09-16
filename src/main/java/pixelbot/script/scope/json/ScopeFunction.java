package pixelbot.script.scope.json;

import java.io.UnsupportedEncodingException;
import java.util.*;

import net.sf.json.JSONFunction;
import pixelbot.script.general.ScriptManager;
import pixelbot.script.scope.general.*;

public class ScopeFunction implements IScopeFunction {
	private final JSONFunction func;
	private IScopeFunction compiled;
	private final ScriptManager manager;

	public ScopeFunction(JSONFunction func, ScriptManager manager) {
		this.compiled = null;
		this.func = func;
		this.manager = manager;
	}

	@Override
	public Object getObject() {
		return this.func;
	}

	@Override
	public IScopeObject call(IScopeObject thisObj, IScopeObject... params)
			throws InterruptedException {
		if (this.compiled == null) {
			byte[] data = null;
			try {
				data = ("json=" + this.func.toString()).getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {}

			this.compiled = this.manager.getScriptParsers().get("djs").compile("json", data).get(0)
					.getFunction();
		}
		return this.compiled.call(thisObj, params);

	}

	@Override
	public Collection<String> getArgsNames() {
		return Arrays.asList(this.func.getParams());
	}

	@Override
	public String origin() {
		return "json";
	}

}
