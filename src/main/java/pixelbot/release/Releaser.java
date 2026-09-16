package pixelbot.release;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.jar.JarOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;

import pixelbot.elements.ElemTools;
import pixelbot.elements.Elements;
import pixelbot.misc.Tools;
import pixelbot.prefs.FilePreferencesFactory;
import pixelbot.script.parser.general.IScriptCompiler.CompiledUnit;
import pixelbot.script.parser.java.EclipseCompiler;
import pixelbot.script.parser.js.rhino.JsCompiler;

public class Releaser {

	/**
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {

		// Releaser.packageElements();
		Releaser.compileScripts();
		if (args.length > 0 && args[0] != null) {
			// Files.move(new File(Tools.workdir, "elements.elz").toPath(), new File(args[0],
			// "elements.elz").toPath(), StandardCopyOption.REPLACE_EXISTING);
			Files.move(new File(Tools.workdir, "scripts.jar").toPath(), new File(args[0],
					"scripts.jar").toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

	}

	@SuppressWarnings("unused")
	private static void packageElements() throws FileNotFoundException, IOException {
		Elements elements = ElemTools.loadElements();

		try (ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(
				new FileOutputStream(new File(Tools.workdir, "elements.elz"))))) {
			oos.writeObject(elements);
		}

	}

	private static void compileScripts() throws FileNotFoundException, IOException {
		System.setProperty("java.util.prefs.PreferencesFactory",
				FilePreferencesFactory.class.getName());

		try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(new File(Tools.workdir,
				"scripts.jar")))) {

			for (File f : new File(Tools.workdir, "scripts").listFiles()) {
				try (FileInputStream fis = new FileInputStream(f);
						ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

					int len = 0;
					byte[] bytes = new byte[1024];

					while ((len = fis.read(bytes)) > 0) {
						baos.write(bytes, 0, len);
					}
					baos.close();
					fis.close();

					Collection<CompiledUnit> res = null;
					if (f.getName().endsWith(".djs")) {
						res = new JsCompiler().compile(f.getName(), baos.toByteArray());
					} else if (f.getName().endsWith(".djava")) {
						res = new EclipseCompiler().compile(f.getName(), baos.toByteArray());
					}

					if (res != null) {
						for (CompiledUnit cu : res) {
							jos.putNextEntry(new ZipEntry(cu.getClassName()));
							jos.write(cu.getData());
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	@SuppressWarnings("unused")
	private static void compileApplication() throws FileNotFoundException, IOException {
		System.setProperty("java.util.prefs.PreferencesFactory",
				FilePreferencesFactory.class.getName());

		try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(new File(Tools.workdir,
				"pixelbot.jar")))) {
			processPath(new File(Tools.workdir, "src"), jos);
		}

	}

	private static void processPath(File path, JarOutputStream jos) {
		for (File f : path.listFiles()) {
			if (f.isDirectory()) {
				processPath(f, jos);
			} else {
				try (FileInputStream fis = new FileInputStream(f);
						ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

					int len = 0;
					byte[] bytes = new byte[1024];

					while ((len = fis.read(bytes)) > 0) {
						baos.write(bytes, 0, len);
					}
					baos.close();
					fis.close();

					Collection<CompiledUnit> res = null;
					if (f.getName().endsWith(".java")) {
						res = new EclipseCompiler().compile(f.getName(), baos.toByteArray());
					}

					if (res != null) {
						for (CompiledUnit cu : res) {
							jos.putNextEntry(new ZipEntry(cu.getClassName()));
							jos.write(cu.getData());
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
