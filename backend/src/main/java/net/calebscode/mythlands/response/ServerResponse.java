package net.calebscode.mythlands.response;

public class ServerResponse {

	private String message;
	private Object data;
	private boolean isError;
	
	public ServerResponse(String message) {
		this(message, null, false);
	}
	
	public ServerResponse(String message, boolean isError) {
		this(message, null, isError);
	}
	
	public ServerResponse(String message, Object data) {
		this(message, data, false);
	}
	
	public ServerResponse(String message, Object data, boolean isError) {
		this.message = message;
		this.data = data;
		this.isError = isError;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public boolean getIsError() {
		return isError;
	}
	
	public void setError(boolean isError) {
		this.isError = isError;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
}
