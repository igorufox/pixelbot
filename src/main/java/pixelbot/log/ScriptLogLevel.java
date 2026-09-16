package pixelbot.log;

import java.io.*;
import java.util.logging.Level;

public class ScriptLogLevel extends Level {
	private static final long serialVersionUID = -1457099025610603099L;

	/**
	 * Private constructor
	 */
	private ScriptLogLevel(String name, int value) {
		super(name, value);
	}

	public static final Level Fatal = new ScriptLogLevel("Fatal", Level.INFO.intValue() + 11);
	public static final Level Error = new ScriptLogLevel("Error", Level.INFO.intValue() + 12);
	public static final Level Warning = new ScriptLogLevel("Warning", Level.INFO.intValue() + 13);
	public static final Level Info = new ScriptLogLevel("Info", Level.INFO.intValue() + 14);
	public static final Level Trace = new ScriptLogLevel("Trace", Level.INFO.intValue() + 15);
	public static final Level Success = new ScriptLogLevel("Success", Level.INFO.intValue() + 16);
	public static final Level CompInfo = new ScriptLogLevel("CompInfo", Level.INFO.intValue() + 20);
	public static final Level CompError = new ScriptLogLevel("CompError",
			Level.INFO.intValue() + 20);

	/**
	 * Method to avoid creating duplicate instances when deserializing the object.
	 * 
	 * @return the singleton instance of this <code>Level</code> value in this classloader
	 * @throws ObjectStreamException If unable to deserialize
	 */
	protected Object readResolve() throws ObjectStreamException {
		if (this.intValue() == Fatal.intValue())
			return Fatal;
		if (this.intValue() == Error.intValue())
			return Error;
		if (this.intValue() == Warning.intValue())
			return Warning;
		if (this.intValue() == Info.intValue())
			return Info;
		if (this.intValue() == Trace.intValue())
			return Trace;
		if (this.intValue() == Success.intValue())
			return Success;
		if (this.intValue() == CompInfo.intValue())
			return CompInfo;
		if (this.intValue() == CompError.intValue())
			return CompError;
		throw new InvalidObjectException("Unknown instance :" + this);
	}

	public static void init() {
		ScriptLogLevel.Fatal.getClass();
	}
}