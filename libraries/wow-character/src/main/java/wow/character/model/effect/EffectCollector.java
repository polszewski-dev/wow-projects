package wow.character.model.effect;

import wow.character.model.equipment.ItemSockets;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.ItemSet;
import wow.commons.model.spell.ActivatedAbility;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-01-05
 */
public interface EffectCollector {
	default void addEffect(Effect effect) {
		addEffect(effect, 1);
	}

	void addEffect(Effect effect, int stackCount);

	default void addEffects(List<Effect> effects) {
		for (var effect : effects) {
			addEffect(effect);
		}
	}

	void addActivatedAbility(ActivatedAbility activatedAbility);

	void addItemSockets(ItemSockets itemSockets);

	void addItemSet(ItemSet itemSet);
}
