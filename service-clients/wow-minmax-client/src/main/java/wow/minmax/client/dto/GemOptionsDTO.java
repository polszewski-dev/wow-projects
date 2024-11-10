package wow.minmax.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.client.dto.GemDTO;
import wow.commons.model.item.SocketType;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-05-23
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GemOptionsDTO {
	private SocketType socketType;
	private List<GemDTO> gems;
}
