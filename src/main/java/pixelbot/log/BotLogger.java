package pixelbot.log;

import java.io.*;
import java.util.logging.*;

import pixelbot.misc.Tools;

public class BotLogger {
	public static void init() throws SecurityException, IOException {
		ScriptLogLevel.init();

		// initialize logging to go to rolling log file
		LogManager logManager = LogManager.getLogManager();
		logManager.reset();

		Logger logger;
		LoggingOutputStream los;
		Handler handler;

		// log file max size 10K, 3 rolling files, append-on-open
		logger = Logger.getLogger("");
		handler = new FileHandler(new File(Tools.workdir, "log.%g.xml").getAbsolutePath(), 1000000,
				5, false);
		handler.setFormatter(new XMLFormatter());
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);

		logger = Logger.getLogger("stdout");
		handler = new OutStreamHandler(System.out);
		logger.addHandler(handler);
		los = new LoggingOutputStream(logger, StdOutErrLevel.STDOUT);
		System.setOut(new PrintStream(los, true));

		logger = Logger.getLogger("stderr");
		handler = new OutStreamHandler(System.err);
		// logger.setUseParentHandlers(false);
		logger.addHandler(handler);
		los = new LoggingOutputStream(logger, StdOutErrLevel.STDERR);
		System.setErr(new PrintStream(los, true));

		//

		// preserve old stdout/stderr streams in case they might be useful
		// PrintStream stdout = System.out;
		// PrintStream stderr = System.err;

		// now rebind stdout/stderr to logger

		// logger = Logger.getLogger("stdout");
		// los = new LoggingOutputStream(logger, StdOutErrLevel.STDOUT);
		// System.setOut(new PrintStream(los, true));

	}
}
