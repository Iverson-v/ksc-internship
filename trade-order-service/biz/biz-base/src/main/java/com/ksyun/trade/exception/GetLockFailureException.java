package com.ksyun.trade.exception;


/**
 * @author ksc
 */
public class GetLockFailureException extends RuntimeException {
	private static final long serialVersionUID = -1L;

	private String message;

	public GetLockFailureException(String message) {
		super(message);
		this.message = message;
	}

	public GetLockFailureException(String message, Throwable e) {
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
