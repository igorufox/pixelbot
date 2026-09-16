package pixelbot.script.scope.js.rhino;

import pixelbot.script.scope.general.*;

public class GlobalScope extends ScriptMap implements IGlobalScriptScope {
	private final pixelbot.script.scope.general.GlobalScope scope;

	public GlobalScope(IConverter converter, pixelbot.script.scope.general.GlobalScope scope) {
		super(converter, scope);
		this.scope = scope;
	}

	@Override
	public void setCurrentOrigin(String origin) {
		this.scope.setCurrentOrigin(origin);
	}

}
