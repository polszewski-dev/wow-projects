package wow.commons.model.spell;

/**
 * User: POlszewski
 * Date: 2024-12-03
 */
public record SpellCooldownId(SpellId spellId) implements CooldownId {
	public static SpellCooldownId of(SpellId spellId) {
		return new SpellCooldownId(spellId);
	}
}
