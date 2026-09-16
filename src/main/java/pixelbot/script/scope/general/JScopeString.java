package pixelbot.script.scope.general;

public class JScopeString implements IScopeString {
	private String str = null;

	public JScopeString() {
		this("");
	}

	public JScopeString(String str) {
		this.str = str;
	}

	@Override
	public Object getObject() {
		return this.str;
	}

	@Override
	public String value() {
		return this.str;
	}

	@Override
	public String toString() {
		return this.str;
	}

	@Override
	public int compareTo(String o) {
		return this.str.compareTo(o);
	}

	@Override
	public String origin() {
		return "general";
	}
}
