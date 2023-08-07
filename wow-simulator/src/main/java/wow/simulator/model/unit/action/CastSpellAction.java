package wow.simulator.model.unit.action;

import wow.character.model.snapshot.CritMode;
import wow.commons.model.Duration;
import wow.commons.model.spells.Spell;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.SpellCastContext;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-08-10
 */
public class CastSpellAction extends UnitAction {
	private final Spell spell;
	private final Unit target;

	private Duration castTime;
	private Duration gcd;

	private SpellCastContext context;

	public CastSpellAction(Unit owner, Spell spell, Unit target) {
		super(owner);
		this.spell = spell;
		this.target = target;
	}

	@Override
	protected void setUp() {
		this.context = owner.getSpellCastContext(spell, target);

		if (!owner.canCast(context)) {
			getGameLog().canNotBeCasted(owner, spell, target, actionId);
			interrupt();
			return;
		}

		this.castTime = context.getCastTime();
		this.gcd = context.getGcd();

		if (spell.isChanneled()) {
			channelSpell();
		} else if (context.isInstantCast()) {
			instaCastSpell();
		} else {
			castSpell();
		}
	}

	private void instaCastSpell() {
		beginCast();
		endCast();
		triggerGcd();
	}

	private void castSpell() {
		beginCast();

		fromNowAfter(castTime, () -> {
			this.context = owner.getSpellCastContext(spell, target);
			endCast();
			triggerGcd();
		});
	}

	private void channelSpell() {
		beginChannel();

		int numTicks = spell.getNumTicks();
		Duration tickInterval = castTime.divideBy(numTicks);

		Time castEnd = fromNowOnEachTick(numTicks, tickInterval, tickNo -> {});

		on(castEnd, () -> {
			endChannel();
			triggerGcd();
		});
	}

	private void beginCast() {
		getGameLog().beginCast(owner, spell, target, actionId);
		onBeginCast();
	}

	private void endCast() {
		getGameLog().endCast(owner, spell, target, actionId);
		onEndCast();
		owner.paySpellCosts(context);
		resolveSpell();
	}

	private void beginChannel() {
		owner.paySpellCosts(context);
		getGameLog().beginCast(owner, spell, target, actionId);
		resolveSpell();
		onBeginCast();
	}

	private void endChannel() {
		getGameLog().endCast(owner, spell, target, actionId);
		onEndCast();
	}

	private void triggerGcd() {
		if (!triggersGcd()) {
			return;
		}

		Duration remainingGcd = gcd.subtract(castTime);
		getGameLog().beginGcd(owner, spell, target, actionId);

		fromNowAfter(remainingGcd, () -> getGameLog().endGcd(owner, spell, target, actionId));
	}

	private boolean triggersGcd() {
		return castTime.compareTo(gcd) < 0 && !spell.getSpellInfo().isIgnoresGCD();
	}

	private void onBeginCast() {
		// void atm
	}

	private void onEndCast() {
		// void atm
	}

	private void resolveSpell() {
		Duration delay = getDelay();

		target.delayedAction(delay, this::spellAction);
	}

	private Duration getDelay() {
		boolean bolt = spell.getSpellInfo().getDamagingSpellInfo().bolt();
		Duration flightTime = Duration.ZERO;
		return bolt ? flightTime : Duration.ZERO;
	}

	private void spellAction() {
		if (spell.hasDamageComponent()) {
			harmfulSpellAction();
		} else {
			friendlySpellAction();
		}
	}

	private void friendlySpellAction() {
		// void atm
	}

	private void harmfulSpellAction() {
		if (spell.hasDirectComponent()) {
			int directDamage = (int) context.snapshot().getDirectDamage(CritMode.AVERAGE, true);

			target.decreaseHealth(directDamage, spell);
		}

		if (spell.hasDotComponent()) {
			// void atm
		}
	}
}
