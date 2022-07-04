package net.mythlands.core.chat;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.mythlands.core.MythlandsUser;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private Instant timestamp;
	
	@ManyToOne
	private MythlandsUser user;
	
	@Column(nullable = false)
	private String message;
	
	@ManyToOne
	private ChatGroup group;

	public MythlandsUser getUser() {
		return user;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public void setUser(MythlandsUser user) {
		this.user = user;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ChatGroup getGroup() {
		return group;
	}

	public void setGroup(ChatGroup group) {
		this.group = group;
	}

	public Integer getId() {
		return id;
	}

}
