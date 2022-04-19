package com.jhopesoft.framework.exception;

/**
 *
 * @author 蒋锋
 * 
 */

public class WorkFlowException extends JavaException {
	private static final long serialVersionUID = 1L;

	public WorkFlowException() {
		super();
	}

	public WorkFlowException(String message) {
		super(message);
	}

	public WorkFlowException(Throwable cause) {
		super(cause);
	}

	public WorkFlowException(String message, Throwable cause) {
		super(message, cause);
	}

}
