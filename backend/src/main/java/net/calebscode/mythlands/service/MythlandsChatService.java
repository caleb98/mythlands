package net.calebscode.mythlands.service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.calebscode.mythlands.dto.ChatMessageDTO;
import net.calebscode.mythlands.dto.MythlandsUserDTO;
import net.calebscode.mythlands.entity.ChatGroup;
import net.calebscode.mythlands.entity.ChatMessage;
import net.calebscode.mythlands.entity.MythlandsUser;
import net.calebscode.mythlands.exception.ChatGroupNotFoundException;
import net.calebscode.mythlands.exception.UserNotFoundException;
import net.calebscode.mythlands.repository.MythlandsChatGroupRepository;
import net.calebscode.mythlands.repository.MythlandsChatRepository;
import net.calebscode.mythlands.repository.MythlandsUserRepository;

@Service
public class MythlandsChatService {

	@Autowired private MythlandsChatRepository chatRepository;
	@Autowired private MythlandsChatGroupRepository groupRepository;
	@Autowired private MythlandsUserRepository userRepository;
	
	@Transactional
	public ChatMessageDTO logMessage(int userId, int groupId, String messageText) 
			throws UserNotFoundException, ChatGroupNotFoundException {
		MythlandsUser user = userRepository.findById(userId).orElse(null);
		if(user == null)  {
			throw new UserNotFoundException("User with id " + userId + " not found.");
		}
		
		ChatGroup group = groupRepository.findById(groupId).orElse(null);
		if(group == null) {
			throw new ChatGroupNotFoundException("Chat group with id " + groupId + " not found.");
		}
		
		ChatMessage message = new ChatMessage();
		message.setTimestamp(Instant.now());
		message.setUser(user);
		message.setMessage(messageText);
		message.setGroup(group);
		message = chatRepository.save(message);
		
		return new ChatMessageDTO(message);
	}
	
	@Transactional
	public Set<MythlandsUserDTO> getGroupUsers(int groupId) throws ChatGroupNotFoundException {
		ChatGroup group = getGroup(groupId);
		Set<MythlandsUserDTO> users = group.getUsers().stream()
				.map(user -> new MythlandsUserDTO(user))
				.collect(Collectors.toSet());
		
		return users;
	}
	
	@Transactional
	public boolean hasChatPermissions(int userId, int groupId) throws ChatGroupNotFoundException {
		ChatGroup group = getGroup(groupId);
		return group.hasUser(userId);
	}
	
	private ChatGroup getGroup(int groupId) throws ChatGroupNotFoundException {
		Optional<ChatGroup> group = groupRepository.findById(groupId);
		if(group.isEmpty()) {
			throw new ChatGroupNotFoundException("No chat group with id " + groupId + " was found.");
		}
		return group.get();
	}
	
}
