package pixelbot.script.scope.java;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public interface INativeJob {
	public interface IConfiguration {
		public boolean active();

		public NativeList config(NativeMap params) throws InterruptedException,
				InvocationTargetException;

		public Map<String, Object> defaults() throws InterruptedException,
				InvocationTargetException;

		public String name();

	}

	public void main(Map<String, Object> params) throws InterruptedException,
			InvocationTargetException;

	public IConfiguration params();

	public void setContext(NativeMap context);
}
