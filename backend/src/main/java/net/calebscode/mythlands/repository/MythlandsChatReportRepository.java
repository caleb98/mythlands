package net.calebscode.mythlands.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.calebscode.mythlands.entity.ChatReport;

@Repository
public interface MythlandsChatReportRepository extends JpaRepository<ChatReport, Integer> {

}
