package wow.minmax.client.dto;

/**
 * User: POlszewski
 * Date: 2023-04-09
 */
public record PlayerCharacterDTO(
		String characterId,
		CharacterClassDTO characterClass,
		RaceDTO race
) {
}
