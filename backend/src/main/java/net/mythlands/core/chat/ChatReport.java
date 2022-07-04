package net.mythlands.core.chat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.mythlands.core.MythlandsUser;

@Entity
@Table(name = "chat_reports")
public class ChatReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	private ChatMessage messageReported;
	
	@ManyToOne
	private MythlandsUser reporter;

	protected ChatReport() {}
	
	public ChatReport(ChatMessage message, MythlandsUser reporter) {
		messageReported = message;
		this.reporter = reporter;
	}
	
	public ChatMessage getMessageReported() {
		return messageReported;
	}

	public void setMessageReported(ChatMessage messageReported) {
		this.messageReported = messageReported;
	}

	public MythlandsUser getReporter() {
		return reporter;
	}

	public void setReporter(MythlandsUser reporter) {
		this.reporter = reporter;
	}

	public int getId() {
		return id;
	}
	
}
