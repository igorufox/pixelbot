package pixelbot.script.scope.general;

public class JScopeNumber implements IScopeNumber {
	private Number number = null;

	public JScopeNumber() {
		this(0);
	}

	public JScopeNumber(int number) {
		this(Integer.valueOf(number));
	}

	public JScopeNumber(double number) {
		this(Double.valueOf(number));
	}

	public JScopeNumber(Number number) {
		this.number = number;
	}

	@Override
	public Object getObject() {
		return this.number;
	}

	@Override
	public Number value() {
		return this.number;
	}

	@Override
	public int intValue() {
		return this.number == null ? 0 : this.number.intValue();
	}

	@Override
	public double doubleValue() {
		return this.number == null ? 0 : this.number.doubleValue();
	}

	@Override
	public String origin() {
		return "general";
	}

}
