package de.cimt.talendcomp.log4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.FileAppender;
import org.apache.log4j.MDC;
import org.apache.log4j.spi.LoggingEvent;

public class TalendJobFileAppender extends FileAppender {
	
	private SimpleDateFormat date_param_format = new SimpleDateFormat("yyyyMMdd_HHmmss");
	private Map<String, Object> jobAttributes = new HashMap<String, Object>();
	
	public TalendJobFileAppender() {
		super();
	}
	
	@Override
	public void append(LoggingEvent event) {
		if (filter(event)) {
			super.append(event);
		}
	}

	private boolean filter(LoggingEvent event) {
		return ((String) jobAttributes.get("talendPid")).equals(MDC.get("talendPid"));
	}
	
	@Override
	public void setFile(String file) {
		if (file == null || file.trim().isEmpty()) {
			throw new IllegalArgumentException("file cannot be null or empty");
		}
		setBufferedIO(true);
		setBufferSize(1000);
		super.setFile(replacePlaceholders(file));
		setName(getFile());
	}
		
	public void setJobAttribute(String key, Object value) {
		if (key == null || key.trim().isEmpty()) {
			throw new IllegalArgumentException("key cannot be null");
		}
		if (value != null) {
			jobAttributes.put(key, value);
		}
	}
	
	private String replacePlaceholders(String template) {
		for (Map.Entry<String, Object> entry : jobAttributes.entrySet()) {
			template = template.replace("{" + entry.getKey() + "}", convertValue(entry.getValue()));
		}
		return template;
	}

	private String convertValue(Object value) {
		if (value != null) {
			if (value instanceof Date) {
				return date_param_format.format((Date) value);
			} else if (value instanceof String) {
				return (String) value;
			} else {
				return value.toString();
			}
		} else {
			return "";
		}
	}
	
}
