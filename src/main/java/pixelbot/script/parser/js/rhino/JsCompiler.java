package pixelbot.script.parser.js.rhino;

import java.util.*;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.optimizer.ClassCompiler;

import pixelbot.script.parser.general.IScriptCompiler;

public class JsCompiler implements IScriptCompiler {

	private static String getName(String src) {
		src = src.replace('-', '_');
		if (src.length() != 0 && src.indexOf('.') != -1) {
			return "pixelbot." + src.substring(0, 1).toUpperCase()
					+ src.substring(1, src.indexOf('.'));
		}
		return "dummy.class";
	}

	@Override
	public List<CompiledUnit> compile(String src_name, byte[] source) {
		List<CompiledUnit> result = new ArrayList<CompiledUnit>();
		try {
			CompilerEnvirons compilerEnv = new CompilerEnvirons();

			ClassCompiler compiler = new ClassCompiler(compilerEnv);

			Object[] compiled = compiler.compileToClassFiles(new String(source, "UTF-8"), src_name,
					0, JsCompiler.getName(src_name));

			for (int j = 0; j != compiled.length; j += 2) {
				String className = compiled[j].toString().replace(".", "/") + ".class";
				byte[] bytes = (byte[]) compiled[(j + 1)];
				result.add(new CompiledUnit(className, bytes));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
