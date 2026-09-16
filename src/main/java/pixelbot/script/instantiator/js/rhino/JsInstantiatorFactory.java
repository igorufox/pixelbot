package pixelbot.script.instantiator.js.rhino;

import org.mozilla.javascript.*;

import pixelbot.script.general.ScriptManager;
import pixelbot.script.instantiator.general.*;
import pixelbot.script.scope.general.IConverter;
import pixelbot.script.scope.js.rhino.*;

public class JsInstantiatorFactory extends ScriptInstantiatorFactory implements
		IScriptInstantiatorFactory {
	private final IConverter converter;
	private Scriptable global_scope = null;
	private GlobalScope engine_scope = null;

	public JsInstantiatorFactory(ScriptManager manager) {
		this.converter = new JsConverter();
		try {
			Context cx = Context.enter();
			this.global_scope = cx.initStandardObjects();
			this.engine_scope = new GlobalScope(this.converter, manager.getScope());
			this.engine_scope.setPrototype(this.global_scope);
		} finally {
			Context.exit();
		}
	}

	@Override
	public IScriptInstantiator produce(String origin, Object script) {
		return new JsInstantiator(this.converter, this.engine_scope, origin, (Script) script);
	}

	@Override
	public String getName() {
		return "Mozilla Rhino objects instantiator";
	}

	@Override
	public String getId() {
		return "javascript/rhino";
	}

}
