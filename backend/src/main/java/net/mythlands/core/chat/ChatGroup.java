package net.mythlands.core.chat;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.mythlands.core.MythlandsUser;

@Entity
@Table(name = "chat_group")
public class ChatGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String name;
	
	@OneToMany
	@JoinTable(
			name = "chat_group_user",
			inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	private Set<MythlandsUser> users = new HashSet<>();

	public String getGroupName() {
		return name;
	}

	public void setGroupName(String groupName) {
		this.name = groupName;
	}

	public Set<MythlandsUser> getUsers() {
		return users;
	}

	public Integer getId() {
		return id;
	}
	
	public boolean hasUser(int id) {
		return users.stream().anyMatch(c -> c.getId() == id);
	}
	
}
