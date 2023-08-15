package wow.simulator.model.unit;

import wow.commons.model.spells.Cost;
import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.Spell;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;

import java.util.EnumMap;
import java.util.Map;

import static wow.commons.model.spells.ResourceType.HEALTH;
import static wow.commons.model.spells.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2023-08-12
 */
public class UnitResources implements SimulationContextSource {
	private final Unit owner;

	private final Map<ResourceType, UnitResource> resourceMap = new EnumMap<>(ResourceType.class);

	public UnitResources(Unit owner) {
		this.owner = owner;
		add(new UnitResource(HEALTH, owner));
		add(new UnitResource(MANA, owner));
	}

	public int getCurrentHealth() {
		return get(HEALTH).getCurrent();
	}

	public int getCurrentMana() {
		return get(MANA).getCurrent();
	}

	public void setHealth(int current, int max) {
		get(HEALTH).set(current, max);
	}

	public void setMana(int current, int max) {
		get(MANA).set(current, max);
	}

	public int increaseHealth(int amount, Spell spell) {
		return get(HEALTH).increase(amount, spell);
	}

	public int decreaseHealth(int amount, Spell spell) {
		return get(HEALTH).decrease(amount, spell);
	}

	public int increaseMana(int amount, Spell spell) {
		return get(MANA).increase(amount, spell);
	}

	public int decreaseMana(int amount, Spell spell) {
		return get(MANA).decrease(amount, spell);
	}

	public boolean canPay(Cost cost) {
		ResourceType type = cost.resourceType();
		return get(type).canPay(cost.amount());
	}

	public void pay(Cost cost, Spell spell) {
		if (!canPay(cost)) {
			throw new IllegalArgumentException("Can't pay spell cost: " + cost);
		}

		ResourceType type = cost.resourceType();
		get(type).pay(cost.amount(), spell);
	}

	private void add(UnitResource resource) {
		this.resourceMap.put(resource.getType(), resource);
	}

	private UnitResource get(ResourceType type) {
		return resourceMap.get(type);
	}

	@Override
	public SimulationContext getSimulationContext() {
		return owner.getSimulationContext();
	}
}
