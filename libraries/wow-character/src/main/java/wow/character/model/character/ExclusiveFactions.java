package wow.character.model.character;

import wow.character.model.Copyable;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.FactionExclusionGroupId;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2023-05-13
 */
public class ExclusiveFactions implements Copyable<ExclusiveFactions> {
	private final Map<String, Faction> availableFactionsByName = new HashMap<>();
	private final Map<FactionExclusionGroupId, Faction> factionsByGroup = new EnumMap<>(FactionExclusionGroupId.class);

	public ExclusiveFactions(List<Faction> availableFactions) {
		for (var faction : availableFactions) {
			if (faction.getExclusionGroupId() == null) {
				throw new IllegalArgumentException();
			}
			availableFactionsByName.put(faction.getName(), faction);
		}
	}

	public boolean has(String name) {
		var faction = getFaction(name).orElseThrow(() -> new IllegalArgumentException(name));

		return get(faction.getExclusionGroupId()) == faction;
	}

	public Faction get(FactionExclusionGroupId groupId) {
		return factionsByGroup.get(groupId);
	}

	public List<Faction> getList() {
		return List.copyOf(factionsByGroup.values());
	}

	public Map<FactionExclusionGroupId, Faction> getFactionsByGroup() {
		return Map.copyOf(factionsByGroup);
	}

	public List<String> getNameList() {
		return factionsByGroup.values().stream()
				.map(Faction::getName)
				.toList();
	}

	public List<Faction> getAvailable() {
		return List.copyOf(availableFactionsByName.values());
	}

	public void enable(String name) {
		var faction = getFaction(name).orElseThrow();

		factionsByGroup.put(faction.getExclusionGroupId(), faction);
	}

	public void set(List<String> names) {
		factionsByGroup.clear();

		for (var name : names) {
			enable(name);
		}
	}

	private Optional<Faction> getFaction(String name) {
		return Optional.ofNullable(availableFactionsByName.get(name));
	}

	@Override
	public ExclusiveFactions copy() {
		var copy = new ExclusiveFactions(List.copyOf(availableFactionsByName.values()));
		copy.factionsByGroup.putAll(factionsByGroup);
		return copy;
	}
}
