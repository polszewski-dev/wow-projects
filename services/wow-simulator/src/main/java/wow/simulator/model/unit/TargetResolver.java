package wow.simulator.model.unit;

import wow.commons.model.effect.component.PeriodicComponent;
import wow.commons.model.spell.EffectApplication;
import wow.commons.model.spell.SpellTarget;
import wow.commons.model.spell.component.DirectComponent;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static wow.commons.model.spell.SpellTarget.FRIEND;
import static wow.commons.model.spell.SpellTarget.FRIENDS_PARTY;

/**
 * User: POlszewski
 * Date: 2023-11-02
 */
public class TargetResolver implements SimulationContextSource {
	private final Unit self;
	private Unit friend;
	private Unit enemy;
	private Unit target;

	private TargetResolver(Unit self) {
		Objects.requireNonNull(self);
		this.self = self;
	}

	public static TargetResolver ofSelf(Unit self) {
		return new TargetResolver(self);
	}

	public static TargetResolver ofFriend(Unit self, Unit friend) {
		Objects.requireNonNull(friend);
		if (!Unit.areFriendly(self, friend)) {
			throw new IllegalArgumentException();
		}
		var targetResolver = new TargetResolver(self);
		targetResolver.friend = friend;
		return targetResolver;
	}

	public static TargetResolver ofEnemy(Unit self, Unit enemy) {
		Objects.requireNonNull(enemy);
		if (!Unit.areHostile(self, enemy)) {
			throw new IllegalArgumentException();
		}
		var targetResolver = new TargetResolver(self);
		targetResolver.enemy = enemy;
		return targetResolver;
	}

	public static TargetResolver ofTarget(Unit self, Unit target) {
		Objects.requireNonNull(target);
		var targetResolver = new TargetResolver(self);
		targetResolver.target = target;
		return targetResolver;
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
			case FRIEND ->
					List.of(friend);
			case ENEMY ->
					List.of(enemy);
			case TARGET ->
					List.of(target);
			case PARTY ->
					self.getParty().getPlayers();
			case FRIENDS_PARTY ->
					friend.getParty().getPlayers();
			case ENEMY_AOE ->
					getSimulation().getEnemiesOf(self);
			case FRIEND_AOE ->
					getSimulation().getFriendsOf(self);
			default ->
					throw new IllegalArgumentException(targetType + "");
		};
	}

	@Override
	public SimulationContext getSimulationContext() {
		return self.getSimulationContext();
	}
}
