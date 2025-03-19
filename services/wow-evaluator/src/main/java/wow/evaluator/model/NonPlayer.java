package wow.evaluator.model;

import wow.character.model.Copyable;
import wow.character.model.character.NonPlayerCharacter;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
public interface NonPlayer extends Unit, NonPlayerCharacter, Copyable<NonPlayer> {
}
