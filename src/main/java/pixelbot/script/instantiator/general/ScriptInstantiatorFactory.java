package pixelbot.script.instantiator.general;

public abstract class ScriptInstantiatorFactory implements IScriptInstantiatorFactory {
	private boolean enabled = true;

	@Override
	public final String toString() {
		return this.getName();
	}

	@Override
	public final int compareTo(IScriptInstantiatorFactory o) {
		return this.getName().compareTo(o.getName());
	}

	@Override
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;

	}

	@Override
	public final boolean isEnabled() {
		return this.enabled;

	}
}
