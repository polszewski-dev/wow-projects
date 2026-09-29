package wow.simulator.model.context;

import lombok.Setter;
import wow.character.model.snapshot.RngStrategy;
import wow.character.util.SpellTargetConditionArgs;
import wow.character.util.SpellTargetConditionChecker;
import wow.commons.model.character.PetType;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectAugmentations;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.Spell;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.impl.NonPeriodicEffectInstance;
import wow.simulator.model.effect.impl.PeriodicEffectInstance;
import wow.simulator.model.unit.PrimaryTarget;
import wow.simulator.model.unit.TargetResolver;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.impl.UnitImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static wow.commons.model.effect.EffectSource.AbilitySource;
import static wow.commons.model.spell.SpellTargetType.GROUND;
import static wow.commons.model.spell.component.ComponentCommand.*;

/**
 * User: POlszewski
 * Date: 2023-11-02
 */
public class SpellResolutionContext extends Context {
	private TargetResolver targetResolver;
	private final Map<Unit, Boolean> hitRollByUnit = new HashMap<>();
	@Setter
	private Double valueParam;

	private PetType sacrificedPetType;

	private EffectSource effectSource;

	public SpellResolutionContext(Unit caster, Spell spell, Context parentContext) {
		super(caster, spell, parentContext);
	}

	public void resolveCastSpell(PrimaryTarget primaryTarget) {
		var targetResolver = primaryTarget.getTargetResolver(caster);
		var effectSource = new AbilitySource((Ability) spell);

		resolveSpell(targetResolver, effectSource);
	}

	public void resolveTriggeredSpell(Unit target, Effect sourceEffect) {
		var targetResolver = TargetResolver.ofTarget(caster, target);
		var effectSource = sourceEffect.getSource();

		resolveSpell(targetResolver, effectSource);
	}

	private void resolveSpell(TargetResolver targetResolver, EffectSource effectSource) {
		this.targetResolver = targetResolver;
		this.effectSource = effectSource;

		executeDirectCommands();
		applyEffects();
	}

	private void executeDirectCommands() {
		for (var command : spell.getDirectCommands()) {
			directComponentAction(command);
		}
	}

	private boolean hitRollOnlyOnce(Unit target) {
		if (isTriggeredSpell() || Unit.areFriendly(caster, target)) {
			return true;
		}

		return hitRollByUnit.computeIfAbsent(target, this::hitRoll);
	}

	private boolean isTriggeredSpell() {
		return !(spell instanceof Ability);
	}

	private boolean critRoll(double critChancePct) {
		return caster.getRng().critRoll(critChancePct, spell);
	}

	private void directComponentAction(DirectCommand command) {
		targetResolver.forEachTarget(
				command,
				componentTarget -> directComponentAction(command, componentTarget)
		);
	}

	private void directComponentAction(DirectCommand directCommand, Unit target) {
		if (!checkSecondaryCondition(directCommand, target)) {
			return;
		}

		switch (directCommand) {
			case DealDamageDirectly command ->
					dealDirectDamage(command, target);

			case HealDirectly command ->
					directHeal(command, target);

			case LoseManaDirectly command ->
					directManaLoss(command, target);

			case GainManaDirectly command ->
					directManaGain(command, target);

			case Copy command ->
					copy(command, target, true);

			case SummonPet command ->
					summonPet(command, target);

			case UnsummonPet ignored ->
					unsummonPet(target);

			case SacrificePet ignored ->
					sacrificePet(target);

			case ResetTreeCooldowns command ->
					resetTreeCooldowns(command, target);

			case RemoveEffect command ->
					removeEffect(command, target);

			default ->
					throw new UnsupportedOperationException();
		}
	}

	private boolean checkSecondaryCondition(HasSecondaryTargetCondition command, Unit target) {
		var condition = command.condition();

		if (condition.isEmpty()) {
			return true;
		}

		var args = new SpellTargetConditionArgs(caster, target);

		args.setSacrificedPetType(sacrificedPetType);

		return SpellTargetConditionChecker.check(condition, args);
	}

	private void dealDirectDamage(DealDamageDirectly command, Unit target) {
		if (!hitRollOnlyOnce(target)) {
			return;
		}

		var snapshot = caster.getDirectSpellDamageSnapshot(spell, target, command);
		var critRoll = critRoll(snapshot.getCritPct());
		var addBonus = shouldAddBonus(command, target);
		var directDamage = snapshot.getDirectAmount(RngStrategy.AVERAGED, addBonus, critRoll);

		decreaseHealth(target, directDamage, true, critRoll);
	}

	private void directHeal(HealDirectly command, Unit target) {
		var snapshot = caster.getDirectHealingSnapshot(spell, target, command);
		var critRoll = critRoll(snapshot.getCritPct());
		var addBonus = shouldAddBonus(command, target);
		var directHealing = snapshot.getDirectAmount(RngStrategy.AVERAGED, addBonus, critRoll);

		increaseHealth(target, directHealing, true, critRoll);
	}

