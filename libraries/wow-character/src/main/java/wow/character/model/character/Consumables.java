package wow.character.model.character;

import wow.character.model.Copyable;
import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.item.Consumable;
import wow.commons.model.item.ConsumableId;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ActivatedAbility;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2024-11-21
 */
public class Consumables extends Options<Consumable, ConsumableId> implements EffectCollection, Copyable<Consumables> {
	public Optional<ActivatedAbility> getAbility(AbilityId abilityId) {
		return getStream()
				.map(Consumable::getActivatedAbility)
				.filter(x -> x.getAbilityId().equals(abilityId))
				.findAny();
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		forEach(consumable -> collector.addActivatedAbility(consumable.getActivatedAbility()));
	}

	@Override
	public Consumables copy() {
		var copy = new Consumables();
		copyInto(copy);
		return copy;
	}

	@Override
	protected ConsumableId getId(Consumable consumable) {
		return consumable.getId();
	}

	@Override
	protected String getName(Consumable consumable) {
		return consumable.getName();
	}

	@Override
	protected String getKey(Consumable consumable) {
		return consumable.getName();
	}
}
