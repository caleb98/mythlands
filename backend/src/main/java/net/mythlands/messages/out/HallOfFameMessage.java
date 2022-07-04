package net.mythlands.messages.out;

import java.util.ArrayList;
import java.util.List;

import net.mythlands.dto.MythlandsCharacterDTO;

public class HallOfFameMessage {

	public final List<HallOfFameEntry> entries;
	
	public HallOfFameMessage(List<MythlandsCharacterDTO> heroes, int pageSize, int pageNum) {
		int rank = pageSize * pageNum + 1;
		entries = new ArrayList<>();
		for(MythlandsCharacterDTO hero : heroes) {
			entries.add(new HallOfFameEntry(rank++, hero));
		}
	}
	
	public class HallOfFameEntry {
		
		final int rank;
		final String firstName;
		final String lastName;
		final String username;
		final int level;
		final int xp;
		final boolean isDeceased;
		
		HallOfFameEntry(int rank, MythlandsCharacterDTO character) {
			this.rank = rank;
			
			this.firstName = character.firstName;
			this.lastName = character.lastName;
			this.username = character.ownerName;
			this.level = character.level;
			this.xp = character.xp;
			this.isDeceased = character.isDeceased;
		}

		public int getRank() {
			return rank;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}
		
		public String getUsername() {
			return username;
		}

		public int getLevel() {
			return level;
		}

		public int getXp() {
			return xp;
		}

		public boolean isDeceased() {
			return isDeceased;
		}
		
	}
	
}
