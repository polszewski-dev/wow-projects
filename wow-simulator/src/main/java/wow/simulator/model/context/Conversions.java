package wow.simulator.model.context;

import wow.commons.model.spell.Ability;
import wow.commons.model.spell.Conversion;
import wow.simulator.model.unit.Unit;

import java.util.ArrayList;
import java.util.List;

import static wow.commons.model.spell.Conversion.From.DAMAGE_DONE;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
public abstract class Conversions {
	protected final Unit caster;
	protected final Ability ability;
	protected final List<Conversion> list = new ArrayList<>();

	protected Conversions(Unit caster, Ability ability) {
		this.caster = caster;
		this.ability = ability;
	}

	protected void addAbilityConversion() {
		var conversion = ability.getConversion();

		if (conversion != null) {
			list.add(conversion);
		}
	}

	public void performDamageDoneConversion(int actualDamage) {
		performConversions(DAMAGE_DONE, actualDamage);
	}

	protected void performConversions(Conversion.From from, int amount) {
		if (amount == 0) {
			return;
		}

		var matchingConversions = getMatchingConversions(from);

		for (var matchingConversion : matchingConversions) {
			performConversion(matchingConversion, amount);
		}
	}

	private void performConversion(Conversion conversion, int amount) {
		int convertedAmount = (int)(conversion.ratioPct().getCoefficient() * amount);

		switch (conversion.to()) {
			case HEALTH -> caster.increaseHealth(convertedAmount, false, ability);
			case MANA -> caster.increaseMana(convertedAmount, false, ability);
			default -> throw new IllegalArgumentException();
		}
	}

	private List<Conversion> getMatchingConversions(Conversion.From from) {
		return list.stream()
				.filter(x -> x.from() == from)
				.toList();
	}
}
