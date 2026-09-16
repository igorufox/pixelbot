package pixelbot.script.scope.general;

public abstract class WrappersBase {
	public IConverter converter = null;

	public WrappersBase(IConverter converter) {
		this.converter = converter;
	}
}
