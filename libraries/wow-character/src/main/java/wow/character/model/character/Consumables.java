package wow.character.model.character;

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
public class Consumables extends Options<Consumable, ConsumableId> implements EffectCollection {
	private final Character owner;

	public Consumables(Character owner) {
		this.owner = owner;
	}

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
	protected ConsumableId getId(Consumable consumable) {
		return consumable.getId();
	}

	@Override
	protected String getName(Consumable consumable) {
		return consumable.getName();
	}

	@Override
	protected String getKey(Consumable consumable) {
		if (consumable.getExclusionGroup() != null) {
			return consumable.getExclusionGroup().toString();
		}

		return consumable.getName();
	}
}
