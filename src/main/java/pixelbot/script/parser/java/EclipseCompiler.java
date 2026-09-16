package pixelbot.script.parser.java;

import java.io.*;
import java.util.*;

import org.eclipse.jdt.internal.compiler.*;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.env.*;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.util.Util;

import pixelbot.misc.Tools;

public class EclipseCompiler implements ICompiler {
	private static final String encoding = "UTF-8";
	protected ILogger logger = null;

	@Override
	public void setLogger(ILogger logger) {
		this.logger = logger;
	}

	@Override
	public Collection<CompiledUnit> compile(final String name, final byte[] data) {
		ICompilationUnit[] units = new ICompilationUnit[] { new ICompilationUnit() {

			@Override
			public char[] getFileName() {
				return name.toCharArray();
			}

			@Override
			public char[][] getPackageName() {
				return null;
			}

			@Override
			public char[] getMainTypeName() {
				return null;
			}

			@Override
			public char[] getContents() {
				try {
					return new String(data, "UTF-8").toCharArray();
				} catch (UnsupportedEncodingException e) {
					return new char[] {};
				}
			}

			@Override
			public boolean ignoreOptionalProblems() {
				return true;
			}
		} };
		return this.performCompilation(units);
	}

	public Collection<CompiledUnit> performCompilation(ICompilationUnit[] units) {
		final ArrayList<CompiledUnit> result = new ArrayList<CompiledUnit>();

		INameEnvironment environment = new INameEnvironment() {
			private FileSystem fs = new FileSystem(handleBootclasspath(), new String[] {}, encoding);

			protected String[] handleBootclasspath() {

				ArrayList<FileSystem.Classpath> bootclasspaths = new ArrayList<FileSystem.Classpath>(
						4);
				try {
					Util.collectRunningVMBootclasspath(bootclasspaths);
				} catch (IllegalStateException localIllegalStateException) {
					return null;
				}

				String classProp = System.getProperty("java.class.path");
				StringTokenizer tokenizer = new StringTokenizer(classProp, File.pathSeparator);

				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					FileSystem.Classpath currentClasspath = FileSystem.getClasspath(token,
							encoding, null);
					if (currentClasspath != null) {
						bootclasspaths.add(currentClasspath);
					}
				}
				bootclasspaths.add(FileSystem.getClasspath(Tools.workdir.getAbsolutePath()
						+ "/pixelbot.jar", encoding, null));
				String[] result = new String[bootclasspaths.size()];
				int i = 0;
				for (FileSystem.Classpath cp : bootclasspaths) {
					result[i++] = cp.getPath();
				}

				return result;
			}

			@Override
			public boolean isPackage(char[][] compoundName, char[] packageName) {
				return this.fs.isPackage(compoundName, packageName);
			}

			@Override
			public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
				NameEnvironmentAnswer answer = this.fs.findType(typeName, packageName);
				return answer;
			}

			@Override
			public NameEnvironmentAnswer findType(char[][] compoundName) {
				NameEnvironmentAnswer answer = this.fs.findType(compoundName);
				return answer;
			}

			@Override
			public void cleanup() {
				this.fs.cleanup();
			}
		};
		ICompilerRequestor compilerRequestor = new ICompilerRequestor() {

			private String getName(char[][] src) {
				StringBuffer sb = new StringBuffer();
				for (char[] ch : src) {
					sb.append(ch).append(File.separatorChar);
				}
				if (sb.length() != 0)
					sb.setLength(sb.length() - 1);
				sb.append(".class");
				return sb.toString();
			}

			@Override
			public void acceptResult(CompilationResult compilationResult) {
				for (ClassFile cf : compilationResult.getClassFiles()) {
					result.add(new CompiledUnit(this.getName(cf.getCompoundName()), cf.getBytes()));
				}
				if (EclipseCompiler.this.logger != null) {
					if (compilationResult.hasProblems()) {
						EclipseCompiler.this.logger.error(compilationResult.toString());
					} else {
						EclipseCompiler.this.logger.message(compilationResult.toString());
					}
				}
			}
		};

		IErrorHandlingPolicy errorHandlingPolicy = new IErrorHandlingPolicy() {
			@Override
			public boolean stopOnFirstError() {
				return false;
			}

			@Override
			public boolean proceedOnErrors() {
				return false;
			}

			@Override
			public boolean ignoreAllErrors() {
				return false;
			}
		};

		CompilerOptions compilerOptions = new CompilerOptions(null);
		compilerOptions.sourceLevel = compilerOptions.originalSourceLevel = CompilerOptions
				.versionToJdkLevel(CompilerOptions.VERSION_1_7);
		compilerOptions.complianceLevel = compilerOptions.originalComplianceLevel = CompilerOptions
				.versionToJdkLevel(CompilerOptions.VERSION_1_7);

		IProblemFactory problemFactory = new DefaultProblemFactory(Locale.getDefault());

		Compiler batchCompiler = new Compiler(environment, errorHandlingPolicy, compilerOptions,
				compilerRequestor, problemFactory);

		batchCompiler.compile(units);

		environment.cleanup();
		return result;
	}

	@Override
	public String getType() {
		return "djava";
	}

	@Override
	public String getId() {
		return "java/eclipse";
	}

	@Override
	public String getName() {
		return "Eclipse java compiler";
	}

	@Override
	public int getPriority() {
		return 100;
	}

}
