package pixelbot.misc;

public interface IClassifierItem extends Comparable<IClassifierItem> {
	public int getOrder();

	public String getCode();

	public String getText();

}
