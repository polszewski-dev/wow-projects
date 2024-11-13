package wow.commons.model.spell;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
public sealed interface CooldownId permits AbilityCooldownId, GroupCooldownId {
	static CooldownId of(AbilityId abilityId) {
		return AbilityCooldownId.of(abilityId);
	}
}
