package pixelbot.script.scope.java;

public interface INativeCallable {

	public Object call(Object thisObj, Object... params) throws InterruptedException;

}
