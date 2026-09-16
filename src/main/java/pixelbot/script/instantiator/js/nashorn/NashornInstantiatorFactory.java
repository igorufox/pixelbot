package pixelbot.script.instantiator.js.nashorn;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import pixelbot.script.general.ScriptManager;
import pixelbot.script.instantiator.general.*;
import pixelbot.script.scope.general.GlobalScope;
import pixelbot.script.scope.general.IScopeObject;
import pixelbot.script.scope.js.nashorn.NashornConverter;

/**
 * Runs scripts in one shared Nashorn engine.
 * <p>
 * Like the GraalJS backend, the engine keeps its own bindings and the globals a script defined are
 * copied into the shared scope afterwards. What is copied are mirrors, so both sides keep seeing
 * the same objects.
 */
public class NashornInstantiatorFactory extends ScriptInstantiatorFactory implements
		IScriptInstantiatorFactory {

	private final NashornConverter converter = new NashornConverter();
	private final ScriptEngine engine;
	private final GlobalScope scope;

	public NashornInstantiatorFactory(ScriptManager manager) {
		this.scope = manager.getScope();
		this.engine = new ScriptEngineManager().getEngineByName("nashorn");
		if (this.engine == null) {
			throw new IllegalStateException("no Nashorn engine on the classpath");
		}
	}

	ScriptEngine getEngine() {
		return this.engine;
	}

	NashornConverter getConverter() {
		return this.converter;
	}

	/** Evaluates a compiled script and publishes whatever globals it defined. */
	synchronized Object evaluate(String origin, CompiledScript script) throws ScriptException {
		Object result = script.eval(this.engine.getContext());
		this.publishGlobals(origin);
		return result;
	}

	private void publishGlobals(String origin) {
		this.scope.setCurrentOrigin(origin);

		Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
		for (String key : bindings.keySet()) {
			IScopeObject value = this.converter.ScriptObjectToScopeObject(bindings.get(key));
			if (value != null) {
				this.scope.put(key, value);
			}
		}
	}

	@Override
	public IScriptInstantiator produce(String origin, Object script) {
		return new NashornInstantiator(this, origin, (CompiledScript) script);
	}

	@Override
	public String getName() {
		return "OpenJDK Nashorn objects instantiator";
	}

	@Override
	public String getId() {
		return "javascript/nashorn";
	}
}
