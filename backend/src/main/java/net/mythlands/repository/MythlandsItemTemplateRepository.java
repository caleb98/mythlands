package net.mythlands.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.mythlands.core.item.ItemTemplate;

@Repository
public interface MythlandsItemTemplateRepository<T extends ItemTemplate> extends JpaRepository<T, String> {

	public List<ItemTemplate> findByName(String name);
	
}
