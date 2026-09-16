package pixelbot.script.parser.general;

import java.util.List;

import pixelbot.script.instantiator.general.IScriptInstantiator;

public interface IScriptParser extends Comparable<IScriptParser> {

	public String getType();

	public String getId();

	public String getName();

	public int getPriority();

	public List<IScriptInstantiator> compile(String name, byte[] data);

}
