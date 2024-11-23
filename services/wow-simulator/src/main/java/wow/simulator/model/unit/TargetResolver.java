package wow.simulator.model.unit;

import wow.commons.model.spell.SpellTarget;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static wow.commons.model.spell.SpellTarget.*;

/**
 * User: POlszewski
 * Date: 2023-11-02
 */
public class TargetResolver {
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

	public Unit getTarget(SpellTarget targetType) {
		return switch (targetType) {
			case SELF ->
					self;
			case PET ->
					throw new UnsupportedOperationException("No pets atm");
			default ->
					targets.get(targetType);
		};
	}

	public boolean hasValidTarget(SpellTarget targetType) {
		if (targetType.isAoE()) {
			return true;
		}
		return getTarget(targetType) != null;
	}

	public boolean hasAllValidTargets(Collection<SpellTarget> targetTypes) {
		return targetTypes.stream().allMatch(this::hasValidTarget);
	}
}
