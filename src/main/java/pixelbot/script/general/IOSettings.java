package pixelbot.script.general;

import javax.swing.*;
import javax.swing.JToggleButton.ToggleButtonModel;

import pixelbot.elements.IElementDescriptor.Locale;

public class IOSettings {
	private SpinnerNumberModel mouseMovingSpeed = new SpinnerNumberModel(50, 0, 100, 1);
	private SpinnerNumberModel mouseClickDurationMin = new SpinnerNumberModel(100, 0, 2000, 10);
	private SpinnerNumberModel mouseClickDurationMax = new SpinnerNumberModel(300, 0, 2000, 10);
	private SpinnerNumberModel mousePauseAfterClickMin = new SpinnerNumberModel(100, 0, 2000, 10);
	private SpinnerNumberModel mousePauseAfterClickMax = new SpinnerNumberModel(300, 0, 2000, 10);
	private SpinnerNumberModel mousePauseAfterPositionMin = new SpinnerNumberModel(100, 0, 2000, 10);
	private SpinnerNumberModel mousePauseAfterPositionMax = new SpinnerNumberModel(300, 0, 2000, 10);
	private ToggleButtonModel checkUserActivity = new ToggleButtonModel();
	private ToggleButtonModel highlightElements = new ToggleButtonModel();
	private ToggleButtonModel hideOnExecution = new ToggleButtonModel();
	private ToggleButtonModel minimizeToSystemTray = new ToggleButtonModel();

	private ComboBoxModel<String> locale = new DefaultComboBoxModel<>(new String[] { "RU", "EN" });

	public SpinnerModel getMouseMovingSpeedModel() {
		return this.mouseMovingSpeed;
	}

	public int getMouseMovingSpeed() {
		return this.mouseMovingSpeed.getNumber().intValue();
	}

	public void setMouseMovingSpeed(int value) {
		this.mouseMovingSpeed.setValue(Integer.valueOf(value));
	}

	public SpinnerModel getMouseClickDurationMinModel() {
		return this.mouseClickDurationMin;
	}

	public int getMouseClickDurationMin() {
		return this.mouseClickDurationMin.getNumber().intValue();
	}

	public void setMouseClickDurationMin(int value) {
		this.mouseClickDurationMin.setValue(Integer.valueOf(value));
	}

	public SpinnerModel getMouseClickDurationMaxModel() {
		return this.mouseClickDurationMax;
	}

	public int getMouseClickDurationMax() {
		return this.mouseClickDurationMax.getNumber().intValue();
	}

	public void setMouseClickDurationMax(int value) {
		this.mouseClickDurationMax.setValue(Integer.valueOf(value));
	}

	public int getMouseClickDuration() {
		int min = this.mouseClickDurationMin.getNumber().intValue();
		int max = this.mouseClickDurationMax.getNumber().intValue();
		if (max < min) {
			min = this.mouseClickDurationMax.getNumber().intValue();
			max = this.mouseClickDurationMin.getNumber().intValue();
		}

		return min + (int) ((max - min) * Math.random());
	}

	public SpinnerModel getMousePauseAfterClickMinModel() {
		return this.mousePauseAfterClickMin;
	}

	public int getMousePauseAfterClickMin() {
		return this.mousePauseAfterClickMin.getNumber().intValue();
	}

	public void setMousePauseAfterClickMin(int value) {
		this.mousePauseAfterClickMin.setValue(Integer.valueOf(value));
	}

	public SpinnerModel getMousePauseAfterClickMaxModel() {
		return this.mousePauseAfterClickMax;
	}

	public int getMousePauseAfterClickMax() {
		return this.mousePauseAfterClickMax.getNumber().intValue();
	}

	public void setMousePauseAfterClickMax(int value) {
		this.mousePauseAfterClickMax.setValue(Integer.valueOf(value));
	}

	public int getMousePauseAfterClick() {
		int min = this.mousePauseAfterClickMin.getNumber().intValue();
		int max = this.mousePauseAfterClickMax.getNumber().intValue();
		if (max < min) {
			min = this.mousePauseAfterClickMin.getNumber().intValue();
			max = this.mousePauseAfterClickMax.getNumber().intValue();
		}

		return min + (int) ((max - min) * Math.random());
	}

	public SpinnerModel getMousePauseAfterPositionMinModel() {
		return this.mousePauseAfterPositionMin;
	}

	public int getMousePauseAfterPositionMin() {
		return this.mousePauseAfterPositionMin.getNumber().intValue();
	}

	public void setMousePauseAfterPositionMin(int value) {
		this.mousePauseAfterPositionMin.setValue(Integer.valueOf(value));
	}

	public SpinnerModel getMousePauseAfterPositionMaxModel() {
		return this.mousePauseAfterPositionMax;
	}

	public int getMousePauseAfterPositionMax() {
		return this.mousePauseAfterPositionMax.getNumber().intValue();
	}

	public void setMousePauseAfterPositionMax(int value) {
		this.mousePauseAfterPositionMax.setValue(Integer.valueOf(value));
	}

	public long getMousePauseAfterPosition() {
		int min = this.mousePauseAfterPositionMin.getNumber().intValue();
		int max = this.mousePauseAfterPositionMax.getNumber().intValue();
		if (max < min) {
			min = this.mousePauseAfterPositionMin.getNumber().intValue();
			max = this.mousePauseAfterPositionMax.getNumber().intValue();
		}

		return min + (int) ((max - min) * Math.random());
	}

	public ButtonModel getCheckUserActivityModel() {
		return this.checkUserActivity;
	}

	public boolean isCheckUserActivity() {
		return this.checkUserActivity.isSelected();
	}

	public void setCheckUserActivity(boolean value) {
		this.checkUserActivity.setSelected(value);
	}

	public ButtonModel getHighlightElementsModel() {
		return this.highlightElements;
	}

	public boolean isHighlightElements() {
		return this.highlightElements.isSelected();
	}

	public void setHighlightElements(boolean value) {
		this.highlightElements.setSelected(value);
	}

	public ButtonModel getHideOnExecutionModel() {
		return this.hideOnExecution;
	}

	public boolean isHideOnExecution() {
		return this.hideOnExecution.isSelected();
	}

	public void setHideOnExecution(boolean value) {
		this.hideOnExecution.setSelected(value);
	}

	public ButtonModel getMinimizeToSystemTrayModel() {
		return this.minimizeToSystemTray;
	}

	public boolean isMinimizeToSystemTray() {
		return this.minimizeToSystemTray.isSelected();
	}

	public void setMinimizeToSystemTray(boolean value) {
		this.minimizeToSystemTray.setSelected(value);
	}

	public ComboBoxModel<String> getLocaleModel() {
		return this.locale;
	}

	public String getLocale() {
		return (String) this.locale.getSelectedItem();
	}

	public Locale getLocaleEnum() {
		return Enum.valueOf(Locale.class, this.getLocale().toLowerCase());
	}

	public void setLocale(String value) {
		this.locale.setSelectedItem(value);
	}

}
