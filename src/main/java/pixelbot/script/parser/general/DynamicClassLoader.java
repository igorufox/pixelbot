package pixelbot.script.parser.general;

import java.util.*;

import pixelbot.script.parser.general.IScriptCompiler.CompiledUnit;

public class DynamicClassLoader extends ClassLoader {
	private Collection<Class<?>> classes = new ArrayList<Class<?>>();

	public DynamicClassLoader(ClassLoader parent) {
		super(parent);
	}

	public void defineClass(byte[] b) {
		if (b != null) {
			Class<?> c = this.defineClass(null, b, 0, b.length);
			this.classes.add(c);
		}
	}

	public void defineClasses(Collection<CompiledUnit> classes) {
		for (CompiledUnit b : classes) {
			this.defineClass(b.getData());
		}
	}

	public Collection<Class<?>> getClasses() {
		return this.classes;
	}

}