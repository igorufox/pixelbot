package pixelbot.script.parser.clazz;

import java.util.*;

import pixelbot.log.ScriptLogLevel;
import pixelbot.script.general.ScriptManager;
import pixelbot.script.instantiator.general.IScriptInstantiator;
import pixelbot.script.parser.general.*;
import pixelbot.script.parser.general.IScriptCompiler.CompiledUnit;

public class ClassParser extends ScriptParser {

	public ClassParser(ScriptManager manager) {
		super(manager);
	}

	@Override
	public String getType() {
		return "class";
	}

	@Override
	public String getId() {
		return "class/generic";
	}

	@Override
	public String getName() {
		return "Generic java .class files loader";
	}

	@Override
	public int getPriority() {
		return 50;
	}

	@Override
	public List<IScriptInstantiator> compile(String name, byte[] data) {
		List<IScriptInstantiator> result = null;
		try {

			final Collection<CompiledUnit> classes = Arrays
					.asList(new CompiledUnit[] { new CompiledUnit(name, data) });

			DynamicClassLoader loader = new DynamicClassLoader(
					IScriptInstantiator.class.getClassLoader());

			loader.defineClasses(classes);

			Collection<Object> scripts = new ArrayList<Object>();

			for (Class<?> c : loader.getClasses()) {
				if (this.manager.getScriptInstantiator().canInstantiate(c)) {
					scripts.add(c.newInstance());
				}
			}

			result = this.manager.getScriptInstantiator().getList(this, scripts);

			this.manager.log(ScriptLogLevel.CompInfo, "Sucessfully parsed: " + name);
		} catch (Exception e) {
			e.printStackTrace();
			this.manager.log(ScriptLogLevel.CompError, e.toString());
		}

		return result;
	}

}
