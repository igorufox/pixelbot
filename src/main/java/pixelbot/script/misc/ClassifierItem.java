package pixelbot.script.misc;


public class ClassifierItem extends ScopeClassifierItem {
	private String code;

	public ClassifierItem(int order, String text, String code) {
		super(order, text);
		this.code = code;
	}

	@Override
	public String getCode() {
		return this.code;
	}


}
