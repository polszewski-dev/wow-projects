package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquippableItemDTO {
	private ItemDTO item;
	private EnchantDTO enchant;
	private List<GemDTO> gems;
}