	private void directManaLoss(LoseManaDirectly command, Unit target) {
		var mana = (command.min() + command.max()) / 2;

		decreaseMana(target, mana, true, false);
	}

	private void directManaGain(GainManaDirectly command, Unit target) {
		var mana = (command.min() + command.max()) / 2;

		increaseMana(target, mana, true, false);
	}

	private void summonPet(SummonPet command, Unit target) {
		target.summonPet(command.petType(), command.duration(), spell, this);
	}

	private void unsummonPet(Unit target) {
		var master = target.getMaster();

		master.unsummonPet(spell, this);
	}

	private void sacrificePet(Unit target) {
		this.sacrificedPetType = target.getActivePetType();

		target.sacrificePet(spell, this);
	}

	private void resetTreeCooldowns(ResetTreeCooldowns command, Unit target) {
		var exceptAbilityId = spell instanceof Ability ability ? ability.getAbilityId() : null;

		target.resetCooldowns(command.tree(), exceptAbilityId);
	}

	private void removeEffect(RemoveEffect command, Unit target) {
		target.removeEffect(command.effectName(), caster);
	}

	@Override
	protected double getRatioPct(Copy command) {
		return valueParam != null ? valueParam : command.ratio().value();
	}

	private void applyEffects() {
		for (var command : spell.getApplyEffectCommands()) {
			var appliedEffects = applyEffect(command);

			if (spell instanceof Ability ability && ability.isChanneled()) {
				((UnitImpl) caster).channelAction(ability, appliedEffects);
			}
		}
	}

	private List<EffectInstance> applyEffect(ApplyEffect command) {
		if (command.target().hasType(GROUND)) {
			var groundEffect = putPeriodicEffectOnTheGround(command);

			return List.of(groundEffect);
		}

		var appliedEffects = new ArrayList<EffectInstance>();

		targetResolver.forEachTarget(
				command,
				effectTarget -> {
					var appliedEffect = applyEffect(command, effectTarget);

					if (appliedEffect != null) {
						appliedEffects.add(appliedEffect);
					}
				}
		);

		return appliedEffects;
	}

	private EffectInstance applyEffect(ApplyEffect command, Unit target) {
		if (!checkSecondaryCondition(command, target) || !hitRollOnlyOnce(target)) {
			return null;
		}

		var replacementMode = command.replacementMode();
		var appliedEffect = createEffect(command, target);
		var augmentations = getEffectAugmentations(appliedEffect);

		appliedEffect.augment(augmentations);
		target.addEffect(appliedEffect, replacementMode);
		return appliedEffect;
	}

	private EffectInstance createEffect(ApplyEffect command, Unit target) {
		var durationSnapshot = caster.getEffectDurationSnapshot(spell, target, command);
		var duration = durationSnapshot.getDuration();
		var tickInterval = durationSnapshot.getTickInterval();

		if (tickInterval.isPositive()) {
			return new PeriodicEffectInstance(
					caster,
					target,
					command.effect(),
					duration,
					tickInterval,
					command.numStacks(),
					command.numCharges(),
					getNumCounters(command),
					effectSource,
					getSourceSpell(),
					this
			);
		} else {
			return new NonPeriodicEffectInstance(
					caster,
					target,
					command.effect(),
					duration,
					command.numStacks(),
					command.numCharges(),
					getNumCounters(command),
					effectSource,
					getSourceSpell(),
					this
			);
		}
	}

	private PeriodicEffectInstance putPeriodicEffectOnTheGround(ApplyEffect command) {
		var effect = createGroundPeriodicEffect(command);
		var replacementMode = command.replacementMode();

		getSimulation().addGroundEffect(effect, replacementMode);
		return effect;
	}

	private PeriodicEffectInstance createGroundPeriodicEffect(ApplyEffect command) {
		var durationSnapshot = caster.getEffectDurationSnapshot(spell, null, command);
		var duration = durationSnapshot.getDuration();
		var tickInterval = durationSnapshot.getTickInterval();

		return new PeriodicEffectInstance(
				caster,
				null,
				command.effect(),
				duration,
				tickInterval,
				command.numStacks(),
				command.numCharges(),
				getNumCounters(command),
				effectSource,
				getSourceSpell(),
				this
		);
	}

	private int getNumCounters(ApplyEffect command) {
		var counterParams = command.counterParams();

		return switch (counterParams.scaling()) {
			case DEFAULT -> counterParams.number();
			case LAST_DAMAGE_DONE_PCT -> (int) (counterParams.number() * parentContext.getLastDamageDone() / 100.0);
		};
	}

	private EffectAugmentations getEffectAugmentations(EffectInstance effect) {
		var target = effect.getTarget();

		return getCharacterCalculationService().getEffectAugmentations(caster, spell, target);
	}

	private boolean shouldAddBonus(ChangeHealthDirectly command, Unit target) {
		var bonus = command.bonus();

		return bonus != null && target.hasEffect(bonus.requiredEffect(), caster);
	}
}
