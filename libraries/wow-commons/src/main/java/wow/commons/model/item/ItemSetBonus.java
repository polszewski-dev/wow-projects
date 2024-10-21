package wow.commons.model.item;

import wow.commons.model.effect.Effect;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public record ItemSetBonus(int numPieces, Effect bonusEffect) {
	public ItemSetBonus {
		Objects.requireNonNull(bonusEffect);
	}

	@Override
	public String toString() {
		return String.format("(%s) Set: %s", numPieces, bonusEffect.getTooltip());
	}
}
