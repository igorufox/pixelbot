package pixelbot.components.editor.hint.event;

public class HintEvent {
	public enum Status {
		Unknown, Success, NotFounnd, Fail
	}

	private Status _status = Status.Unknown;

	public void setStatus(Status status) {
		this._status = status;
	}

	public Status getStatus() {
		return this._status;
	}

}
