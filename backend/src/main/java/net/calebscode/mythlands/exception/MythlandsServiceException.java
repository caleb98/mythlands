package net.calebscode.mythlands.exception;

public class MythlandsServiceException extends Exception {

	private static final long serialVersionUID = 3805520807046684405L;

	public MythlandsServiceException() {
		super();
	}

	public MythlandsServiceException(String message, Throwable cause, boolean enableSuppression,boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MythlandsServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public MythlandsServiceException(String message) {
		super(message);
	}

	public MythlandsServiceException(Throwable cause) {
		super(cause);
	}
	
}
