package logger;

import com.fxcm.fix.UTCTimestamp;
import logger.Logger.LogType;

public class Log{
	
	private UTCTimestamp logTime;
	private LogType logType;
	private String logContent;
	
	public Log(UTCTimestamp logTime, LogType logType, String logContent) {
		this.logType = logType;
		this.logContent = logContent;
		this.logTime = logTime;
	}

	public UTCTimestamp getLogTime() {
		return logTime;
	}

	public LogType getLogType() {
		return logType;
	}

	public String getLogContent() {
		return logContent;
	}

	@Override
	public String toString() {
		return "Log [logTime=" + logTime + ", logType=" + logType + ", logContent=" + logContent + "]";
	}
	
	
}
