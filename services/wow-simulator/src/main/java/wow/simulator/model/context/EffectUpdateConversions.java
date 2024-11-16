package wow.simulator.model.context;

import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
public class EffectUpdateConversions extends Conversions {
	public EffectUpdateConversions(Unit caster, EffectInstance effect) {
		super(caster, effect.getSourceSpell());
		addEffectConversion(effect);
	}

	private void addEffectConversion(EffectInstance effect) {
		var conversion = effect.getConversion();

		if (conversion != null) {
			list.add(conversion);
		}
	}
}
