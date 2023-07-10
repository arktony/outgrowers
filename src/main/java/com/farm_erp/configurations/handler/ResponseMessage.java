package com.farm_erp.configurations.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ResponseMessage {

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public String timestamp = LocalDateTime.now().format(formatter);

	public String message;

	public int code = 200;

	public Object data;

	public ResponseMessage(String message) {
		super();
		this.message = message;
	}

	public ResponseMessage(String message, Object data) {
		super();
		this.message = message;
		this.data = data;
	}

}
