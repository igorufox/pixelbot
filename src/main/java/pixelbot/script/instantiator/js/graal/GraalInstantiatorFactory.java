package pixelbot.script.instantiator.js.graal;

import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import pixelbot.script.general.ScriptManager;
import pixelbot.script.instantiator.general.*;
import pixelbot.script.scope.general.GlobalScope;
import pixelbot.script.scope.general.IScopeObject;
import pixelbot.script.scope.js.graal.GraalConverter;
import pixelbot.script.scope.js.graal.GraalRuntime;

/**
 * Runs scripts in one shared GraalJS context.
 * <p>
 * Rhino is handed a live {@code Scriptable} that writes straight through to the shared scope; Graal
 * keeps its own global object instead, so after a script has run its new globals are copied across.
 * The copy is of handles, not of data — a global stays a {@code Value} that reads through to the
 * engine — so later mutation is still visible on both sides.
 */
public class GraalInstantiatorFactory extends ScriptInstantiatorFactory implements
		IScriptInstantiatorFactory {

	private final GraalRuntime runtime = new GraalRuntime();
	private final GraalConverter converter = new GraalConverter(this.runtime);
	private final GlobalScope scope;

	public GraalInstantiatorFactory(ScriptManager manager) {
		this.scope = manager.getScope();
	}

	@Override
	public IScriptInstantiator produce(String origin, Object script) {
		return new GraalInstantiator(this, origin, (Source) script);
	}

	GraalRuntime getRuntime() {
		return this.runtime;
	}

	GraalConverter getConverter() {
		return this.converter;
	}

	/** Evaluates a script and publishes whatever globals it defined. */
	Value evaluate(String origin, Source source) {
		Value result = this.runtime.eval(source);
		this.publishGlobals(origin);
		return result;
	}

	private void publishGlobals(String origin) {
		this.scope.setCurrentOrigin(origin);

		Value bindings = this.runtime.bindings();
		for (String key : this.runtime.call(() -> bindings.getMemberKeys())) {
			IScopeObject value = this.converter.ScriptObjectToScopeObject(this.runtime
					.call(() -> bindings.getMember(key)));
			if (value != null) {
				this.scope.put(key, value);
			}
		}
	}

	@Override
	public String getName() {
		return "GraalVM JavaScript objects instantiator";
	}

	@Override
	public String getId() {
		return "javascript/graal";
	}
}
