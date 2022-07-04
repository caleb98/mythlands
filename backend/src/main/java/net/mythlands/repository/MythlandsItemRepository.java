package net.mythlands.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.mythlands.core.item.ItemInstance;

@Repository
public interface MythlandsItemRepository extends JpaRepository<ItemInstance, UUID> {
	
}
