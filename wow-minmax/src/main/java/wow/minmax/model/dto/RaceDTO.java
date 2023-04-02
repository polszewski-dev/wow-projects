package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wow.commons.model.character.RaceId;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaceDTO {
	private RaceId id;
	private String name;
	private String icon;
}
