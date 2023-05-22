package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wow.commons.model.item.SocketType;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-05-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GemOptionsDTO {
	private SocketType socketType;
	private List<GemDTO> gems;
}
