package pixelbot.script.instantiator.general;

public interface IScriptInstantiatorFactory extends Comparable<IScriptInstantiatorFactory> {

	public IScriptInstantiator produce(String origin, Object script);

	public String getName();

	public String getId();

	public void setEnabled(boolean enabled);

	boolean isEnabled();

}
