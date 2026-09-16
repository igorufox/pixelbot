package pixelbot.script.instantiator.general;

import pixelbot.script.scope.general.IScopeFunction;

public interface IScriptInstantiator {
	public void instantiate() throws Exception;

	public IScopeFunction getFunction();
}
