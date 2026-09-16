package pixelbot.components.logviewer;

import java.util.LinkedList;
import java.util.logging.*;

import javax.swing.SwingUtilities;

public class LoggerModel extends BaseModel {
	private static final long serialVersionUID = -3725242481111277894L;
	protected LinkedList<LogRecord> messages = new LinkedList<LogRecord>();

	@Override
	public int getRowCount() {
		return this.messages.size();
	}

	@Override
	protected long getMillis(int rowIndex) {
		return this.messages.get(rowIndex).getMillis();
	}

	@Override
	protected long getSequence(int rowIndex) {
		return this.messages.get(rowIndex).getSequenceNumber();
	}

	@Override
	protected Level getLevel(int rowIndex) {
		return this.messages.get(rowIndex).getLevel();
	}

	@Override
	protected String getMessage(int rowIndex) {
		return this.messages.get(rowIndex).getMessage();
	}

	public LoggerModel() {
		Logger logger = Logger.getLogger("");
		logger.addHandler(new Handler() {

			@Override
			public void publish(final LogRecord record) {
				if (record == null || record.getMessage() == null)
					return;
				if (record.getMessage().contains("\n")) {
					for (String m : record.getMessage().split("\r?\n")) {
						LogRecord nm = new LogRecord(record.getLevel(), m);
						nm.setMillis(record.getMillis());
						this.publish(nm);
					}
				} else {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							while (LoggerModel.this.messages.size() >= 500) {
								LoggerModel.this.messages.remove();
								LoggerModel.this.fireTableRowsDeleted(0, 0);
							}
							LoggerModel.this.messages.add(record);
							LoggerModel.this.fireTableRowsInserted(
									LoggerModel.this.messages.size() - 1,
									LoggerModel.this.messages.size() - 1);
						}
					});
				}
			}

			@Override
			public void flush() {}

			@Override
			public void close() throws SecurityException {}
		});
	}

}
