package pixelbot.script.parser.java;

import pixelbot.script.parser.general.IScriptCompiler;

interface ICompiler extends IScriptCompiler {

	public interface ILogger {
		void message(String message);

		void error(String message);
	}

	public String getType();

	public String getId();

	public String getName();

	public int getPriority();

	public void setLogger(ILogger logger);

}
