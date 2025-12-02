package wow.simulator.model.context;

import lombok.Setter;
import wow.character.model.snapshot.RngStrategy;
import wow.commons.model.Duration;
import wow.commons.model.effect.AbilitySource;
import wow.commons.model.effect.EffectAugmentations;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.spell.Spell;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.impl.NonPeriodicEffectInstance;
import wow.simulator.model.effect.impl.PeriodicEffectInstance;
import wow.simulator.model.unit.TargetResolver;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static wow.commons.model.spell.SpellTargetType.GROUND;
import static wow.commons.model.spell.component.ComponentCommand.DirectCommand;

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
		executeDirectCommands();
		return applyEffect();
	}

	private void executeDirectCommands() {
		for (var command : spell.getDirectCommands()) {
			var delay = getDelay(command);

			getSimulation().delayedAction(
					delay,
					() -> directComponentAction(command)
			);
		}
	}

	private Duration getDelay(DirectCommand command) {
		var flightTime = Duration.ZERO;
		return command.bolt() ? flightTime : Duration.ZERO;
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

	public void directComponentAction(DirectCommand command) {
		var lastValueSnapshot = getLastValueSnapshot();

		targetResolver.forEachTarget(
				command,
				componentTarget -> directComponentAction(command, componentTarget, lastValueSnapshot)
		);
	}

	private void directComponentAction(DirectCommand command, Unit target, LastValueSnapshot last) {
		switch (command.type()) {
			case DAMAGE ->
					dealDirectDamage(command, target);
			case HEAL ->
					directHeal(command, target);
			case MANA_DRAIN ->
					directManaDrain(command, target);
			case MANA_GAIN ->
					directManaGain(command, target);
			case COPY_DAMAGE_AS_HEAL_PCT ->
					copyAsHeal(command, target, last.damageDone);
			case FROM_PARENT_COPY_DAMAGE_AS_HEAL_PCT ->
					copyAsHeal(command, target, last.parentDamageDone);
			case COPY_DAMAGE_AS_MANA_GAIN_PCT ->
					copyAsManaGain(command, target, last.damageDone);
			case FROM_PARENT_COPY_DAMAGE_AS_MANA_GAIN_PCT ->
					copyAsManaGain(command, target, last.parentDamageDone);
			case COPY_HEALTH_PAID_AS_MANA_GAIN_PCT ->
					copyAsManaGain(command, target, last.parentHealthPaid);
			case COPY_MANA_DRAINED_AS_DAMAGE_PCT ->
					copyAsDamage(command, target, last.manaDrained);
			default ->
					throw new UnsupportedOperationException();
		}
	}

	private void dealDirectDamage(DirectCommand command, Unit target) {
		if (!hitRollOnlyOnce(target)) {
			return;
		}

		var snapshot = caster.getDirectSpellDamageSnapshot(spell, target, command);
		var critRoll = critRoll(snapshot.getCritPct());
		var addBonus = shouldAddBonus(command, target);
		var directDamage = snapshot.getDirectAmount(RngStrategy.AVERAGED, addBonus, critRoll);

		decreaseHealth(target, directDamage, true, critRoll);
	}

	private void directHeal(DirectCommand command, Unit target) {
		var snapshot = caster.getDirectHealingSnapshot(spell, target, command);
		var critRoll = critRoll(snapshot.getCritPct());
		var addBonus = shouldAddBonus(command, target);
		var directHealing = snapshot.getDirectAmount(RngStrategy.AVERAGED, addBonus, critRoll);

		increaseHealth(target, directHealing, true, critRoll);
	}

	private void directManaDrain(DirectCommand command, Unit target) {
		var mana = (command.min() + command.max()) / 2;

		decreaseMana(target, mana);
	}

	private void directManaGain(DirectCommand command, Unit target) {
		var mana = (command.min() + command.max()) / 2;

		increaseMana(target, mana);
	}

	private void copyAsDamage(DirectCommand command, Unit target, int value) {
		var ratioPct = getRatioPct(command);
		copyAsDamage(target, value, ratioPct);
	}

	private void copyAsHeal(DirectCommand command, Unit target, int value) {
		var ratioPct = getRatioPct(command);
		copyAsHeal(target, value, ratioPct);
	}

	private void copyAsManaGain(DirectCommand command, Unit target, int value) {
		var ratioPct = getRatioPct(command);
		var manaGain = getCharacterCalculationService().getCopiedAmountAsManaGain(caster, getSourceSpell(), target, value, ratioPct);
		var roundedManaGain = roundValue(manaGain, target);

		increaseMana(target, roundedManaGain);
	}

	private double getRatioPct(DirectCommand command) {
		return valueParam != null ? valueParam : command.min();
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

		if (effectApplication.target().hasType(GROUND)) {
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

	private boolean shouldAddBonus(DirectCommand command, Unit target) {
		var bonus = command.bonus();

		if (bonus == null) {
			return false;
		}

		return bonus.requiredEffect() == null || target.hasEffect(bonus.requiredEffect(), caster);
	}

	private record LastValueSnapshot(
			int damageDone,
			int parentDamageDone,
			int parentHealthPaid,
			int manaDrained
	) {}

	private LastValueSnapshot getLastValueSnapshot() {
		return new LastValueSnapshot(
			this.getLastDamageDone(),
			parentContext.getLastDamageDone(),
			parentContext.getLastHealthPaid(),
			this.getLastManaDrained()
		);
	}
}
