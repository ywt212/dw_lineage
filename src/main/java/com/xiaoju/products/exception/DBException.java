package com.xiaoju.products.exception;
/**
 * @author yangyang
 */
public class DBException extends RuntimeException {

	private static final long serialVersionUID = -5588025121452725145L;

	public DBException(String message, Throwable cause) {
		super(message, cause);
	}

	public DBException(String message) {
		super(message);
	}

	public DBException(Throwable cause) {
		super(cause);
	}

}
