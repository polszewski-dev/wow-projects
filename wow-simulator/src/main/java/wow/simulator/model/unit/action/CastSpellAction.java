package wow.simulator.model.unit.action;

import wow.character.model.snapshot.RngStrategy;
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
			getGameLog().canNotBeCasted(owner, spell, target, this);
			interrupt();
			return;
		}

		this.castTime = context.getCastTime();

		if (spell.isChanneled()) {
			channelSpell();
		} else if (context.isInstantCast()) {
			instaCastSpell();
		} else {
			castSpell();
		}

		if (!spell.getSpellInfo().isIgnoresGCD()) {
			owner.triggerGcd(context.getGcd(), this);
		}
	}

	private void instaCastSpell() {
		beginCast();
		endCast();
	}

	private void castSpell() {
		beginCast();

		fromNowAfter(castTime, () -> {
			this.context = owner.getSpellCastContext(spell, target);
			endCast();
		});
	}

	private void channelSpell() {
		beginChannel();

		int numTicks = spell.getNumTicks();
		Duration tickInterval = castTime.divideBy(numTicks);

		Time castEnd = fromNowOnEachTick(numTicks, tickInterval, tickNo -> {});

		on(castEnd, this::endChannel);
	}

	private void beginCast() {
		getGameLog().beginCast(owner, spell, target, this);
		onBeginCast();
	}

	private void endCast() {
		getGameLog().endCast(owner, spell, target, this);
		onEndCast();
		paySpellCost();
		resolveSpell();
	}

	private void beginChannel() {
		paySpellCost();
		getGameLog().beginCast(owner, spell, target, this);
		resolveSpell();
		onBeginCast();
	}

	private void endChannel() {
		getGameLog().endCast(owner, spell, target, this);
		onEndCast();
	}

	private void onBeginCast() {
		// void atm
	}

	private void onEndCast() {
		// void atm
	}

	private void paySpellCost() {
		owner.paySpellCost(context);
		context.getConversions().performPaidCostConversion();
	}

	private void resolveSpell() {
		Duration delay = getDelay();

		getSimulation().delayedAction(delay, this::spellAction);
	}

	private Duration getDelay() {
		Duration flightTime = Duration.ZERO;
		return spell.isBolt() ? flightTime : Duration.ZERO;
	}

	private void spellAction() {
		if (spell.isHostile()) {
			harmfulSpellAction();
		} else {
			friendlySpellAction();
		}
	}

	private void harmfulSpellAction() {
		if (!hitRoll()) {
			getGameLog().spellMissed(owner, spell, target);
			return;
		}

		if (spell.hasDirectComponent()) {
			directDamageAction();
		}

		if (spell.hasDotComponent()) {
			// void atm
		}
	}

	private void directDamageAction() {
		boolean critRoll = critRoll();
		RngStrategy rngStrategy = getRngStrategy(critRoll);

		int directDamage = (int) context.snapshot().getDirectDamage(rngStrategy, true);
		int actualDamage = target.decreaseHealth(directDamage, critRoll, spell);
		context.getConversions().performDamageDoneConversion(actualDamage);
	}

	private boolean hitRoll() {
		double hitChance = context.snapshot().getHitChance();
		return owner.getRng().hitRoll(hitChance, spell.getSpellId());
	}

	private boolean critRoll() {
		double critChance = context.snapshot().getCritChance();
		return owner.getRng().critRoll(critChance, spell.getSpellId());
	}

	private void friendlySpellAction() {
		// void atm
	}

	private static RngStrategy getRngStrategy(boolean critRoll) {
		return new RngStrategy() {
			@Override
			public double getHitChance(double hitChance) {
				return 1;
			}

			@Override
			public double getCritChance(double critChance) {
				return critRoll ? 1 : 0;
			}
		};
	}
}
