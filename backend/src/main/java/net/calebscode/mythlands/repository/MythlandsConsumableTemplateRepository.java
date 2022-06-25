package net.calebscode.mythlands.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import net.calebscode.mythlands.core.item.ConsumableItemTemplate;
import net.calebscode.mythlands.core.item.ItemRarity;

@Repository
public interface MythlandsConsumableTemplateRepository extends MythlandsItemTemplateRepository<ConsumableItemTemplate> {

	public List<ConsumableItemTemplate> findByNameAndIconAndDescriptionAndStackSizeAndRarityAndOnConsumeId(
			String name, String icon, String description, int stackSize, ItemRarity rarity, String onConsumeId);
	
}
