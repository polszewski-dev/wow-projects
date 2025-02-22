package wow.simulator.model.unit.impl;

import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.effect.EffectCollector;
import wow.simulator.model.unit.NonPlayer;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public class NonPlayerImpl extends UnitImpl implements NonPlayer {
	public NonPlayerImpl(String name, NonPlayerCharacter character) {
		super(name, character);
		this.resources.setHealth(1_000_000_000, 1_000_000_000);
	}

	@Override
	public void collectAuras(EffectCollector collector) {
		// void
	}
}
