package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Gem;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.EquippableItemDTO;
import wow.minmax.model.dto.SocketDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EquippableItemConverter extends Converter<EquippableItem, EquippableItemDTO> {
	private final ItemConverter itemConverter;
	private final EnchantConverter enchantConverter;
	private final GemConverter gemConverter;

	@Override
	protected EquippableItemDTO doConvert(EquippableItem item) {
		List<SocketDTO> sockets = new ArrayList<>();
		for (int i = 0; i < item.getSocketCount(); ++i) {
			Gem gem = item.getGem(i);
			sockets.add(new SocketDTO(gemConverter.convert(gem), false));
		}
		return new EquippableItemDTO(
				itemConverter.convert(item.getItem()),
				enchantConverter.convert(item.getEnchant()),
				sockets,
				false,
				null
		);
	}
}
