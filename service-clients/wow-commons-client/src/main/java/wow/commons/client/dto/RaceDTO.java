package wow.commons.client.dto;

import wow.commons.model.character.RaceId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
public record RaceDTO(
		RaceId id,
		String name,
		String icon,
		List<RacialDTO> racials
) {
	public RaceDTO withRacials(List<RacialDTO> racials) {
		return new RaceDTO(
				id, name, icon, racials
		);
	}
}
