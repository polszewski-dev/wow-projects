package wow.simulator.model.context;

import wow.character.model.snapshot.PeriodicSpellComponentSnapshot;
import wow.commons.model.effect.component.PeriodicComponent;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.TargetResolver;
import wow.simulator.model.unit.Unit;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static wow.commons.model.spell.component.ComponentCommand.PeriodicCommand;

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

	private void periodicComponentAction(int tickNo, int numStacks, PeriodicCommand command, Unit target) {
		switch (command.type()) {
			case DAMAGE ->
					dealPeriodicDamage(tickNo, numStacks, command, target);
			case HEAL ->
					periodicHeal(tickNo, numStacks, command, target);
			case MANA_DRAIN ->
					periodicManaDrain(tickNo, numStacks, command, target);
			case MANA_GAIN ->
					periodicManaGain(tickNo, numStacks, command, target);
			case PCT_OF_TOTAL_MANA_GAIN ->
					periodicPctOfTotalManaGain(tickNo, numStacks, command, target);
			case COPY_DAMAGE_AS_HEAL_PCT ->
					copyAsHeal(target, this.getLastDamageDone(), command.amount());
			case COPY_MANA_DRAINED_AS_MANA_PCT ->
					copyAsManaGain(target, this.getLastManaDrained(), command.amount());
			default ->
					throw new UnsupportedOperationException();
		}
	}

	private void dealPeriodicDamage(int tickNo, int numStacks, PeriodicCommand command, Unit target) {
		// multi-target periodic component requires hit roll each time damage is dealt

		if (command.isAoE() && !hitRoll(target)) {
			return;
		}

		var snapshot = getSpellDamageSnapshot(command, target);
		var tickDamage = getRoundedTickAmount(snapshot, tickNo, numStacks, target);

		decreaseHealth(target, tickDamage, false, false);
	}

	private void periodicHeal(int tickNo, int numStacks, PeriodicCommand command, Unit target) {
		var snapshot = getHealingSnapshot(command, target);
		var tickHealing = getRoundedTickAmount(snapshot, tickNo, numStacks, target);

		increaseHealth(target, tickHealing, false, false);
	}

	private void periodicManaDrain(int tickNo, int numStacks, PeriodicCommand command, Unit target) {
		var snapshot = getManaDrainSnapshot(command, target);
		var roundedTickManaDrain = getRoundedTickAmount(snapshot, tickNo, numStacks, target);

		decreaseMana(target, roundedTickManaDrain);
	}

	private void periodicManaGain(int tickNo, int numStacks, PeriodicCommand command, Unit target) {
		var snapshot = getManaGainSnapshot(command, target);
		var roundedTickManaGain = getRoundedTickAmount(snapshot, tickNo, numStacks, target);

		increaseMana(target, roundedTickManaGain);
	}

	private void periodicPctOfTotalManaGain(int tickNo, int numStacks, PeriodicCommand command, Unit target) {
		var snapshot = getPctOfTotalManaGainSnapshot(command, target);
		var roundedTickManaGain = getRoundedTickAmount(snapshot, tickNo, numStacks, target);

		increaseMana(target, roundedTickManaGain);
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

	private void computeSingleTargetSnapshot(PeriodicCommand command, Unit target) {
		if (!command.isSingleTarget()) {
			return;
		}

		switch (command.type()) {
			case DAMAGE ->
					getSpellDamageSnapshot(command, target);
			case HEAL ->
					getHealingSnapshot(command, target);
			case PCT_OF_TOTAL_MANA_GAIN ->
					getPctOfTotalManaGainSnapshot(command, target);
			default -> {
				// void
			}
		}
	}

	private PeriodicSpellComponentSnapshot getSpellDamageSnapshot(PeriodicCommand command, Unit target) {
		return computeSnapshotOnce(
				command,
				target,
				() -> caster.getPeriodicSpellDamageSnapshot(spell, target, command)
		);
	}

	private PeriodicSpellComponentSnapshot getHealingSnapshot(PeriodicCommand command, Unit target) {
		return computeSnapshotOnce(
				command,
				target,
				() -> caster.getPeriodicHealingSnapshot(spell, target, command)
		);
	}

	private PeriodicSpellComponentSnapshot getManaDrainSnapshot(PeriodicCommand command, Unit target) {
		return computeSnapshotOnce(
				command,
				target,
				() -> caster.getPeriodicManaDrainSnapshot(spell, target, command)
		);
	}


	private PeriodicSpellComponentSnapshot getManaGainSnapshot(PeriodicCommand command, Unit target) {
		return computeSnapshotOnce(
				command,
				target,
				() -> caster.getPeriodicManaGainSnapshot(spell, target, command)
		);
	}

	private PeriodicSpellComponentSnapshot getPctOfTotalManaGainSnapshot(PeriodicCommand command, Unit target) {
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
