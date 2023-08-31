package wow.commons.model.spell;

/**
 * User: POlszewski
 * Date: 2023-10-25
 */
public interface TriggeredSpell extends Spell {
	@Override
	default SpellType getType() {
		return SpellType.TRIGGERED_SPELL;
	}
}
