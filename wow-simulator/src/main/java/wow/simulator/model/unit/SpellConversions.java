package wow.simulator.model.unit;

import wow.commons.model.spells.Conversion;
import wow.commons.model.spells.Spell;

import java.util.ArrayList;
import java.util.List;

import static wow.commons.model.spells.Conversion.From.*;

/**
 * User: POlszewski
 * Date: 2023-08-15
 */
public class SpellConversions {
	private final SpellCastContext context;
	private final Unit caster;
	private final Spell spell;
	private final List<Conversion> conversions = new ArrayList<>();

	public SpellConversions(SpellCastContext context) {
		this.context = context;
		this.caster = context.caster();
		this.spell = context.spell();

		Conversion conversion = spell.getSpellInfo().getConversion();
		if (conversion != null) {
			conversions.add(conversion);
		}
	}

	public void performPaidCostConversion() {
		var from = getFrom();
		var amount = context.cost().amount();
		performConversions(from, amount);
	}

	public void performDamageDoneConversion(int actualDamage) {
		performConversions(DAMAGE_DONE, actualDamage);
	}

	private void performConversions(Conversion.From from, int amount) {
		if (amount == 0) {
			return;
		}

		var matchingConversions = getMatchingConversions(from);

		for (Conversion matchingConversion : matchingConversions) {
			performConversion(matchingConversion, amount);
		}
	}

	private void performConversion(Conversion conversion, int amount) {
		int convertedAmount = (int)(conversion.ratioPct().getCoefficient() * amount);

		switch (conversion.to()) {
			case HEALTH -> caster.increaseHealth(convertedAmount, spell);
			case MANA -> caster.increaseMana(convertedAmount, spell);
			default -> throw new IllegalArgumentException();
		}
	}

	private List<Conversion> getMatchingConversions(Conversion.From from) {
		return conversions.stream()
				.filter(x -> x.from() == from)
				.toList();
	}

	private Conversion.From getFrom() {
		return switch (context.cost().resourceType()) {
			case HEALTH -> HEALTH_PAID;
			case MANA -> MANA_PAID;
			case PET_MANA -> throw new UnsupportedOperationException("no pets atm");
		};
	}
}
