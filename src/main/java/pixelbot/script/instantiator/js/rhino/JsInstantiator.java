package pixelbot.script.instantiator.js.rhino;

import org.mozilla.javascript.*;

import pixelbot.script.instantiator.general.IScriptInstantiator;
import pixelbot.script.scope.general.*;
import pixelbot.script.scope.js.rhino.GlobalScope;

public class JsInstantiator implements IScriptInstantiator {
	private final IConverter converter;
	private final GlobalScope scope;
	private final String origin;
	private final Script job;

	public JsInstantiator(IConverter converter, GlobalScope scope, String origin, Script job) {
		this.converter = converter;
		this.scope = scope;
		this.origin = origin;
		this.job = job;
	}

	@Override
	public void instantiate() {
		try {
			Context context = Context.enter();
			this.scope.setCurrentOrigin(this.origin);
			this.job.exec(context, this.scope);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Context.exit();
		}

	}

	@Override
	public IScopeFunction getFunction() {
		try {
			Context context = Context.enter();
			Object obj = this.job.exec(context, this.scope);
			return (IScopeFunction) this.converter.ScriptObjectToScopeObject(obj);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Context.exit();
		}
		return null;
	}

}
