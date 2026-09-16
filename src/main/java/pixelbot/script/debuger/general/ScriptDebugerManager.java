package pixelbot.script.debuger.general;

import java.awt.event.*;
import java.util.*;

import javax.swing.JRadioButton;

import pixelbot.script.debuger.general.DebugHandler.CallFrame;
import pixelbot.script.general.*;
import pixelbot.script.general.ScriptManager.BreakPointEvent;

public class ScriptDebugerManager implements ActionListener {
	private final List<DebugHandler> debugers = new ArrayList<DebugHandler>();
	private DebugHandler active = null;
	private final ScriptManager manager;

	public ScriptDebugerManager(ScriptManager manager) {
		this.manager = manager;

	}

	public void register(DebugHandler handler) {
		this.debugers.add(handler);
	}

	public DebugHandler getActive() {
		if (this.active == null && this.debugers.size() > 0) {
			this.active = this.debugers.get(0);
		}
		return this.active;
	}

	public CallFrame[] getCallStack() {
		return this.getActive().getCallStack();
	}

	public void addBreakPoint(String name, int line) {
		this.getActive().addBreakPoint(name, line);
	}

	public Map<String, Integer[]> getBreakPoints() {
		return this.getActive().getBreakPoints();
	}

	public void removeBreakPoint(String name, int line) {
		this.getActive().removeBreakPoint(name, line);
	}

	public void resume() {
		this.getActive().resume();
	}

	public void step_into() {
		this.getActive().step_into();
	}

	public void step_over() {
		this.getActive().step_over();
	}

	public void step_return() {
		this.getActive().step_return();
	}

	public void suspend() {
		this.getActive().suspend();
	}

	public void startDebug() {
		this.getActive().startDebug();
	}

	public void endDebug() {
		this.getActive().endDebug();
	}

	public void fireBreakPoint(BreakPointEvent event) {
		this.manager.fireBreakPoint(event);
	}

	public List<DebugHandler> list() {
		return Collections.unmodifiableList(this.debugers);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.active = (DebugHandler) ((JRadioButton) e.getSource()).getClientProperty("value");
	}

}
