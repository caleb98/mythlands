package net.calebscode.mythlands.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.calebscode.mythlands.core.item.ItemTemplate;

@Repository
public interface MythlandsItemTemplateRepository<T extends ItemTemplate> extends JpaRepository<T, Integer> {

	public List<ItemTemplate> findByName(String name);
	
}
