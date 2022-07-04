package net.mythlands.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.mythlands.core.chat.ChatReport;

@Repository
public interface MythlandsChatReportRepository extends JpaRepository<ChatReport, Integer> {

}
