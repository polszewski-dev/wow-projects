package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewProfileOptionsDTO {
	private List<CharacterClassDTO> classOptions;
}
