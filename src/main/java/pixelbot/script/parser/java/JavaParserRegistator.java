package pixelbot.script.parser.java;

import javax.tools.ToolProvider;

import pixelbot.script.general.ScriptManager;
import pixelbot.script.parser.general.IScriptParserRegistrator;

public class JavaParserRegistator implements IScriptParserRegistrator {

	@Override
	public void register(ScriptManager manager) {
		if (ToolProvider.getSystemJavaCompiler() != null) {
			manager.getScriptParsers().addScriptProvider(new JavaParser(manager, new JdkCompiler()));
		}
		try {
			Class.forName("org.eclipse.jdt.internal.compiler.Compiler");
			manager.getScriptParsers().addScriptProvider(new JavaParser(manager, new EclipseCompiler()));
		} catch (Exception e) {}
	}
}
