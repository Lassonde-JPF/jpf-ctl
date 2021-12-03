package logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		
		String datePattern = "dd-MM-yyyy HH:mm:ss";
		DateFormat df = new SimpleDateFormat(datePattern);
		Date date = new Date(record.getMillis());
		
		// Append Date
        sb.append(df.format(date))
        .append(' ')
        // Append Level
        .append('[')
        .append(record.getLevel().getLocalizedName())
        .append(']')
        .append(' ')
        // Append Associated Class
        .append(record.getLoggerName())
        .append(':')
        .append(' ')
        // Append Message
        .append(formatMessage(record))
        .append(LINE_SEPARATOR);

        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
                // ignore (empty log call)
            }
        }
        
		return sb.toString();
	}
	

}
