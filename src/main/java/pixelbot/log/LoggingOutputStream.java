package pixelbot.log;

import java.io.*;
import java.util.logging.*;

/**
 * An OutputStream that writes contents to a Logger upon each call to flush()
 */
class LoggingOutputStream extends ByteArrayOutputStream {

	private String lineSeparator;

	private Logger logger;
	private Level level;

	/**
	 * Constructor
	 * 
	 * @param logger Logger to write to
	 * @param level Level at which to write the log message
	 */
	public LoggingOutputStream(Logger logger, Level level) {
		super();
		this.logger = logger;
		this.level = level;
		this.lineSeparator = System.getProperty("line.separator");
	}

	/**
	 * upon flush() write the existing contents of the OutputStream to the logger as a log record.
	 * 
	 * @throws java.io.IOException in case of error
	 */
	@Override
	public void flush() throws IOException {

		String record;
		synchronized (this) {
			super.flush();
			record = this.toString();
			if (record.endsWith(this.lineSeparator)) {
				super.reset();
				this.logger.logp(this.level, "", "",
						record.substring(0, record.length() - this.lineSeparator.length()));
			}
		}
	}
}