package wow.simulator.model.context;

import lombok.Setter;
import wow.character.model.snapshot.RngStrategy;
import wow.commons.model.Duration;
import wow.commons.model.effect.AbilitySource;
import wow.commons.model.effect.EffectAugmentations;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.component.DirectComponent;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.impl.NonPeriodicEffectInstance;
import wow.simulator.model.effect.impl.PeriodicEffectInstance;
import wow.simulator.model.unit.TargetResolver;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static wow.commons.model.spell.SpellTarget.GROUND;

/**
 * User: POlszewski
 * Date: 2023-11-02
 */
public class SpellResolutionContext extends Context {
	private final TargetResolver targetResolver;
	private final CastSpellAction action;
	private final Map<Unit, Boolean> hitRollByUnit = new HashMap<>();
	@Setter
	private Double valueParam;

	public SpellResolutionContext(Unit caster, Spell spell, TargetResolver targetResolver, Context parentContext, CastSpellAction action) {
		super(caster, spell, parentContext);
		this.targetResolver = targetResolver;
		this.action = action;
	}

	public EffectInstance resolveSpell() {
		for (var directComponent : spell.getDirectComponents()) {
			var delay = getDelay(directComponent);

			getSimulation().delayedAction(
					delay,
					() -> directComponentAction(directComponent)
			);
		}

		return applyEffect();
	}

	private Duration getDelay(DirectComponent directComponent) {
		var flightTime = Duration.ZERO;
		return directComponent.bolt() ? flightTime : Duration.ZERO;
	}

	private boolean hitRollOnlyOnce(Unit target) {
		if (action == null || Unit.areFriendly(caster, target)) {
			return true;
		}

		return hitRollByUnit.computeIfAbsent(target, this::hitRoll);
	}

	private boolean critRoll(double critChancePct) {
		return caster.getRng().critRoll(critChancePct, spell);
	}

	public void directComponentAction(DirectComponent directComponent) {
		var lastValueSnapshot = getLastValueSnapshot();

		targetResolver.forEachTarget(
				directComponent,
				componentTarget -> directComponentAction(directComponent, componentTarget, lastValueSnapshot)
		);
	}

	private void directComponentAction(DirectComponent directComponent, Unit target, LastValueSnapshot last) {
		switch (directComponent.type()) {
			case DAMAGE ->
					dealDirectDamage(directComponent, target);
			case HEAL ->
					directHeal(directComponent, target);
			case MANA_GAIN ->
					directManaGain(directComponent, target);
			case COPY_DAMAGE_AS_HEAL_PCT ->
					copyAsHeal(directComponent, target, last.damageDone);
			case FROM_PARENT_COPY_DAMAGE_AS_HEAL_PCT ->
					copyAsHeal(directComponent, target, last.parentDamageDone);
			case COPY_DAMAGE_AS_MANA_GAIN_PCT ->
					copyAsManaGain(directComponent, target, last.damageDone);
			case FROM_PARENT_COPY_DAMAGE_AS_MANA_GAIN_PCT ->
					copyAsManaGain(directComponent, target, last.parentDamageDone);
			case COPY_HEALTH_PAID_AS_MANA_GAIN_PCT ->
					copyAsManaGain(directComponent, target, last.parentHealthPaid);
			default ->
					throw new UnsupportedOperationException();
		}
	}

	private void dealDirectDamage(DirectComponent directComponent, Unit target) {
		if (!hitRollOnlyOnce(target)) {
			return;
		}

		var snapshot = caster.getDirectSpellDamageSnapshot(spell, target, directComponent);
		var critRoll = critRoll(snapshot.getCritPct());
		var addBonus = shouldAddBonus(directComponent, target);
		var directDamage = snapshot.getDirectAmount(RngStrategy.AVERAGED, addBonus, critRoll);

		decreaseHealth(target, directDamage, true, critRoll);
	}

	private void directHeal(DirectComponent directComponent, Unit target) {
		var snapshot = caster.getDirectHealingSnapshot(spell, target, directComponent);
		var critRoll = critRoll(snapshot.getCritPct());
		var addBonus = shouldAddBonus(directComponent, target);
		var directHealing = snapshot.getDirectAmount(RngStrategy.AVERAGED, addBonus, critRoll);

		increaseHealth(target, directHealing, true, critRoll);
	}

