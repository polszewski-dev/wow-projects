package wow.character.model.asset;

import wow.character.model.character.PlayerCharacter;
import wow.commons.model.config.*;
import wow.commons.model.spell.AbilityId;
import wow.commons.util.EnumUtil;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-12-13
 */
public record Asset(
		AssetId id,
		Description description,
		TimeRestriction timeRestriction,
		CharacterRestriction characterRestriction,
		String improvedByTalent,
		Scope scope,
		ExclusionGroup exclusionGroup,
		BuffCommand buffCommand
) implements CharacterRestricted, TimeRestricted, Described {
	public enum ExclusionGroup {
		ARMOR,
		CURSE,
		DEMONIC_SACRIFICE,
		PRAYER_OF_FORTITUDE,
		PRAYER_OF_SPIRIT,
		GIFT_OF_THE_WILD,
		FORM,
		FIRE_TOTEM,
		AIR_TOTEM,
		WATER_TOTEM,
		BLESSING,
		AURA,
		;

		public static ExclusionGroup parse(String value) {
			return EnumUtil.parse(value, values());
		}
	}

	public enum Scope {
		PERSONAL,
		PARTY,
		RAID,
		;

		public static Scope parse(String value) {
			return EnumUtil.parse(value, values());
		}
	}

	public enum BuffTarget {
		EACH_RAID_MEMBER,
		EACH_PARTY_FIRST_MEMBER,
		SELF,
		TARGET_ENEMY;

		public static BuffTarget parse(String value) {
			return EnumUtil.parse(value, values());
		}
	}

	public sealed interface BuffCommand {
		BuffTarget target();
	}

	public record CastAbility(BuffTarget target, AbilityId abilityId) implements BuffCommand {
		public CastAbility {
			Objects.requireNonNull(target);
			Objects.requireNonNull(abilityId);
		}
	}

	public record ExecuteScript(BuffTarget target, String scriptName) implements BuffCommand {
		public ExecuteScript {
			Objects.requireNonNull(target);
			Objects.requireNonNull(scriptName);
		}
	}

	public Asset {
		Objects.requireNonNull(id);
		Objects.requireNonNull(description);
		Objects.requireNonNull(timeRestriction);
		Objects.requireNonNull(characterRestriction);
		Objects.requireNonNull(scope);
	}

	public String name() {
		return getName();
	}

	@Override
	public Description getDescription() {
		return description;
	}

	@Override
	public CharacterRestriction getCharacterRestriction() {
		return characterRestriction;
	}

	@Override
	public TimeRestriction getTimeRestriction() {
		return timeRestriction;
	}

	public int getEffectiveRank(PlayerCharacter player) {
		if (improvedByTalent == null) {
			return 0;
		}

		return player.getTalents().getRank(improvedByTalent);
	}
}
