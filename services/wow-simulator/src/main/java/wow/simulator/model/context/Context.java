package wow.simulator.model.context;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.snapshot.SpellCostSnapshot;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.TriggeredSpell;
import wow.simulator.model.unit.Pet;
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

	protected void decreaseHealth(Unit target, int amount, boolean direct, boolean crit) {
		this.lastDamageDone = target.decreaseHealth(amount, direct, crit, caster, getSourceSpell(), this);

		EventContext.fireSpellDamageEvent(caster, target, spell, direct, crit, this);

		if (lastDamageDone > 0 && target.isDead()) {
			EventContext.fireTargetDied(caster, target, spell, this);

			if (target.isPet()) {
				EventContext.firePetDied(caster, (Pet) target, spell, this);
			}
		}
	}

	protected void increaseHealth(Unit target, int amount, boolean direct, boolean crit) {
		this.lastHealingDone = target.increaseHealth(amount, direct, crit, caster, getSourceSpell(), this);

		EventContext.fireSpellHealEvent(caster, target, spell, direct, crit, this);
	}

	protected void increaseMana(Unit target, int amount, boolean direct, boolean crit) {
		this.lastManaRestored = target.increaseMana(amount, direct, crit, caster, getSourceSpell(), this);

		EventContext.fireManaGainedEvent(caster, target, spell, this);
	}

	protected void decreaseMana(Unit target, int amount, boolean direct, boolean crit) {
		this.lastManaLost = target.decreaseMana(amount, direct, crit, caster, getSourceSpell(), this);

		EventContext.fireManaLostEvent(caster, target, spell, this);
	}

	protected void copy(Copy copy, Unit target, LastValueSnapshot last, boolean direct) {
		var from = getFrom(copy, last);
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

	private int getFrom(Copy copy, LastValueSnapshot last) {
		return switch (copy.from()) {
			case DAMAGE -> last.damageDone;
			case MANA_LOSS -> last.manaLost;
			case HEALTH_PAID -> last.parentHealthPaid;
			case PARENT_DAMAGE -> last.parentDamageDone;
			case PARENT_MANA_GAIN -> last.parentManaGained;
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
			int manaLost,
			int parentManaGained
	) {}

	protected LastValueSnapshot getLastValueSnapshot() {
		return new LastValueSnapshot(
				this.getLastDamageDone(),
				parentContext.getLastDamageDone(),
				parentContext.getLastHealthPaid(),
				this.getLastManaLost(),
				parentContext.getLastManaRestored()
		);
	}

	@Override
	public SimulationContext getSimulationContext() {
		return caster.getSimulationContext();
	}
}
