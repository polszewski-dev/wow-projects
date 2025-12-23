package wow.simulator.model.context;

import wow.character.model.snapshot.PeriodicSpellComponentSnapshot;
import wow.commons.model.effect.component.PeriodicComponent;
import wow.commons.model.spell.TickScheme;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.TargetResolver;
import wow.simulator.model.unit.Unit;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static wow.commons.model.spell.component.ComponentCommand.*;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
public class EffectUpdateContext extends Context {
	private final EffectInstance effect;
	private final PeriodicComponent periodicComponent;
	private final TargetResolver targetResolver;
	private final Map<CommandAndTarget, PeriodicSpellComponentSnapshot> periodicSnapshots = new HashMap<>();

	private record CommandAndTarget(PeriodicCommand command, Unit target) {}

	public EffectUpdateContext(EffectInstance effect, Context parentContext) {
		super(effect.getOwner(), effect.getSourceSpell(), parentContext);
		this.effect = effect;
		this.periodicComponent = effect.getPeriodicComponent();
		this.targetResolver = getTargetResolver();
		forEach(this::computeSingleTargetSnapshot);
	}

	public void periodicComponentAction(int tickNo, int numStacks) {
		forEach((command, target) -> periodicComponentAction(tickNo, numStacks, command, target));

		// multi-target periodic command requires snapshot to be computed each time damage is dealt

		periodicSnapshots.entrySet().removeIf(e -> e.getKey().command().isAoE());
	}

	private void periodicComponentAction(int tickNo, int numStacks, PeriodicCommand periodicCommand, Unit target) {
		switch (periodicCommand) {
			case DealDamagePeriodically command ->
					dealPeriodicDamage(tickNo, numStacks, command, target);

			case HealPeriodically command ->
					periodicHeal(tickNo, numStacks, command, target);

			case LoseManaPeriodically command ->
					periodicManaLoss(tickNo, numStacks, command, target);

			case GainManaPeriodically command ->
					periodicManaGain(tickNo, numStacks, command, target);

			case GainPctOfTotalManaPeriodically command ->
					periodicPctOfTotalManaGain(tickNo, numStacks, command, target);

			case Copy command ->
					copy(command, target, getLastValueSnapshot());

			case DealCounterDamagePeriodically command ->
					dealCounterDamagePeriodically(tickNo, command, target);

			default ->
					throw new UnsupportedOperationException();
		}
	}

	private void dealPeriodicDamage(int tickNo, int numStacks, DealDamagePeriodically command, Unit target) {
		// multi-target periodic component requires hit roll each time damage is dealt

		if (command.isAoE() && !hitRoll(target)) {
			return;
		}

		var snapshot = getSpellDamageSnapshot(command, target);
		var tickDamage = getRoundedTickAmount(snapshot, tickNo, numStacks, target);

		decreaseHealth(target, tickDamage, false, false);
	}

	private void periodicHeal(int tickNo, int numStacks, HealPeriodically command, Unit target) {
		var snapshot = getHealingSnapshot(command, target);
		var tickHealing = getRoundedTickAmount(snapshot, tickNo, numStacks, target);

		increaseHealth(target, tickHealing, false, false);
	}

	private void periodicManaLoss(int tickNo, int numStacks, LoseManaPeriodically command, Unit target) {
		var snapshot = getManaLossSnapshot(command, target);
		var roundedTickManaLoss = getRoundedTickAmount(snapshot, tickNo, numStacks, target);

		decreaseMana(target, roundedTickManaLoss);
	}

	private void periodicManaGain(int tickNo, int numStacks, GainManaPeriodically command, Unit target) {
		var snapshot = getManaGainSnapshot(command, target);
		var roundedTickManaGain = getRoundedTickAmount(snapshot, tickNo, numStacks, target);

		increaseMana(target, roundedTickManaGain);
	}

