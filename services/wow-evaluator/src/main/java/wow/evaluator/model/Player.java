package wow.evaluator.model;

import wow.character.model.Copyable;
import wow.character.model.character.PlayerCharacter;
import wow.commons.model.attribute.AttributeScalingParams;
import wow.commons.model.spell.Ability;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
public interface Player extends Unit, PlayerCharacter, Copyable<Player>, AttributeScalingParams {
	boolean canCast(Ability ability);
}
