package net.calebscode.mythlands.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.calebscode.mythlands.core.item.MythlandsItem;

@Repository
public interface MythlandsItemRepository extends JpaRepository<MythlandsItem, UUID> {

	public Optional<MythlandsItem> findById(UUID id);
	
}
