package net.mythlands.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.mythlands.core.effect.StatusEffectTemplate;

public interface MythlandsStatusEffectTemplateRepository extends JpaRepository<StatusEffectTemplate, String> {

}
