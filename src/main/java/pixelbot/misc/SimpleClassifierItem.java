package pixelbot.misc;

public class SimpleClassifierItem implements IClassifierItem {
	private int order;
	private String text;

	public SimpleClassifierItem(int order, String text) {
		this.order = order;
		this.text = text;
	}

	@Override
	public int compareTo(IClassifierItem o) {
		if (this.getOrder() == o.getOrder()) {
			return this.getText().compareTo(o.getText());
		}
		return this.getOrder() - o.getOrder();
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public String getCode() {
		return this.getText();
	}

	public void setText(String value) {
		this.text = value;
	}

	@Override
	public String toString() {
		return this.getText();
	}

	@Override
	public int hashCode() {
		return this.text.hashCode();
	}

}
