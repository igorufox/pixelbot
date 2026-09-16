package pixelbot.script.misc;

import pixelbot.misc.*;
import pixelbot.script.scope.general.IScopeString;

public class ScopeClassifierItem extends SimpleClassifierItem {

	public ScopeClassifierItem(int order, String text) {
		super(order, text);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IClassifierItem) {
			return this.getText().equals(((IClassifierItem) obj).getText());
		} else if (obj instanceof String) {
			return this.getText().equals(obj);
		} else if (obj instanceof IScopeString) {
			return this.getText().equals(obj.toString());
		}

		return false;
	}

}
