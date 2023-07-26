package com.ksyun.trade.exception;

/**
 * @author ksc
 */
public class CasRetryException extends RuntimeException {
	private static final long serialVersionUID = -1L;

	private String message;

	public CasRetryException(String message) {
		super(message);
		this.message = message;
	}

	public CasRetryException(String message, Throwable e) {
		super(message, e);
		this.message = message;
	}

 	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
