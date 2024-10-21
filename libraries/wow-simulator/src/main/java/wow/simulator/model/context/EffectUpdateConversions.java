package wow.simulator.model.context;

import wow.simulator.model.effect.UnitEffect;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
public class EffectUpdateConversions extends Conversions {
	private final UnitEffect effect;

	public EffectUpdateConversions(Unit caster, UnitEffect effect) {
		super(caster, effect.getSourceAbility());
		this.effect = effect;
		addEffectConversion(effect);
	}

	private void addEffectConversion(UnitEffect effect) {
		var conversion = effect.getConversion();

		if (conversion != null) {
			list.add(conversion);
		}
	}
}