	private void periodicPctOfTotalManaGain(int tickNo, int numStacks, GainPctOfTotalManaPeriodically command, Unit target) {
		var snapshot = getPctOfTotalManaGainSnapshot(command, target);
		var roundedTickManaGain = getRoundedTickAmount(snapshot, tickNo, numStacks, target);

		increaseMana(target, roundedTickManaGain);
	}

	private void dealCounterDamagePeriodically(int tickNo, DealCounterDamagePeriodically command, Unit target) {
		var damageCommand = new DealDamagePeriodically(
				command.target(),
				command.coefficient(),
				effect.getNumCounters(),
				command.numTicks(),
				TickScheme.DEFAULT
		);

		dealPeriodicDamage(tickNo, 1, damageCommand, target);
	}

	private int getRoundedTickAmount(PeriodicSpellComponentSnapshot snapshot, int tickNo, int numStacks, Unit target) {
		var tickAmount = numStacks * snapshot.getTickAmount(tickNo);

		return roundValue(tickAmount, target);
	}

	private TargetResolver getTargetResolver() {
		return effect.getTarget() != null
				? TargetResolver.ofTarget(effect.getOwner(), effect.getTarget())
				: TargetResolver.ofSelf(effect.getOwner());
	}

	private void forEach(BiConsumer<PeriodicCommand, Unit> consumer) {
		if (periodicComponent == null) {
			return;
		}

		for (var command : periodicComponent.commands()) {
			targetResolver.forEachTarget(
					command,
					target -> consumer.accept(command, target)
			);
		}
	}

	private void computeSingleTargetSnapshot(PeriodicCommand periodicCommand, Unit target) {
		if (!periodicCommand.isSingleTarget()) {
			return;
		}

		switch (periodicCommand) {
			case DealDamagePeriodically command ->
					getSpellDamageSnapshot(command, target);

			case HealPeriodically command ->
					getHealingSnapshot(command, target);

			case GainPctOfTotalManaPeriodically command ->
					getPctOfTotalManaGainSnapshot(command, target);

			default -> {
				// void
			}
		}
	}

	private PeriodicSpellComponentSnapshot getSpellDamageSnapshot(DealDamagePeriodically command, Unit target) {
		return computeSnapshotOnce(
				command,
				target,
				() -> caster.getPeriodicSpellDamageSnapshot(spell, target, command)
		);
	}

	private PeriodicSpellComponentSnapshot getHealingSnapshot(HealPeriodically command, Unit target) {
		return computeSnapshotOnce(
				command,
				target,
				() -> caster.getPeriodicHealingSnapshot(spell, target, command)
		);
	}

	private PeriodicSpellComponentSnapshot getManaLossSnapshot(LoseManaPeriodically command, Unit target) {
		return computeSnapshotOnce(
				command,
				target,
				() -> caster.getPeriodicManaLossSnapshot(spell, target, command)
		);
	}


	private PeriodicSpellComponentSnapshot getManaGainSnapshot(GainManaPeriodically command, Unit target) {
		return computeSnapshotOnce(
				command,
				target,
				() -> caster.getPeriodicManaGainSnapshot(spell, target, command)
		);
	}

	private PeriodicSpellComponentSnapshot getPctOfTotalManaGainSnapshot(GainPctOfTotalManaPeriodically command, Unit target) {
		return computeSnapshotOnce(
				command,
				target,
				() -> caster.getPeriodicPctOfTotalManaGainSnapshot(spell, target, command)
		);
	}

	private PeriodicSpellComponentSnapshot computeSnapshotOnce(PeriodicCommand command, Unit target, Supplier<PeriodicSpellComponentSnapshot> supplier) {
		return periodicSnapshots.computeIfAbsent(
				new CommandAndTarget(command, target),
				x -> supplier.get()
		);
	}

	public void increaseEffect(double effectIncreasePct) {
		periodicSnapshots.values().forEach(
				snapshot -> snapshot.increaseEffect(effectIncreasePct)
		);
	}
}
