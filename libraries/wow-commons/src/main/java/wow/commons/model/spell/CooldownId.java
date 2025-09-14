package wow.commons.model.spell;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
public sealed interface CooldownId permits GcdCooldownId, AbilityCooldownId, SpellCooldownId, GroupCooldownId {
	static AbilityCooldownId of(AbilityId abilityId) {
		return AbilityCooldownId.of(abilityId);
	}

	static SpellCooldownId of(SpellId spellId) {
		return SpellCooldownId.of(spellId);
	}

	static CooldownId of(Spell spell) {
		if (spell instanceof Ability a) {
			return of(a.getAbilityId());
		}
		return of(spell.getId());
	}
}
