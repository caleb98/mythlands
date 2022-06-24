package net.calebscode.mythlands.service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.calebscode.mythlands.dto.ChatMessageDTO;
import net.calebscode.mythlands.dto.ChatReportDTO;
import net.calebscode.mythlands.dto.MythlandsUserDTO;
import net.calebscode.mythlands.entity.ChatGroup;
import net.calebscode.mythlands.entity.ChatMessage;
import net.calebscode.mythlands.entity.ChatReport;
import net.calebscode.mythlands.entity.MythlandsUser;
import net.calebscode.mythlands.exception.MythlandsServiceException;
import net.calebscode.mythlands.repository.MythlandsChatGroupRepository;
import net.calebscode.mythlands.repository.MythlandsChatReportRepository;
import net.calebscode.mythlands.repository.MythlandsChatRepository;
import net.calebscode.mythlands.repository.MythlandsUserRepository;

@Service
public class MythlandsChatService {

	@Autowired private MythlandsChatReportRepository reportRepository;
	@Autowired private MythlandsChatRepository chatRepository;
	@Autowired private MythlandsChatGroupRepository groupRepository;
	@Autowired private MythlandsUserRepository userRepository;
	
	@Transactional
	public ChatMessageDTO logMessage(int userId, int groupId, String messageText) throws MythlandsServiceException {
		MythlandsUser user = getUser(userId);
		ChatGroup group = getGroup(groupId);
		
		ChatMessage message = new ChatMessage();
		message.setTimestamp(Instant.now());
		message.setUser(user);
		message.setMessage(messageText);
		message.setGroup(group);
		message = chatRepository.save(message);
		
		return new ChatMessageDTO(message);
	}
	
	@Transactional
	public void addUserToGroup(int userId, int groupId) throws MythlandsServiceException {
		MythlandsUser user = getUser(userId);
		ChatGroup group = getGroup(groupId);
		group.getUsers().add(user);
	}
	
	@Transactional
	public void removeUserFromGroup(int userId, int groupId) throws MythlandsServiceException {
		ChatGroup group = getGroup(groupId);
		group.getUsers().removeIf(u -> u.getId() == userId);
	}
	
	@Transactional
	public Set<MythlandsUserDTO> getGroupUsers(int groupId) throws MythlandsServiceException {
		ChatGroup group = getGroup(groupId);
		Set<MythlandsUserDTO> users = group.getUsers().stream()
				.map(user -> new MythlandsUserDTO(user))
				.collect(Collectors.toSet());
		
		return users;
	}
	
	@Transactional
	public boolean hasChatPermissions(int userId, int groupId) throws MythlandsServiceException {
		ChatGroup group = getGroup(groupId);
		return group.hasUser(userId);
	}
	
	@Transactional
	public ChatReportDTO addChatReport(int messageId, int userId) throws MythlandsServiceException {
		MythlandsUser user = getUser(userId);
		ChatMessage message = chatRepository.findById(messageId).orElse(null);
		if(message == null) {
			return null;
		}
		
		ChatReport report = new ChatReport(message, user);
		report = reportRepository.save(report);
		
		return new ChatReportDTO(report);
	}
	
	private MythlandsUser getUser(int userId) throws MythlandsServiceException {
		MythlandsUser user = userRepository.findById(userId).orElse(null);
		if(user == null)  {
			throw new MythlandsServiceException("User with id " + userId + " not found.");
		}
		return user;
	}
	
	private ChatGroup getGroup(int groupId) throws MythlandsServiceException {
		Optional<ChatGroup> group = groupRepository.findById(groupId);
		if(group.isEmpty()) {
			throw new MythlandsServiceException("No chat group with id " + groupId + " was found.");
		}
		return group.get();
	}
	
}
