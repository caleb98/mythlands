package net.mythlands.messages.out;

public class ServerMessage {

	private String message;
	private Object data;
	private boolean isError;
	
	public ServerMessage(String message) {
		this(message, null, false);
	}
	
	public ServerMessage(String message, boolean isError) {
		this(message, null, isError);
	}
	
	public ServerMessage(String message, Object data) {
		this(message, data, false);
	}
	
	public ServerMessage(String message, Object data, boolean isError) {
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
