package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.model.character.RaceId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RaceDTO {
	private RaceId id;
	private String name;
	private String icon;
	private List<RacialDTO> racials;
}
