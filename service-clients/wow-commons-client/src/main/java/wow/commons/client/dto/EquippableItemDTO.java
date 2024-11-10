package wow.commons.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EquippableItemDTO {
	private ItemDTO item;
	private EnchantDTO enchant;
	private List<GemDTO> gems;
}
