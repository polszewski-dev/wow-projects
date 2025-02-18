package wow.minmax.model;

import wow.character.model.Copyable;
import wow.character.model.character.PlayerCharacter;
import wow.commons.model.attribute.AttributeScalingParams;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
public interface Player extends Unit, PlayerCharacter, Copyable<Player>, AttributeScalingParams {
}
