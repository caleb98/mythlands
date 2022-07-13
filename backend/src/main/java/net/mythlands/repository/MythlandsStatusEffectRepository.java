package net.mythlands.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.mythlands.core.effect.StatusEffectInstance;

public interface MythlandsStatusEffectRepository extends JpaRepository<StatusEffectInstance, Integer> {

}
