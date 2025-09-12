package wow.character.model.character;

import wow.character.model.Copyable;
import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.item.Consumable;
import wow.commons.model.item.ConsumableId;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ActivatedAbility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2024-11-21
 */
public class Consumables implements EffectCollection, Copyable<Consumables> {
	private final Map<ConsumableId, Consumable> availableConsumablesById = new HashMap<>();
	private final Map<String, Consumable> availableConsumablesByName = new HashMap<>();
	private final Map<ConsumableId, Consumable> enabledConsumables = new HashMap<>();

	public Optional<ActivatedAbility> getAbility(AbilityId abilityId) {
		return enabledConsumables.values().stream()
				.map(Consumable::getActivatedAbility)
				.filter(x -> x.getAbilityId() == abilityId)
				.findAny();
	}

	public List<Consumable> getList() {
		return List.copyOf(enabledConsumables.values());
	}

	public Stream<Consumable> getStream() {
		return enabledConsumables.values().stream();
	}

	public List<Consumable> getAvailable() {
		return List.copyOf(availableConsumablesById.values());
	}

	public boolean has(ConsumableId consumableId) {
		return getConsumable(consumableId)
				.map(consumable -> enabledConsumables.containsKey(consumable.getId()))
				.orElse(false);
	}

	public void reset() {
		enabledConsumables.clear();
	}

	public void setConsumableIds(List<ConsumableId> consumableIds) {
		reset();

		for (var consumableId : consumableIds) {
			enable(consumableId);
		}
	}

	public void setConsumableNames(List<String> names) {
		reset();

		for (var name : names) {
			enable(name);
		}
	}

	public void setAvailable(List<Consumable> consumables) {
		for (var consumable : consumables) {
			availableConsumablesById.put(consumable.getId(), consumable);
			availableConsumablesByName.put(consumable.getName(), consumable);
		}
	}

	public void enable(ConsumableId consumableId, boolean enabled) {
		var consumable = getConsumable(consumableId).orElseThrow();

		enable(consumable, enabled);
	}

	public void enable(ConsumableId consumableId) {
		enable(consumableId, true);
	}

	public void enable(String name, boolean enabled) {
		var consumable = getConsumable(name).orElseThrow();

		enable(consumable, enabled);
	}

	public void enable(String name) {
		enable(name, true);
	}

	private void enable(Consumable consumable, boolean enabled) {
		if (enabled) {
			enabledConsumables.put(consumable.getId(), consumable);
		} else {
			enabledConsumables.remove(consumable.getId());
		}
	}

	private Optional<Consumable> getConsumable(ConsumableId consumableId) {
		var consumable = availableConsumablesById.get(consumableId);

		return Optional.ofNullable(consumable);
	}

	private Optional<Consumable> getConsumable(String name) {
		var consumable = availableConsumablesByName.get(name);

		return Optional.ofNullable(consumable);
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		for (var consumable : enabledConsumables.values()) {
			collector.addActivatedAbility(consumable.getActivatedAbility());
		}
	}

	@Override
	public Consumables copy() {
		var copy = new Consumables();
		copy.availableConsumablesById.putAll(this.availableConsumablesById);
		copy.availableConsumablesByName.putAll(this.availableConsumablesByName);
		copy.enabledConsumables.putAll(this.enabledConsumables);
		return copy;
	}
}
