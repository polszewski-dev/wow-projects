package wow.simulator.model.context;

import wow.character.model.snapshot.RngStrategy;
import wow.commons.model.effect.AbilitySource;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.component.DirectComponent;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.impl.NonPeriodicEffectInstance;
import wow.simulator.model.effect.impl.PeriodicEffectInstance;
import wow.simulator.model.unit.TargetResolver;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-11-02
 */
public class SpellResolutionContext extends Context {
	private final TargetResolver targetResolver;

	private final Map<Unit, Boolean> hitRollByUnit = new HashMap<>();
	private final Set<Unit> spellHitFiredByUnit = new HashSet<>();

	public SpellResolutionContext(Unit caster, Spell spell, TargetResolver targetResolver) {
		super(caster, spell);
		this.targetResolver = targetResolver;
	}

	public boolean hitRoll(CastSpellAction action, Unit target) {
		if (action == null || Unit.areFriendly(caster, target)) {
			return true;
		}

		var fireEvents = !hitRollByUnit.containsKey(target);

		var hitRoll = hitRollByUnit.computeIfAbsent(target, key -> {
			double hitChancePct = caster.getSpellHitPct(spell, target);
			return caster.getRng().hitRoll(hitChancePct, spell);
		});

		if (fireEvents) {
			if (hitRoll) {
				caster.getGameLog().spellHit(action, target);
			} else {
				caster.getGameLog().spellResisted(action, target);
				EventContext.fireSpellResistedEvent(caster, target, spell);
			}
		}

		return hitRoll;
	}

	private boolean critRoll(double critChancePct) {
		return caster.getRng().critRoll(critChancePct, spell);
	}

	@Override
	protected SpellResolutionConversions getConversions() {
		return new SpellResolutionConversions(caster, spell);
	}

	public void directComponentAction(DirectComponent directComponent, CastSpellAction action) {
		switch (directComponent.type()) {
			case DAMAGE ->
					dealDirectDamage(directComponent, action);
			case MANA_GAIN ->
					increaseMana(directComponent);
			case COPY_DAMAGE_AS_HEAL_PCT -> {
					// void
			}
			default ->
					throw new UnsupportedOperationException();
		}
	}

	private void increaseMana(DirectComponent directComponent) {
		var componentTarget = targetResolver.getTarget(directComponent.target());

		increaseMana(componentTarget, (directComponent.min() + directComponent.max()) / 2);
	}

	private void dealDirectDamage(DirectComponent directComponent, CastSpellAction action) {
		var target = targetResolver.getTarget(directComponent.target());

		if (!hitRoll(action, target)) {
			return;
		}

		dealDirectDamage(directComponent, target);
	}

	private void dealDirectDamage(DirectComponent directComponent, Unit target) {
		var snapshot = caster.getDirectSpellDamageSnapshot(spell, target, directComponent);
		var critRoll = critRoll(snapshot.getCritPct());
		var addBonus = shouldAddBonus(directComponent, target);
		var directDamage = snapshot.getDirectDamage(RngStrategy.AVERAGED, addBonus, critRoll);

		decreaseHealth(target, directDamage, true, critRoll);
		fireSpellHitEvent(target);
	}

	public EffectInstance applyEffect(CastSpellAction action, Unit target) {
		if (!hitRoll(action, target)) {
			return null;
		}

		return applyEffect(new AbilitySource(action.getAbility()));
	}

	public EffectInstance applyEffect(EffectSource effectSource) {
		var effectApplication = spell.getEffectApplication();
		var target = targetResolver.getTarget(effectApplication.target());
		var appliedEffect = createEffect(target, effectSource);

		target.addEffect(appliedEffect);
		fireSpellHitEvent(target);
		return appliedEffect;
	}

	private EffectInstance createEffect(Unit target, EffectSource effectSource) {
		var effectApplication = spell.getEffectApplication();
		var durationSnapshot = caster.getEffectDurationSnapshot(spell, target);
		var duration = durationSnapshot.getDuration();
		var tickInterval = durationSnapshot.getTickInterval();

		if (tickInterval.isPositive()) {
			return new PeriodicEffectInstance(
					caster,
					target,
					effectApplication.effect(),
					duration,
					tickInterval,
					effectApplication.numStacks(),
					effectApplication.numCharges(),
					effectSource
			);
		} else {
			return new NonPeriodicEffectInstance(
					caster,
					target,
					effectApplication.effect(),
					duration,
					effectApplication.numStacks(),
					effectApplication.numCharges(),
					effectSource
			);
		}
	}

	private boolean shouldAddBonus(DirectComponent directComponent, Unit target) {
		var bonus = directComponent.bonus();

		if (bonus == null) {
			return false;
		}

		return bonus.requiredEffect() == null || target.hasEffect(bonus.requiredEffect(), caster);
	}

	private void fireSpellHitEvent(Unit target) {
		if (spellHitFiredByUnit.contains(target) || Unit.areFriendly(caster, target)) {
			return;
		}

		EventContext.fireSpellHitEvent(caster, target, spell);
		spellHitFiredByUnit.add(target);
	}
}
