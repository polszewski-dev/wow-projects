package wow.simulator.model.context;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.snapshot.SpellCostSnapshot;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.TriggeredSpell;
import wow.simulator.model.unit.Unit;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;
import wow.simulator.util.RoundingReminder;

import java.util.HashMap;
import java.util.Map;

import static wow.commons.model.spell.component.ComponentCommand.Copy;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
@Getter
public abstract class Context implements SimulationContextSource {
	protected final Unit caster;
	protected final Spell spell;

	protected final Context parentContext;

	private int lastManaPaid;
	private int lastHealthPaid;
	@Setter
	private int lastDamageDone;
	@Setter
	private int lastHealingDone;
	@Setter
	private int lastManaRestored;
	@Setter
	private int lastManaLost;

	@Setter
	private Spell sourceSpellOverride;

	private Map<SpellAndTarget, RoundingReminder> roundingRemindersBySpellTarget;

	private record SpellAndTarget(Spell spell, Unit target) {}

	protected Context(Unit caster, Spell spell, Context parentContext) {
		this.caster = caster;
		this.spell = spell;
		this.parentContext = parentContext;
	}

	public boolean hitRoll(Unit target) {
		var hitChancePct = caster.getSpellHitPct(spell, target);
		var hitRoll = caster.getRng().hitRoll(hitChancePct, spell);

		if (hitRoll) {
			caster.getEventBus().spellHit(spell, target, this);
		} else {
			caster.getEventBus().spellResisted(spell, target, this);
		}

		return hitRoll;
	}

	protected void decreaseHealth(Unit target, int amount, boolean direct, boolean crit) {
		target.decreaseHealth(amount, direct, crit, caster, spell, this);
	}

	protected void increaseHealth(Unit target, int amount, boolean direct, boolean crit) {
		target.increaseHealth(amount, direct, crit, caster, spell, this);
	}

	protected void increaseMana(Unit target, int amount, boolean direct, boolean crit) {
		target.increaseMana(amount, direct, crit, caster, spell, this);
	}

	protected void decreaseMana(Unit target, int amount, boolean direct, boolean crit) {
		target.decreaseMana(amount, direct, crit, caster, spell, this);
	}

	protected void copy(Copy copy, Unit target, boolean direct) {
		var from = getFrom(copy);
		var ratioPct = getRatioPct(copy);

		switch (copy.to()) {
			case DAMAGE ->
					copyAsDamage(target, from, ratioPct, direct);

			case HEAL ->
					copyAsHeal(target, from, ratioPct, direct);

			case MANA_LOSS ->
					copyAsManaLoss(target, from, ratioPct, direct);

			case MANA_GAIN ->
					copyAsManaGain(target, from, ratioPct, direct);
		}
	}

	private int getFrom(Copy copy) {
		return switch (copy.from()) {
			case DAMAGE -> lastDamageDone;
			case MANA_LOSS -> lastManaLost;
			case HEALTH_PAID -> parentContext.lastHealthPaid;
			case PARENT_DAMAGE -> parentContext.lastDamageDone;
			case PARENT_MANA_GAIN -> parentContext.lastManaRestored;
			default -> throw new IllegalArgumentException(copy.from().name());
		};
	}

	protected double getRatioPct(Copy command) {
		return command.ratio().value();
	}

	protected void copyAsDamage(Unit target, int value, double ratioPct, boolean direct) {
		var damage = getCharacterCalculationService().getCopiedAmountAsDamage(caster, getSourceSpell(), target, value, ratioPct);
		var roundedDamage = roundValue(damage, target);

		decreaseHealth(target, roundedDamage, direct, false);
	}

	protected void copyAsHeal(Unit target, int value, double ratioPct, boolean direct) {
		var heal = getCharacterCalculationService().getCopiedAmountAsHeal(caster, getSourceSpell(), target, value, ratioPct);
		var roundedHeal = roundValue(heal, target);

		increaseHealth(target, roundedHeal, direct, false);
	}

	protected void copyAsManaGain(Unit target, int value, double ratioPct, boolean direct) {
		var manaGain = getCharacterCalculationService().getCopiedAmountAsManaGain(caster, getSourceSpell(), target, value, ratioPct);
		var roundedManaGain = roundValue(manaGain, target);

		increaseMana(target, roundedManaGain, direct, false);
	}

	protected void copyAsManaLoss(Unit target, int value, double ratioPct, boolean direct) {
		var manaLoss = value * ratioPct / 100;
		var roundedManaGain = roundValue(manaLoss, target);

		decreaseMana(target, roundedManaGain, direct, false);
	}

	protected void setPaidCost(SpellCostSnapshot costSnapshot) {
		var cost = costSnapshot.getCostToPayUnreduced();

		switch (cost.resourceType()) {
			case MANA -> this.lastManaPaid = cost.amount();
			case HEALTH -> this.lastHealthPaid = cost.amount();
			default -> {
				// ignored
			}
		}
	}

	public Spell getSourceSpell() {
		if (sourceSpellOverride != null) {
			return sourceSpellOverride;
		}
		if (spell instanceof TriggeredSpell) {
			return parentContext.spell;
		}
		return spell;
	}

	private Context getRootContext() {
		if (parentContext != null) {
			return parentContext.getRootContext();
		}
		return this;
	}

	private RoundingReminder getRoundingReminder(Spell spell, Unit target) {
		if (roundingRemindersBySpellTarget == null) {
			roundingRemindersBySpellTarget = new HashMap<>();
		}

		return roundingRemindersBySpellTarget.computeIfAbsent(
				new SpellAndTarget(spell, target),
				x -> new RoundingReminder()
		);
	}

	protected int roundValue(double value, Unit target) {
		return getRootContext().getRoundingReminder(spell, target).roundValue(value);
	}

	@Override
	public SimulationContext getSimulationContext() {
		return caster.getSimulationContext();
	}
}
