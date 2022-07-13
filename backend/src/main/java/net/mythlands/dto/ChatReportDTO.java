package net.mythlands.dto;

import net.mythlands.core.chat.ChatReport;

public class ChatReportDTO {

	public final int id;
	public final ChatMessageDTO message;
	public final MythlandsUserDTO reporter;
	
	public ChatReportDTO(ChatReport report) {
		id = report.getId();
		message = new ChatMessageDTO(report.getMessageReported());
		reporter = new MythlandsUserDTO(report.getReporter());
	}
	
}
