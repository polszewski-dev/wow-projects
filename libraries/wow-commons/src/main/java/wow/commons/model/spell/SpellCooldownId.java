package wow.commons.model.spell;

/**
 * User: POlszewski
 * Date: 2024-12-03
 */
public record SpellCooldownId(int spellId) implements CooldownId {
	public static SpellCooldownId of(int spellId) {
		return new SpellCooldownId(spellId);
	}
}
