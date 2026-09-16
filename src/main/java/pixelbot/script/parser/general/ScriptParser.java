package pixelbot.script.parser.general;

import pixelbot.script.general.ScriptManager;

public abstract class ScriptParser implements Comparable<IScriptParser>, IScriptParser {
	public ScriptManager manager;

	public ScriptParser(ScriptManager manager) {
		this.manager = manager;
	}

	@Override
	public final String toString() {
		return this.getName();
	}

	@Override
	public final int compareTo(IScriptParser o) {
		return this.getPriority() - o.getPriority();
	}

}
