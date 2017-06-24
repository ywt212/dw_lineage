package com.xiaoju.products.exception;
/**
 * 不支持解析异常
 * @author yangyang
 */
public class UnSupportedException extends RuntimeException {

	private static final long serialVersionUID = -5588025121452725145L;

	public UnSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnSupportedException(String message) {
		super(message);
	}

	public UnSupportedException(Throwable cause) {
		super(cause);
	}
}
