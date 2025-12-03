package wow.simulator.model.unit;

import wow.character.util.SpellTargetConditionChecker;
import wow.commons.model.spell.SpellTarget;
import wow.commons.model.spell.SpellTargets;
import wow.commons.model.spell.component.ComponentCommand;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static wow.commons.model.spell.SpellTargetType.FRIENDS_PARTY;

/**
 * User: POlszewski
 * Date: 2023-11-02
 */
public class TargetResolver implements SimulationContextSource {
	private final Unit self;
	private Unit friend;
	private Unit enemy;
	private Unit any;
	private Unit target;

	private TargetResolver(Unit self) {
		Objects.requireNonNull(self);
		this.self = self;
	}

	public static TargetResolver ofSelf(Unit self) {
		return new TargetResolver(self);
	}

	public static TargetResolver ofFriend(Unit self, Unit friend) {
		Objects.requireNonNull(self);
		Objects.requireNonNull(friend);

		if (!Unit.areFriendly(self, friend)) {
			throw new IllegalArgumentException();
		}

		var targetResolver = new TargetResolver(self);
		targetResolver.friend = friend;
		return targetResolver;
	}

	public static TargetResolver ofEnemy(Unit self, Unit enemy) {
		Objects.requireNonNull(self);
		Objects.requireNonNull(enemy);

		if (!Unit.areHostile(self, enemy)) {
			throw new IllegalArgumentException();
		}

		var targetResolver = new TargetResolver(self);
		targetResolver.enemy = enemy;
		return targetResolver;
	}

	public static TargetResolver ofAny(Unit self, Unit any) {
		Objects.requireNonNull(self);
		Objects.requireNonNull(any);

		var targetResolver = new TargetResolver(self);
		targetResolver.any = any;
		return targetResolver;
	}

	public static TargetResolver ofTarget(Unit self, Unit target) {
		Objects.requireNonNull(target);
		var targetResolver = new TargetResolver(self);
		targetResolver.target = target;
		return targetResolver;
	}

	public boolean hasValidTarget(SpellTarget spellTarget) {
		if (spellTarget.isAoE()) {
			return true;
		}
		if (spellTarget.hasType(FRIENDS_PARTY)) {
			return hasValidTarget(SpellTargets.FRIEND);
		}
		return getTargets(spellTarget).size() == 1;
	}

	public boolean hasAllValidTargets(Collection<SpellTarget> spellTargets) {
		return spellTargets.stream().allMatch(this::hasValidTarget);
	}

	public void forEachTarget(ComponentCommand command, Consumer<Unit> consumer) {
		getTargets(command.target()).forEach(consumer);
	}

	private List<Unit> getTargets(SpellTarget spellTarget) {
		return getTargetsUnchecked(spellTarget).stream()
				.filter(target -> SpellTargetConditionChecker.check(spellTarget.condition(), target, self))
				.toList();
	}

	private List<Unit> getTargetsUnchecked(SpellTarget spellTarget) {
		return switch (spellTarget.type()) {
			case SELF ->
					List.of(self);
			case PET ->
					List.of();//todo
			case FRIEND ->
					List.of(friend);
			case ENEMY ->
					List.of(enemy);
			case ANY ->
					List.of(any);
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
					throw new IllegalArgumentException(spellTarget.type() + "");
		};
	}

	@Override
	public SimulationContext getSimulationContext() {
		return self.getSimulationContext();
	}
}