	private void directManaGain(DirectComponent directComponent, Unit target) {
		var mana = (directComponent.min() + directComponent.max()) / 2;

		increaseMana(target, mana);
	}

	private void copyAsHeal(DirectComponent directComponent, Unit target, int value) {
		var ratioPct = getRatioPct(directComponent);
		var heal = getCharacterCalculationService().getCopiedAmountAsHeal(caster, getSourceSpell(), target, value, ratioPct);
		var roundedHeal = roundValue(heal, target);

		increaseHealth(target, roundedHeal, true, false);
	}

	private void copyAsManaGain(DirectComponent directComponent, Unit target, int value) {
		var ratioPct = getRatioPct(directComponent);
		var manaGain = getCharacterCalculationService().getCopiedAmountAsManaGain(caster, getSourceSpell(), target, value, ratioPct);
		var roundedManaGain = roundValue(manaGain, target);

		increaseMana(target, roundedManaGain);
	}

	private double getRatioPct(DirectComponent directComponent) {
		return valueParam != null ? valueParam : directComponent.min();
	}

	public void applyEffect(EffectSource effectSource) {
		var effectApplication = spell.getEffectApplication();

		targetResolver.forEachTarget(
				effectApplication,
				effectTarget -> applyEffect(effectTarget, effectSource)
		);
	}

	private EffectInstance applyEffect() {
		var effectApplication = spell.getEffectApplication();

		if (effectApplication == null) {
			return null;
		}

		if (effectApplication.target() == GROUND) {
			return putPeriodicEffectOnTheGround();
		}

		var appliedEffects = new ArrayList<EffectInstance>();

		targetResolver.forEachTarget(
				effectApplication,
				effectTarget -> appliedEffects.add(applyEffect(effectTarget))
		);

		return appliedEffects.getFirst();
	}

	private EffectInstance applyEffect(Unit target) {
		if (!hitRollOnlyOnce(target)) {
			return null;
		}

		return applyEffect(target, new AbilitySource(action.getAbility()));
	}

	private EffectInstance applyEffect(Unit target, EffectSource effectSource) {
		var effectApplication = spell.getEffectApplication();
		var replacementMode = effectApplication.replacementMode();
		var appliedEffect = createEffect(target, effectSource);
		var augmentations = getEffectAugmentations(appliedEffect);

		appliedEffect.augment(augmentations);
		target.addEffect(appliedEffect, replacementMode);
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
					effectSource,
					getSourceSpell(),
					this
			);
		} else {
			return new NonPeriodicEffectInstance(
					caster,
					target,
					effectApplication.effect(),
					duration,
					effectApplication.numStacks(),
					effectApplication.numCharges(),
					effectSource,
					getSourceSpell(),
					this
			);
		}
	}

	private PeriodicEffectInstance putPeriodicEffectOnTheGround() {
		var effect = createGroundPeriodicEffect();
		var replacementMode = spell.getEffectApplication().replacementMode();

		getSimulation().addGroundEffect(effect, replacementMode);
		return effect;
	}

	private PeriodicEffectInstance createGroundPeriodicEffect() {
		var effectApplication = spell.getEffectApplication();
		var durationSnapshot = caster.getEffectDurationSnapshot(spell, null);
		var duration = durationSnapshot.getDuration();
		var tickInterval = durationSnapshot.getTickInterval();

		return new PeriodicEffectInstance(
				caster,
				null,
				effectApplication.effect(),
				duration,
				tickInterval,
				effectApplication.numStacks(),
				effectApplication.numCharges(),
				new AbilitySource(action.getAbility()),
				getSourceSpell(),
				this
		);
	}

	private EffectAugmentations getEffectAugmentations(EffectInstance effect) {
		var target = effect.getTarget();

		return getCharacterCalculationService().getEffectAugmentations(caster, spell, target);
	}

	private boolean shouldAddBonus(DirectComponent directComponent, Unit target) {
		var bonus = directComponent.bonus();

		if (bonus == null) {
			return false;
		}

		return bonus.requiredEffect() == null || target.hasEffect(bonus.requiredEffect(), caster);
	}

	private record LastValueSnapshot(
			int damageDone,
			int parentDamageDone,
			int parentHealthPaid
	) {}

	private LastValueSnapshot getLastValueSnapshot() {
		return new LastValueSnapshot(
			this.getLastDamageDone(),
			parentContext.getLastDamageDone(),
			parentContext.getLastHealthPaid()
		);
	}
}
