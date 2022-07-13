package net.mythlands.response;

public class TimestampedRO<T> {

	public final long timestamp;
	public final T data;
	
	public TimestampedRO(T data) {
		timestamp = System.currentTimeMillis();
		this.data = data;
	}
	
}
