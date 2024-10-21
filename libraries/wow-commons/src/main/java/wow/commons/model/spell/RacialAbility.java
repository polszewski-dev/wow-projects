package wow.commons.model.spell;

/**
 * User: POlszewski
 * Date: 2023-10-25
 */
public interface RacialAbility extends Ability {
	@Override
	default SpellType getType() {
		return SpellType.RACIAL_ABILITY;
	}

	@Override
	default int getRank() {
		return 0;
	}
}
