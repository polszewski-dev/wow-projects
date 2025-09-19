package wow.estimator.model;

import wow.character.model.Copyable;
import wow.character.model.character.PlayerCharacter;
import wow.commons.model.attribute.AttributeScalingParams;
import wow.commons.model.spell.Ability;
import wow.estimator.model.impl.BuildWithRotation;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
public interface Player extends Unit, PlayerCharacter, Copyable<Player>, AttributeScalingParams {
	@Override
	BuildWithRotation getBuild();

	boolean canCast(Ability ability);

	default Rotation getRotation() {
		return getBuild().getRotation().compile(this);
	}
}
