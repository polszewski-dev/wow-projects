package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Data
@AllArgsConstructor
public class EquipmentDTO {
	private EquippableItemDTO head;
	private EquippableItemDTO neck;
	private EquippableItemDTO shoulder;
	private EquippableItemDTO back;
	private EquippableItemDTO chest;
	private EquippableItemDTO wrist;
	private EquippableItemDTO hands;
	private EquippableItemDTO waist;
	private EquippableItemDTO legs;
	private EquippableItemDTO feet;
	private EquippableItemDTO finger1;
	private EquippableItemDTO finger2;
	private EquippableItemDTO trinket1;
	private EquippableItemDTO trinket2;
	private EquippableItemDTO mainHand;
	private EquippableItemDTO offHand;
	private EquippableItemDTO ranged;

	public List<EquippableItemDTO> toList() {
		return getItemStream().collect(Collectors.toList());
	}

	private Stream<EquippableItemDTO> getItemStream() {
		return Stream.of(getItemArray()).filter(Objects::nonNull);
	}

	private EquippableItemDTO[] getItemArray() {
		return new EquippableItemDTO[]{ head, neck, shoulder, back, chest, wrist, hands, waist, legs, feet, finger1, finger2, trinket1, trinket2, mainHand, offHand, ranged};
	}
}
