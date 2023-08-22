package wow.simulator.model.unit.action;

import wow.character.model.snapshot.RngStrategy;
import wow.commons.model.Duration;
import wow.commons.model.spells.Spell;
import wow.simulator.model.effect.DoT;
import wow.simulator.model.effect.PeriodicEffect;
import wow.simulator.model.rng.RngStrategies;
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
	private Boolean hitRoll;

	private PeriodicEffect periodicEffect;

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
			finish();
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

	private void beginCast() {
		onBeginCast();
	}

	private void endCast() {
		onEndCast();
		paySpellCost();
		resolveSpell();
	}

	private void channelSpell() {
		paySpellCost();

		if (!hitRoll()) {
			onBeginCast();
			getGameLog().spellResisted(owner, spell, target, this);
			onEndCast();
			return;
		}

		onBeginCast();
		spellAction();

		fromNowOnEachTick(periodicEffect.getNumTicks(), periodicEffect.getTickInterval(), tickNo -> {});
	}

	private void onBeginCast() {
		getGameLog().beginCast(owner, spell, target, this);
		// events here
	}

	private void onEndCast() {
		getGameLog().endCast(owner, spell, target, this);
		// events here
	}

	private void paySpellCost() {
		context.paySpellCost();
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
			getGameLog().spellResisted(owner, spell, target, this);
			return;
		}

		if (spell.hasDirectComponent()) {
			directDamageAction();
		}

		if (spell.hasDotComponent()) {
			dotAction();
		}
	}

	private void directDamageAction() {
		boolean critRoll = critRoll();
		RngStrategy rngStrategy = RngStrategies.directDamageStrategy(critRoll);

		int directDamage = (int) context.snapshot().getDirectDamage(rngStrategy, true);

		context.decreaseHealth(directDamage, critRoll);
	}

	private void dotAction() {
		periodicEffect = spell.isChanneled() ? new ChannelDoT(context) : new DoT(context);
		target.addEffect(periodicEffect);
	}

	private class ChannelDoT extends DoT {
		public ChannelDoT(SpellCastContext context) {
			super(context);
		}

		@Override
		protected void onFinished() {
			endChannel();
			super.onFinished();
		}

		private void endChannel() {
			onEndCast();
		}
	}

	private boolean hitRoll() {
		if (hitRoll == null) {
			double hitChance = context.snapshot().getHitChance();
			this.hitRoll = owner.getRng().hitRoll(hitChance, spell.getSpellId());
		}
		return hitRoll;
	}

	private boolean critRoll() {
		double critChance = context.snapshot().getCritChance();
		return owner.getRng().critRoll(critChance, spell.getSpellId());
	}

	private void friendlySpellAction() {
		// void atm
	}

	@Override
	public void onRemovedFromQueue() {
		getGameLog().castInterrupted(owner, spell, target, this);
		super.onRemovedFromQueue();
		if (spell.isChanneled()) {
			target.removeEffect(periodicEffect);
		}
	}
}
