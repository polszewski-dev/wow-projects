package wow.simulator.model.unit;

import lombok.AllArgsConstructor;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.SpellTarget;

import java.util.List;

import static wow.commons.model.spell.SpellTargetType.*;
import static wow.commons.model.spell.component.ComponentCommand.Copy;
import static wow.simulator.model.unit.Unit.areFriendly;
import static wow.simulator.model.unit.Unit.areHostile;

/**
 * User: POlszewski
 * Date: 2024-11-24
 */
@AllArgsConstructor
public class PrimaryTargetResolver {
	private final Ability ability;
	private final Unit self;
	private final Unit defaultTarget;
	private final Unit explicitTarget;

	public PrimaryTarget getPrimaryTarget() {
		var singleTargets = getSingleTargets();

		if (singleTargets.isEmpty()) {
			if (explicitTarget != null) {
				return PrimaryTarget.INVALID;
			} else {
				return PrimaryTarget.EMPTY;
			}
		}

		var nonIgnorableSingleTargets = getNonIgnorableSingleTargets();

		if (nonIgnorableSingleTargets.size() == 1) {
			return resolveTarget(nonIgnorableSingleTargets.getFirst());
		}

		if (singleTargets.size() == 1) {
			return resolveTarget(singleTargets.getFirst());
		}

		return PrimaryTarget.INVALID;
	}

	private List<SpellTarget> getSingleTargets() {
		var targets = ability.getTargets();

		return targets.stream()
				.filter(SpellTarget::isSingle)
				.toList();
	}

	private List<SpellTarget> getNonIgnorableSingleTargets() {
		var targets = ability.getTargets(
				command -> !(
						command instanceof Copy &&
						(command.target().hasType(MASTER) || command.target().hasType(PET))
				),
				command -> true
		);

		return targets.stream()
				.filter(SpellTarget::isSingle)
				.filter(x -> !x.hasType(SELF))
				.toList();
	}

	private PrimaryTarget resolveTarget(SpellTarget spellTarget) {
		return switch (spellTarget.type()) {
			case SELF ->
					getSelf();
			case PET ->
					getActivePet();
			case MASTER ->
					getMaster();
			case FRIEND, FRIENDS_PARTY ->
					getFriendlyTarget();
			case ENEMY ->
					getHostileTarget();
			case ANY ->
					getAnyTarget();
			default ->
					throw new UnsupportedOperationException("No AoE targets atm");
		};
	}

	private PrimaryTarget getSelf() {
		if (explicitTarget == self || explicitTarget == null) {
			return PrimaryTarget.ofSelf(self);
		}

		return PrimaryTarget.INVALID;
	}

	private PrimaryTarget getActivePet() {
		if (explicitTarget == self || explicitTarget == null) {
			return PrimaryTarget.ofActivePet(self.getActivePet());
		}

		return PrimaryTarget.INVALID;
	}

	private PrimaryTarget getMaster() {
		if (explicitTarget == self || explicitTarget == null) {
			return PrimaryTarget.ofMaster(((Pet) self).getMaster());
		}

		return PrimaryTarget.INVALID;
	}

	private PrimaryTarget getFriendlyTarget() {
		if (explicitTarget != null) {
			if (areFriendly(self, explicitTarget)) {
				return PrimaryTarget.ofFriend(explicitTarget);
			} else {
				return PrimaryTarget.INVALID;
			}
		}

		if (defaultTarget != null && areFriendly(self, defaultTarget)) {
			return PrimaryTarget.ofFriend(defaultTarget);
		}

		return PrimaryTarget.ofFriend(self);
	}

	private PrimaryTarget getHostileTarget() {
		if (explicitTarget != null) {
			if (areHostile(self, explicitTarget)) {
				return PrimaryTarget.ofEnemy(explicitTarget);
			} else {
				return PrimaryTarget.INVALID;
			}
		}

		if (defaultTarget != null && areHostile(self, defaultTarget)) {
			return PrimaryTarget.ofEnemy(defaultTarget);
		}

		return PrimaryTarget.INVALID;
	}

	private PrimaryTarget getAnyTarget() {
		if (explicitTarget != null) {
			return PrimaryTarget.ofAny(explicitTarget);
		}
		if (defaultTarget != null) {
			return PrimaryTarget.ofAny(defaultTarget);
		}
		return PrimaryTarget.ofAny(self);
	}
}
