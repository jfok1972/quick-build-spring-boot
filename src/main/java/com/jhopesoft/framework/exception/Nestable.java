package com.jhopesoft.framework.exception;

/**
 *
 * @author 蒋锋
 * 
 */

public abstract interface Nestable {
	/**
	 * getCause
	 * 
	 * @return
	 */
	public abstract Throwable getCause();

	/**
	 * getMessage
	 * 
	 * @return
	 */
	public abstract String getMessage();

	/**
	 * getMessageThrowable
	 * 
	 * @return
	 */
	public abstract Throwable getMessageThrowable();

	/**
	 * getFullMessage
	 * 
	 * @return
	 */
	public abstract String getFullMessage();

	/**
	 * getOriginalMessage
	 * 
	 * @return
	 */
	public abstract String getOriginalMessage();

	/**
	 * getOriginalThrowable
	 * 
	 * @return
	 */
	public abstract Throwable getOriginalThrowable();
}