package wow.minmax.client.dto;

/**
 * User: POlszewski
 * Date: 2026-03-13
 */
public record PartyMemberDTO(
		String name,
		CharacterClassDTO characterClass,
		RaceDTO race
) {
}
