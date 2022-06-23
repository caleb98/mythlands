package net.calebscode.mythlands.service;

import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.calebscode.mythlands.core.item.ItemRarity;
import net.calebscode.mythlands.core.item.MythlandsItem;
import net.calebscode.mythlands.dto.MythlandsItemDTO;
import net.calebscode.mythlands.repository.MythlandsItemRepository;

@Service
public class MythlandsItemService {

	@Autowired private MythlandsItemRepository itemRepository;
	
	@Transactional
	public MythlandsItemDTO createNewItem(String name, ItemRarity rarity) {
		MythlandsItem item = new MythlandsItem(name, rarity);
		item = itemRepository.save(item);
		return new MythlandsItemDTO(item);
	}
	
	public MythlandsItemDTO getItem(UUID id) {
		Optional<MythlandsItem> item = itemRepository.findById(id);
		if(item.isPresent()) {
			return new MythlandsItemDTO(item.get());
		}
		else {
			return null;
		}
	}
	
	public void deleteItem(UUID id) {
		itemRepository.deleteById(id);
	}
	
}
