package pixelbot.components.editor.hint.event;

import java.util.EventListener;

public interface HintEventListener extends EventListener {
	public abstract void initialized(HintEvent event);
}
