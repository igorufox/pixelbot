package pixelbot.script.instantiator.js.graal;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import pixelbot.script.instantiator.general.IScriptInstantiator;
import pixelbot.script.scope.general.IScopeFunction;
import pixelbot.script.scope.general.IScopeObject;

public class GraalInstantiator implements IScriptInstantiator {

	private static final Logger LOG = Logger.getLogger(GraalInstantiator.class.getName());

	private final GraalInstantiatorFactory factory;
	private final String origin;
	private final Source source;

	GraalInstantiator(GraalInstantiatorFactory factory, String origin, Source source) {
		this.factory = factory;
		this.origin = origin;
		this.source = source;
	}

	@Override
	public void instantiate() {
		try {
			this.factory.evaluate(this.origin, this.source);
		} catch (RuntimeException e) {
			LOG.log(Level.SEVERE, "failed to instantiate " + this.origin, e);
		}
	}

	@Override
	public IScopeFunction getFunction() {
		try {
			Value result = this.factory.evaluate(this.origin, this.source);
			IScopeObject scoped = this.factory.getConverter().ScriptObjectToScopeObject(result);
			return scoped instanceof IScopeFunction ? (IScopeFunction) scoped : null;
		} catch (RuntimeException e) {
			LOG.log(Level.SEVERE, "failed to evaluate " + this.origin, e);
			return null;
		}
	}
}
