package wow.minmax.model;

import wow.character.model.Copyable;
import wow.commons.model.attribute.AttributeScalingParams;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
public interface PlayerCharacter extends Character, wow.character.model.character.PlayerCharacter, Copyable<PlayerCharacter>, AttributeScalingParams {
}
