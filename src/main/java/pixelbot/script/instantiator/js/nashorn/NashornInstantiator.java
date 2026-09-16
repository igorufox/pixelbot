package pixelbot.script.instantiator.js.nashorn;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.CompiledScript;

import pixelbot.script.instantiator.general.IScriptInstantiator;
import pixelbot.script.scope.general.IScopeFunction;
import pixelbot.script.scope.general.IScopeObject;

public class NashornInstantiator implements IScriptInstantiator {

	private static final Logger LOG = Logger.getLogger(NashornInstantiator.class.getName());

	private final NashornInstantiatorFactory factory;
	private final String origin;
	private final CompiledScript script;

	NashornInstantiator(NashornInstantiatorFactory factory, String origin, CompiledScript script) {
		this.factory = factory;
		this.origin = origin;
		this.script = script;
	}

	@Override
	public void instantiate() {
		try {
			this.factory.evaluate(this.origin, this.script);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "failed to instantiate " + this.origin, e);
		}
	}

	@Override
	public IScopeFunction getFunction() {
		try {
			Object result = this.factory.evaluate(this.origin, this.script);
			IScopeObject scoped = this.factory.getConverter().ScriptObjectToScopeObject(result);
			return scoped instanceof IScopeFunction ? (IScopeFunction) scoped : null;
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "failed to evaluate " + this.origin, e);
			return null;
		}
	}
}
