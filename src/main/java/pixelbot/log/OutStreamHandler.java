package pixelbot.log;

import java.io.OutputStream;
import java.util.logging.*;

public class OutStreamHandler extends Handler {
	private LogManager manager = LogManager.getLogManager();
	private OutputStream output;
	private boolean doneHeader;
	protected String lineSeparator;

	public OutStreamHandler(OutputStream paramOutputStream) {
		this.setLevel(Level.INFO);
		this.setFilter(null);
		try {
			this.setEncoding("UTF-8");
		} catch (Exception localException1) {}
		this.lineSeparator = System.getProperty("line.separator");
		setFormatter(new Formatter() {
			@Override
			public String format(LogRecord rec) {
				return rec.getMessage() + OutStreamHandler.this.lineSeparator;
			}
		});
		setOutputStream(paramOutputStream);
	}

	protected synchronized void setOutputStream(OutputStream paramOutputStream)
			throws SecurityException {
		if (paramOutputStream == null)
			throw new NullPointerException();
		flushAndClose();
		this.output = paramOutputStream;
		this.doneHeader = false;

	}

	@Override
	public synchronized void publish(LogRecord paramLogRecord) {
		if (!(isLoggable(paramLogRecord)))
			return;
		String str;
		try {
			str = getFormatter().format(paramLogRecord);
		} catch (Exception localException1) {
			reportError(null, localException1, 5);
			return;
		}
		try {
			if (!(this.doneHeader)) {
				this.output.write(getFormatter().getHead(this).getBytes(this.getEncoding()));
				this.doneHeader = true;
			}
			this.output.write(str.getBytes(this.getEncoding()));
		} catch (Exception localException2) {
			reportError(null, localException2, 1);
		}
	}

	@Override
	public boolean isLoggable(LogRecord paramLogRecord) {
		if ((this.output == null) || (paramLogRecord == null))
			return false;
		return super.isLoggable(paramLogRecord);
	}

	@Override
	public synchronized void flush() {
		if (this.output == null)
			return;
		try {
			this.output.flush();
		} catch (Exception localException) {
			reportError(null, localException, 2);
		}
	}

	private synchronized void flushAndClose() throws SecurityException {
		this.manager.checkAccess();
		if (this.output == null)
			return;
		try {
			if (!(this.doneHeader)) {
				this.output.write(getFormatter().getHead(this).getBytes(this.getEncoding()));
				this.doneHeader = true;
			}
			this.output.write(getFormatter().getTail(this).getBytes(this.getEncoding()));
			this.output.flush();
			this.output.close();
		} catch (Exception localException) {
			reportError(null, localException, 3);
		}
		this.output = null;
	}

	@Override
	public synchronized void close() throws SecurityException {
		flushAndClose();
	}
}
