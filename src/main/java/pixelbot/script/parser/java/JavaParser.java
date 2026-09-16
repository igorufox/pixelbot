package pixelbot.script.parser.java;

import java.util.*;

import pixelbot.log.ScriptLogLevel;
import pixelbot.script.general.ScriptManager;
import pixelbot.script.instantiator.general.IScriptInstantiator;
import pixelbot.script.parser.general.*;
import pixelbot.script.parser.general.IScriptCompiler.CompiledUnit;
import pixelbot.script.parser.java.ICompiler.ILogger;

public class JavaParser extends ScriptParser {
	private ICompiler compiler;

	public JavaParser(ScriptManager manager, ICompiler compiler) {
		super(manager);
		this.compiler = compiler;
		this.compiler.setLogger(new ILogger() {

			@Override
			public void message(String message) {
				JavaParser.this.manager.log(ScriptLogLevel.CompInfo, message);
			}

			@Override
			public void error(String message) {
				JavaParser.this.manager.log(ScriptLogLevel.CompError, message);
			}
		});
	}

	@Override
	public List<IScriptInstantiator> compile(String name, byte[] data) {
		List<IScriptInstantiator> result = null;
		try {

			final Collection<CompiledUnit> classes = this.compiler.compile(
					name.substring(0, name.length() - 6), data);

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
		} catch (Throwable e) {
			e.printStackTrace();
			this.manager.log(ScriptLogLevel.CompError, e.toString());
		}

		return result;
	}

	@Override
	public String getType() {
		return this.compiler.getType();
	}

	@Override
	public String getId() {
		return this.compiler.getId();
	}

	@Override
	public String getName() {
		return this.compiler.getName();
	}

	@Override
	public int getPriority() {
		return this.compiler.getPriority();
	}

}
