package net.mythlands.dto;

import net.mythlands.core.chat.ChatReport;

public class ChatReportDTO {

	public final ChatMessageDTO message;
	public final MythlandsUserDTO reporter;
	
	public ChatReportDTO(ChatReport report) {
		message = new ChatMessageDTO(report.getMessageReported());
		reporter = new MythlandsUserDTO(report.getReporter());
	}
	
}
