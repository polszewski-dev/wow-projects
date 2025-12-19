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
	private int lastDamageDone;
	private int lastHealingDone;
	private int lastManaRestored;
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
			getGameLog().spellHit(caster, target, spell);
			EventContext.fireSpellHitEvent(caster, target, spell, this);
		} else {
			getGameLog().spellResisted(caster, target, spell);
			EventContext.fireSpellResistedEvent(caster, target, spell, this);
		}

		return hitRoll;
	}

	protected void decreaseHealth(Unit target, int amount, boolean directDamage, boolean critRoll) {
		this.lastDamageDone = target.decreaseHealth(amount, critRoll, getSourceSpell(), caster);

		EventContext.fireSpellDamageEvent(caster, target, spell, directDamage, critRoll, this);
	}

	protected void increaseHealth(Unit target, int amount, boolean directHeal, boolean critRoll) {
		this.lastHealingDone = target.increaseHealth(amount, critRoll, getSourceSpell(), caster);

		EventContext.fireSpellHealEvent(caster, target, spell, directHeal, critRoll, this);
	}

	protected void increaseMana(Unit target, int amount) {
		this.lastManaRestored = target.increaseMana(amount, false, getSourceSpell(), caster);
	}

	protected void decreaseMana(Unit target, int amount) {
		this.lastManaLost = target.decreaseMana(amount, false, getSourceSpell(), caster);
	}

	protected void copy(Copy copy, Unit target, LastValueSnapshot last) {
		var from = getFrom(copy, last);
		var ratioPct = getRatioPct(copy);

		switch (copy.to()) {
			case DAMAGE ->
					copyAsDamage(target, from, ratioPct);

			case HEAL ->
					copyAsHeal(target, from, ratioPct);

			case MANA_LOSS ->
					copyAsManaLoss(target, from, ratioPct);

			case MANA_GAIN ->
					copyAsManaGain(target, from, ratioPct);
		}
	}

	private int getFrom(Copy copy, LastValueSnapshot last) {
		return switch (copy.from()) {
			case DAMAGE -> last.damageDone;
			case MANA_LOSS -> last.manaLost;
			case HEALTH_PAID -> last.parentHealthPaid;
			case PARENT_DAMAGE -> last.parentDamageDone;
			default -> throw new IllegalArgumentException(copy.from().name());
		};
	}

	protected double getRatioPct(Copy command) {
		return command.ratio().value();
	}

	protected void copyAsDamage(Unit target, int value, double ratioPct) {
		var damage = getCharacterCalculationService().getCopiedAmountAsDamage(caster, getSourceSpell(), target, value, ratioPct);
		var roundedDamage = roundValue(damage, target);

		decreaseHealth(target, roundedDamage, true, false);
	}

	protected void copyAsHeal(Unit target, int value, double ratioPct) {
		var heal = getCharacterCalculationService().getCopiedAmountAsHeal(caster, getSourceSpell(), target, value, ratioPct);
		var roundedHeal = roundValue(heal, target);

		increaseHealth(target, roundedHeal, true, false);
	}

	protected void copyAsManaGain(Unit target, int value, double ratioPct) {
		var manaGain = getCharacterCalculationService().getCopiedAmountAsManaGain(caster, getSourceSpell(), target, value, ratioPct);
		var roundedManaGain = roundValue(manaGain, target);

		increaseMana(target, roundedManaGain);
	}

	protected void copyAsManaLoss(Unit target, int value, double ratioPct) {
		var manaLoss = value * ratioPct / 100;
		var roundedManaGain = roundValue(manaLoss, target);

		decreaseMana(target, roundedManaGain);
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

	protected Spell getSourceSpell() {
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

	protected record LastValueSnapshot(
			int damageDone,
			int parentDamageDone,
			int parentHealthPaid,
			int manaLost
	) {}

	protected LastValueSnapshot getLastValueSnapshot() {
		return new LastValueSnapshot(
				this.getLastDamageDone(),
				parentContext.getLastDamageDone(),
				parentContext.getLastHealthPaid(),
				this.getLastManaLost()
		);
	}

	@Override
	public SimulationContext getSimulationContext() {
		return caster.getSimulationContext();
	}
}
