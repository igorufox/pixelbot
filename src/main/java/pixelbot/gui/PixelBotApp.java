package pixelbot.gui;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Launcher.
 * <p>
 * Everything past this class is loaded through a class loader that also covers {@code lib/} and
 * {@code libext/}, so scripts compiled into jars can be dropped next to the application and picked
 * up without rebuilding it.
 */
public class PixelBotApp {

	private static URLClassLoader loader;

	public static void main(String[] args) {
		try {
			ArrayList<URL> urls = new ArrayList<URL>();
			String classProp = System.getProperty("java.class.path");
			StringTokenizer tokenizer = new StringTokenizer(classProp, File.pathSeparator);
			while (tokenizer.hasMoreTokens()) {
				urls.add(new File(tokenizer.nextToken()).toURI().toURL());
			}

			for (String path : new String[] { "lib", "libext" }) {
				File[] jars = new File(path).listFiles(new FileFilter() {
					@Override
					public boolean accept(File file) {
						return file.getName().endsWith(".jar");
					}
				});
				if (jars != null) {
					for (File jar : jars) {
						urls.add(jar.toURI().toURL());
					}
				}
			}

			// The parent must be the platform loader, not the bootstrap loader. Before the module
			// system the bootstrap loader saw all of rt.jar; today it defines only java.base,
			// java.desktop and a few others, so a null parent hides java.compiler and the embedded
			// Java script backend dies with NoClassDefFoundError: javax/tools/ToolProvider.
			loader = new URLClassLoader(urls.toArray(new URL[0]),
					ClassLoader.getPlatformClassLoader());
			Thread.currentThread().setContextClassLoader(loader);

			Class<?> c = Class.forName("pixelbot.gui.Starter", true, loader);
			c.getMethod("start", File.class).invoke(null, new File("."));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
