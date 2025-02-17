package wow.simulator.model.unit;

import wow.commons.model.effect.component.PeriodicComponent;
import wow.commons.model.spell.EffectApplication;
import wow.commons.model.spell.SpellTarget;
import wow.commons.model.spell.component.DirectComponent;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static wow.commons.model.spell.SpellTarget.*;

/**
 * User: POlszewski
 * Date: 2023-11-02
 */
public class TargetResolver implements SimulationContextSource {
	private final Unit self;
	private final Map<SpellTarget, Unit> targets;

	private TargetResolver(Unit self, Map<SpellTarget, Unit> targets) {
		Objects.requireNonNull(self);
		this.self = self;
		this.targets = targets;
	}

	public static TargetResolver ofSelf(Unit self) {
		return new TargetResolver(self, Map.of());
	}

	public static TargetResolver ofFriend(Unit self, Unit friend) {
		Objects.requireNonNull(friend);
		if (!Unit.areFriendly(self, friend)) {
			throw new IllegalArgumentException();
		}
		return new TargetResolver(self, Map.of(FRIEND, friend));
	}

	public static TargetResolver ofEnemy(Unit self, Unit enemy) {
		Objects.requireNonNull(enemy);
		if (!Unit.areHostile(self, enemy)) {
			throw new IllegalArgumentException();
		}
		return new TargetResolver(self, Map.of(ENEMY, enemy));
	}

	public static TargetResolver ofTarget(Unit self, Unit target) {
		Objects.requireNonNull(target);
		return new TargetResolver(self, Map.of(TARGET, target));
	}

	public boolean hasValidTarget(SpellTarget targetType) {
		if (targetType.isAoE()) {
			return true;
		}
		if (targetType == FRIENDS_PARTY) {
			return hasValidTarget(FRIEND);
		}
		return getTargets(targetType).size() == 1;
	}

	public boolean hasAllValidTargets(Collection<SpellTarget> targetTypes) {
		return targetTypes.stream().allMatch(this::hasValidTarget);
	}

	public void forEachTarget(DirectComponent directComponent, Consumer<Unit> consumer) {
		getTargets(directComponent.target()).forEach(consumer);
	}

	public void forEachTarget(PeriodicComponent periodicComponent, Consumer<Unit> consumer) {
		getTargets(periodicComponent.target()).forEach(consumer);
	}

	public void forEachTarget(EffectApplication effectApplication, Consumer<Unit> consumer) {
		getTargets(effectApplication.target()).forEach(consumer);
	}

	private List<Unit> getTargets(SpellTarget targetType) {
		return switch (targetType) {
			case SELF ->
					List.of(self);
			case PET ->
					List.of();//todo
			case PARTY ->
					self.getParty().getPlayers();
			case FRIENDS_PARTY ->
					targets.get(FRIEND).getParty().getPlayers();
			case ENEMY_AOE ->
					getSimulation().getEnemiesOf(self);
			case FRIEND_AOE ->
					getSimulation().getFriendsOf(self);
			default -> {
				var unit = targets.get(targetType);
				yield unit != null ? List.of(unit) : List.of();
			}
		};
	}

	@Override
	public SimulationContext getSimulationContext() {
		return self.getSimulationContext();
	}
}
