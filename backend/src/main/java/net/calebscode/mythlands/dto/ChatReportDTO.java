package net.calebscode.mythlands.dto;

import net.calebscode.mythlands.entity.ChatReport;

public class ChatReportDTO {

	public final ChatMessageDTO message;
	public final MythlandsUserDTO reporter;
	
	public ChatReportDTO(ChatReport report) {
		message = new ChatMessageDTO(report.getMessageReported());
		reporter = new MythlandsUserDTO(report.getReporter());
	}
	
}
