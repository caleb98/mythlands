package net.mythlands.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.mythlands.core.item.ItemAffixTemplate;

@Repository
public interface MythlandsAffixRepository extends JpaRepository<ItemAffixTemplate, String> {

}
