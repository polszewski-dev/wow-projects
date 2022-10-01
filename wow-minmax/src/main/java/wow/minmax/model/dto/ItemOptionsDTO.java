package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-31
 */
@Data
@AllArgsConstructor
public class ItemOptionsDTO {
	private List<EnchantDTO> availableEnchants;
	private List<GemDTO> availableGems1;
	private List<GemDTO> availableGems2;
	private List<GemDTO> availableGems3;
}
