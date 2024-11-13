package wow.commons.model.spell;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
public record AbilityCooldownId(AbilityId abilityId) implements CooldownId {
	public static AbilityCooldownId of(AbilityId abilityId) {
		return new AbilityCooldownId(abilityId);
	}
}
