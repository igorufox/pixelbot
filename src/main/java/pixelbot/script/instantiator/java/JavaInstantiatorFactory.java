package pixelbot.script.instantiator.java;

import pixelbot.script.general.ScriptManager;
import pixelbot.script.instantiator.general.*;
import pixelbot.script.scope.general.IConverter;
import pixelbot.script.scope.java.*;

public class JavaInstantiatorFactory extends ScriptInstantiatorFactory implements
		IScriptInstantiatorFactory {
	private final IConverter converter;
	private final GlobalScope scope;

	public JavaInstantiatorFactory(ScriptManager manager) {
		this.converter = new JavaConverter();
		this.scope = new GlobalScope(this.converter, manager.getScope());
	}

	@Override
	public IScriptInstantiator produce(String origin, Object script) {
		return new JavaInstatiator(this.converter, this.scope, origin, (INativeJob) script);
	}

	@Override
	public String getName() {
		return "Java objects instantiator";
	}

	@Override
	public String getId() {
		return "java/generic";
	}

}
