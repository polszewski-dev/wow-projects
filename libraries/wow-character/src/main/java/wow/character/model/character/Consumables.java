package wow.character.model.character;

import wow.character.model.Copyable;
import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.item.Consumable;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ActivatedAbility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2024-11-21
 */
public class Consumables implements EffectCollection, Copyable<Consumables> {
	private final Map<String, Consumable> availableConsumablesByName = new HashMap<>();
	private final Map<String, Consumable> enabledConsumables = new HashMap<>();

	public Optional<ActivatedAbility> getAbility(AbilityId abilityId) {
		return enabledConsumables.values().stream()
				.map(Consumable::getActivatedAbility)
				.filter(x -> x.getAbilityId() == abilityId)
				.findAny();
	}

	public void reset() {
		enabledConsumables.clear();
	}

	public void setConsumables(List<String> names) {
		reset();

		for (var name : names) {
			enable(name);
		}
	}

	public List<Consumable> getList() {
		return List.copyOf(enabledConsumables.values());
	}

	public List<Consumable> getAvailable() {
		return List.copyOf(availableConsumablesByName.values());
	}

	public void setAvailable(List<Consumable> consumables) {
		for (Consumable consumable : consumables) {
			availableConsumablesByName.put(consumable.getName(), consumable);
		}
	}

	public void enable(String name, boolean enabled) {
		if (enabled) {
			enable(name);
		} else {
			disable(name);
		}
	}

	public void enable(String name) {
		var consumable = availableConsumablesByName.get(name);

		if (consumable == null) {
			throw new IllegalArgumentException("No consumable: " + name);
		}

		enabledConsumables.put(name, consumable);
	}

	public void disable(String name) {
		enabledConsumables.remove(name);
	}

	public boolean has(String name) {
		return enabledConsumables.containsKey(name);
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
		copy.availableConsumablesByName.putAll(this.availableConsumablesByName);
		copy.enabledConsumables.putAll(this.enabledConsumables);
		return copy;
	}
}
