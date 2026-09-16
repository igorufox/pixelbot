package pixelbot.script.parser.java;

import java.io.*;
import java.net.URI;
import java.util.*;

import javax.tools.*;
import javax.tools.JavaFileObject.Kind;

public class JdkCompiler implements ICompiler {

	private static class JavaSourceFromString extends SimpleJavaFileObject {
		/**
		 * The source code of this "file".
		 */
		final String code;
		private ByteArrayOutputStream os = new ByteArrayOutputStream();

		/**
		 * Constructs a new JavaSourceFromString.
		 * 
		 * @param name the name of the compilation unit represented by this file object
		 * @param code the source code for the compilation unit represented by this file object
		 */
		JavaSourceFromString(String text, String name) {
			super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
					Kind.SOURCE);
			this.code = text;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return this.code;
		}

		@Override
		public OutputStream openOutputStream() throws IOException {
			return this.os;
		}

		public byte[] getContents() {
			return this.os.toByteArray();
		}

	}

	protected ILogger logger = null;

	@Override
	public void setLogger(ILogger logger) {
		this.logger = logger;
	}

	@Override
	public Collection<CompiledUnit> compile(final String name, final byte[] data) {

		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		final ArrayList<JavaSourceFromString> results = new ArrayList<JavaSourceFromString>();
		ArrayList<CompiledUnit> result = new ArrayList<CompiledUnit>();

		JavaFileManager filemanager = new JavaFileManager() {
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

			@Override
			public int isSupportedOption(String option) {
				return this.fileManager.isSupportedOption(option);
			}

			@Override
			public ClassLoader getClassLoader(Location location) {
				return this.fileManager.getClassLoader(location);
			}

			@Override
			public Iterable<JavaFileObject> list(Location location, String packageName,
					Set<Kind> kinds, boolean recurse) throws IOException {
				return this.fileManager.list(location, packageName, kinds, recurse);
			}

			@Override
			public String inferBinaryName(Location location, JavaFileObject file) {
				return this.fileManager.inferBinaryName(location, file);
			}

			@Override
			public boolean isSameFile(FileObject a, FileObject b) {
				return this.fileManager.isSameFile(a, b);
			}

			@Override
			public boolean handleOption(String current, Iterator<String> remaining) {
				return this.fileManager.handleOption(current, remaining);
			}

			@Override
			public boolean hasLocation(Location location) {
				return this.fileManager.hasLocation(location);
			}

			@Override
			public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind)
					throws IOException {
				return this.fileManager.getJavaFileForInput(location, className, kind);
			}

			@Override
			public JavaFileObject getJavaFileForOutput(Location location, String className,
					Kind kind, FileObject sibling) throws IOException {
				JavaSourceFromString r = new JavaSourceFromString(className, "test");
				results.add(r);
				return r;
				// return this.fileManager.getJavaFileForOutput(location, className, kind, sibling);
			}

			@Override
			public FileObject getFileForInput(Location location, String packageName,
					String relativeName) throws IOException {
				return this.fileManager.getFileForInput(location, packageName, relativeName);
			}

			@Override
			public FileObject getFileForOutput(Location location, String packageName,
					String relativeName, FileObject sibling) throws IOException {
				return this.fileManager.getFileForOutput(location, packageName, relativeName,
						sibling);
			}

			@Override
			public void flush() throws IOException {
				this.fileManager.flush();

			}

			@Override
			public void close() throws IOException {
				this.fileManager.close();
			}

		};

		ArrayList<JavaFileObject> units = new ArrayList<JavaFileObject>();
		try {
			units.add(new JavaSourceFromString(new String(data, "UTF-8"), name));
		} catch (UnsupportedEncodingException e) {}

		StringWriter writer = new StringWriter();
		Boolean r = compiler.getTask(writer, filemanager, null, null, null, units).call();
		if (this.logger != null && !r.booleanValue()) {
			this.logger.error(writer.toString());
		}

		for (JavaSourceFromString jfo : results) {
			result.add(new CompiledUnit(jfo.getName(), jfo.getContents()));
		}

		return result;
	}

	@Override
	public String getType() {
		return "djava";
	}

	@Override
	public String getId() {
		return "java/jdk";
	}

	@Override
	public String getName() {
		return "JDK java compiler";
	}

	@Override
	public int getPriority() {
		return 100;
	}
}
