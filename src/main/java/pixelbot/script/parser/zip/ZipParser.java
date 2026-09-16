package pixelbot.script.parser.zip;

import java.io.*;
import java.util.*;
import java.util.jar.*;

import pixelbot.log.ScriptLogLevel;
import pixelbot.script.general.ScriptManager;
import pixelbot.script.instantiator.general.IScriptInstantiator;
import pixelbot.script.parser.general.*;
import pixelbot.script.parser.general.IScriptCompiler.CompiledUnit;

public class ZipParser extends ScriptParser {

	public ZipParser(ScriptManager manager) {
		super(manager);
	}

	@Override
	public String getType() {
		return "zip";
	}

	@Override
	public String getId() {
		return "zip/generic";
	}

	@Override
	public String getName() {
		return "ZIP files parser";
	}

	@Override
	public int getPriority() {
		return 50;
	}

	@Override
	public List<IScriptInstantiator> compile(String name, byte[] data) {
		List<IScriptInstantiator> result = null;
		try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
				JarInputStream jis = new JarInputStream(bais, true)) {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int len = 0;

			final Collection<CompiledUnit> classes = new ArrayList<CompiledUnit>();
			JarEntry entry = null;
			while ((entry = jis.getNextJarEntry()) != null) {
				if (entry.getName().endsWith(".class")) {
					baos.reset();
					while ((len = jis.read(buff)) != -1) {
						baos.write(buff, 0, len);
					}
					baos.flush();
					classes.add(new CompiledUnit(entry.getName(), baos.toByteArray()));
				}
			}
			jis.close();
			bais.close();

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

			this.manager.log(ScriptLogLevel.CompError, "Sucessfully parsed: " + name);
		} catch (Throwable e) {
			e.printStackTrace();
			this.manager.log(ScriptLogLevel.CompInfo, e.toString());
		}

		return result;
	}

}
