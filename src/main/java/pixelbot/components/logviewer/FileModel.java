package pixelbot.components.logviewer;

import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.Level;

import javax.swing.Timer;
import javax.swing.event.TableModelEvent;
import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class FileModel extends BaseModel {
	private static final long serialVersionUID = 6027117841887309826L;
	List<LogRecord> messages = new ArrayList<LogRecord>();
	private File file;
	private Timer refreshTimer;
	private boolean refreshEnabled;

	private class LogRecord {

		private int thread_id;
		private String source_class_name;
		private String source_method_name;
		private Level level;
		private String logger_name;
		private String message;
		private long millis;
		private long sequence;
		private Date time;

		public LogRecord() {}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof LogRecord))
				return false;

			LogRecord logRecord = (LogRecord) obj;

			if ((logRecord.sequence == this.sequence) && logRecord.time.equals(this.time)
					&& logRecord.millis == this.millis)
				return true;
			else
				return false;

		}

		@SuppressWarnings("unused")
		public Integer getThreadID() {
			return Integer.valueOf(this.thread_id);
		}

		@SuppressWarnings("unused")
		public String getSourceClassName() {
			return this.source_class_name;
		}

		@SuppressWarnings("unused")
		public String getSourceMethodName() {
			return this.source_method_name;
		}

		public Level getLevel() {
			return this.level;
		}

		@SuppressWarnings("unused")
		public String getLoggerName() {
			return this.logger_name;
		}

		public String getMessage() {
			return this.message;
		}

		public long getMillis() {
			return this.millis;
		}

		public long getSequence() {
			return this.sequence;
		}

		@SuppressWarnings("unused")
		public Date getTime() {
			return this.time;
		}

		public void setThreadID(int value) {
			this.thread_id = value;
		}

		public void setSourceClassName(String value) {
			this.source_class_name = value;
		}

		public void setSourceMethodName(String value) {
			this.source_method_name = value;
		}

		public void setLevel(Level value) {
			this.level = value;
		}

		public void setLoggerName(String value) {
			this.logger_name = value;
		}

		public void setMessage(String value) {
			this.message = value;
		}

		public void setMillis(long value) {
			this.millis = value;
		}

		public void setSequence(long value) {
			this.sequence = value;
		}

		public void setTime(Date value) {
			this.time = value;
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

	}

	private class LogRecordSaxHandler extends DefaultHandler {

		String lastElement;

		LogRecord lastLogRecord;

		// Sometimes SAX Parser trunk text so we need to save last part of the text for each field
		String lastDate = null;
		String lastMillisec = null;
		String lastSequence = null;
		String lastLogger = null;
		String lastLevel = null;
		String lastClass = null;
		String lastThread = null;
		String lastMethod = null;
		String lastMessage = null;

		public LogRecordSaxHandler() {}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (this.lastElement.equals("date"))
				try {
					if (this.lastDate == null)
						this.lastDate = new String(ch, start, length);
					else
						this.lastDate = this.lastDate + new String(ch, start, length);
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					Date date = df.parse(this.lastDate);
					this.lastLogRecord.setTime(date);
					this.lastDate = null;
				} catch (ParseException e) {}
			else if (this.lastElement.equals("millis")) {
				if (this.lastMillisec == null)
					this.lastMillisec = new String(ch, start, length);
				else
					this.lastMillisec = this.lastMillisec + new String(ch, start, length);
				this.lastLogRecord.setMillis(Long.parseLong(this.lastMillisec));
			} else if (this.lastElement.equals("sequence")) {
				if (this.lastSequence == null)
					this.lastSequence = new String(ch, start, length);
				else
					this.lastSequence = this.lastSequence + new String(ch, start, length);
				this.lastLogRecord.setSequence(Long.parseLong(this.lastSequence));
			} else if (this.lastElement.equals("logger")) {
				if (this.lastLogger == null)
					this.lastLogger = new String(ch, start, length);
				else
					this.lastLogger = this.lastLogger + new String(ch, start, length);
				this.lastLogRecord.setLoggerName(this.lastLogger);
			} else if (this.lastElement.equals("level")) {
				if (this.lastLevel == null)
					this.lastLevel = new String(ch, start, length);
				else
					this.lastLevel = this.lastLevel + new String(ch, start, length);
				this.lastLogRecord.setLevel(Level.parse(this.lastLevel));
			} else if (this.lastElement.equals("class")) {
				if (this.lastClass == null)
					this.lastClass = new String(ch, start, length);
				else
					this.lastClass = this.lastClass + new String(ch, start, length);
				this.lastLogRecord.setSourceClassName(this.lastClass);
			} else if (this.lastElement.equals("method")) {
				if (this.lastMethod == null)
					this.lastMethod = new String(ch, start, length);
				else
					this.lastMethod = this.lastMethod + new String(ch, start, length);
				this.lastLogRecord.setSourceMethodName(this.lastMethod);
			} else if (this.lastElement.equals("thread")) {
				if (this.lastThread == null)
					this.lastThread = new String(ch, start, length);
				else
					this.lastThread = this.lastThread + new String(ch, start, length);
				this.lastLogRecord.setThreadID(Integer.parseInt(this.lastThread));
			} else if (this.lastElement.equals("message")) {
				if (this.lastMessage == null)
					this.lastMessage = new String(ch, start, length);
				else
					this.lastMessage = this.lastMessage + new String(ch, start, length);
				this.lastLogRecord.setMessage(this.lastMessage);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equals("record")) {
				FileModel.this.messages.add(this.lastLogRecord);
				this.lastDate = null;
				this.lastMillisec = null;
				this.lastSequence = null;
				this.lastLogger = null;
				this.lastLevel = null;
				this.lastClass = null;
				this.lastThread = null;
				this.lastMethod = null;
				this.lastMessage = null;
			}

		}

		@Override
		public InputSource resolveEntity(String publicId, String systemId) {
			// System.out.println( "PublicID: " + publicId + "------ SystemID: " + systemId);
			if (systemId.indexOf("logger.dtd") != -1)
				return new InputSource(getClass().getResourceAsStream(
						"/pixelbot/components/logviewer/resources/logger.dtd"));
			else
				return null;

		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			if (qName.equals("record"))
				this.lastLogRecord = new LogRecord();
			else
				this.lastElement = qName;
		}

		@Override
		public void error(SAXParseException e) throws SAXException {
			System.out.println("ERRORE: " + e.getMessage());
			super.error(e);
		}
	}

	public FileModel() {
		this.refreshTimer = new Timer(5000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					FileModel.this.parseFile();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

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
		return this.messages.get(rowIndex).getSequence();
	}

	@Override
	protected Level getLevel(int rowIndex) {
		return this.messages.get(rowIndex).getLevel();
	}

	@Override
	protected String getMessage(int rowIndex) {
		return this.messages.get(rowIndex).getMessage();
	}

	protected final void parseFile() throws ParserConfigurationException, SAXException, IOException {
		this.messages.clear();
		LogRecordSaxHandler logRecordHandler = new LogRecordSaxHandler();
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();

		SAXParser saxParser = parserFactory.newSAXParser();
		XMLReader reader = saxParser.getXMLReader();
		reader.setEntityResolver(logRecordHandler);
		reader.setContentHandler(logRecordHandler);
		reader.setErrorHandler(logRecordHandler);

		try {
			String fileURI = this.file.toURI().toString();
			reader.parse(fileURI);

		} catch (SAXException e) {
			if (!e.getMessage().equals("End of entity not allowed; an end tag is missing.")
					&& !e.getMessage().equals(
							"XML document structures must start and end within the same entity.")) {
				throw e;
			}
		}

		this.fireTableChanged(new TableModelEvent(this));
	}

	public boolean setFile(File value) {
		this.file = value;
		this.refreshTimer.stop();

		try {
			this.parseFile();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (this.refreshEnabled) {
			this.refreshTimer.start();
		}
		return true;
	}

	public void setRefreshEnabled(boolean value) {
		if (value) {
			try {
				this.parseFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.refreshTimer.start();
		} else {
			this.refreshTimer.stop();
		}
		this.refreshEnabled = value;
	}
}
