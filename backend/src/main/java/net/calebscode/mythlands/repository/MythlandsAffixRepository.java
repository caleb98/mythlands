package net.calebscode.mythlands.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.calebscode.mythlands.core.item.ItemAffix;

@Repository
public interface MythlandsAffixRepository extends JpaRepository<ItemAffix, String> {

}
