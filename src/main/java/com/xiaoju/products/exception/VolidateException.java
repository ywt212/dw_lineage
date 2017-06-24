package com.xiaoju.products.exception;
/**
 * @author yangyang
 */
public class VolidateException extends RuntimeException {

	private static final long serialVersionUID = -5588025121452725145L;
	
	public VolidateException(String message, Throwable cause) {
		super(message, cause);
	}

	public VolidateException(String message) {
		super(message);
	}

	public VolidateException(Throwable cause) {
		super(cause);
	}

}
