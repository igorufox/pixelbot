package pixelbot.script.scope.general;

public class JScopeBoolean implements IScopeBoolean {
	boolean bool = false;

	public JScopeBoolean() {
		this(false);
	}

	public JScopeBoolean(Boolean bool) {
		this(bool == null ? false : bool.booleanValue());
	}

	public JScopeBoolean(boolean bool) {
		this.bool = bool;
	}

	@Override
	public Object getObject() {
		return Boolean.valueOf(this.bool);
	}

	@Override
	public boolean value() {
		return this.bool;
	}

	@Override
	public String toString() {
		return Boolean.toString(this.bool);
	}

	@Override
	public String origin() {
		return "general";
	}

}
