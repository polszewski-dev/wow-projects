package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewProfileOptionsDTO {
	private List<CharacterClassDTO> classOptions;
}
