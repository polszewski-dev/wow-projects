package wow.minmax.model;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
public interface Character extends wow.character.model.character.Character {
	@Override
	Character getTarget();
}
