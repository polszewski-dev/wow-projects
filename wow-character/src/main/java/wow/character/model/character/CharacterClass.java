package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.character.model.build.BuildId;
import wow.commons.model.categorization.*;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.config.Described;
import wow.commons.model.config.Description;
import wow.commons.model.spells.SpellSchool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
@AllArgsConstructor
@Getter
public class CharacterClass implements Described {
	@NonNull
	private final CharacterClassId characterClassId;

	@NonNull
	private final Description description;

	@NonNull
	private final List<Race> races = new ArrayList<>();

	@NonNull
	private final Set<ArmorProfficiency> armorProfficiencies;

	@NonNull
	private final Set<WeaponProfficiency> weaponProfficiencies;

	private final boolean dualWield;

	@NonNull
	private final List<SpellSchool> spellSchools;

	private final BuildId defaultBuildId;

	private final GameVersion gameVersion;
	private final List<BaseStatInfo> baseStatInfos = new ArrayList<>();

	public boolean canEquip(ItemSlot itemSlot, ItemType itemType, ItemSubType itemSubType) {
		switch (itemType.getCategory()) {
			case WEAPON:
				if (itemSlot == ItemSlot.OFF_HAND && !isWeaponAllowedInOffHand(itemType)) {
					return false;
				}
				return matches(itemType, (WeaponSubType) itemSubType);
			case ARMOR:
				return matches((ArmorSubType) itemSubType);
			case ACCESSORY:
				return true;
			default:
				return false;
		}
	}

	private boolean isWeaponAllowedInOffHand(ItemType itemType) {
		return dualWield || itemType == ItemType.OFF_HAND;
	}

	private boolean matches(ItemType weaponType, WeaponSubType weaponSubType) {
		return weaponProfficiencies.stream().anyMatch(x -> x.matches(weaponType, weaponSubType));
	}

	private boolean matches(ArmorSubType armorSubType) {
		return armorProfficiencies.stream().anyMatch(x -> x.matches(armorSubType));
	}

	public BaseStatInfo getBaseStatInfo(int level, RaceId raceId) {
		return baseStatInfos.stream()
				.filter(x -> x.getLevel() == level && x.getRaceId() == raceId)
				.findFirst()
				.orElseThrow();
	}

	public boolean isAvailableTo(RaceId raceId) {
		return races.stream().anyMatch(x -> x.getRaceId() == raceId);
	}

	@Override
	public String toString() {
		return getName();
	}
}
