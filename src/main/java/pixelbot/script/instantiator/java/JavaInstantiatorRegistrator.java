package pixelbot.script.instantiator.java;

import pixelbot.script.general.ScriptManager;
import pixelbot.script.instantiator.general.*;
import pixelbot.script.scope.java.INativeJob;

public class JavaInstantiatorRegistrator implements IScriptInstantiatorRegistrator {

	@Override
	public void register(ScriptManager manager) {
		manager.getScriptInstantiator().register(INativeJob.class,
				new JavaInstantiatorFactory(manager));

	}

}
