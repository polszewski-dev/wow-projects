package wow.commons.client.converter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.client.WowCommonsClientSpringTest;
import wow.commons.client.converter.equipment.ItemConverter;
import wow.commons.client.dto.PhaseDTO;
import wow.commons.client.dto.equipment.ItemDTO;
import wow.commons.model.categorization.ArmorSubType;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.WeaponSubType;
import wow.commons.repository.item.ItemRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.item.SocketType.BLUE;
import static wow.commons.model.item.SocketType.YELLOW;
import static wow.commons.model.pve.PhaseId.TBC_P3;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class ItemConverterTest extends WowCommonsClientSpringTest {
	@Autowired
	ItemConverter itemConverter;

	@Autowired
	ItemRepository itemRepository;

	@Test
	void converNonSetItem() {
		var item = itemRepository.getItem("Wand of the Demonsoul", TBC_P5).orElseThrow();
		var converted = itemConverter.convert(item);

		assertThat(converted).isEqualTo(
				new ItemDTO(
						34347,
						"Wand of the Demonsoul",
						ItemRarity.EPIC,
						ItemType.RANGED,
						WeaponSubType.WAND,
						154,
						"SWP",
						"SWP",
						List.of(YELLOW),
						"+2 Spell Damage and Healing",
						"inv_wand_25",
						"""
								+9 Stamina
								+10 Intellect
								Equip: Improves spell haste rating by 18.
								Equip: Increases damage and healing done by magical spells and effects by up to 22.
								Sockets: [Y] +2 Spell Damage and Healing""",
						"",
						new PhaseDTO(TBC_P5, "TBC P5", 70)
				)
		);
	}

	@Test
	void convertSetItem() {
		var item = itemRepository.getItem("Mantle of the Malefic", TBC_P5).orElseThrow();
		var converted = itemConverter.convert(item);

		assertThat(converted).isEqualTo(
				new ItemDTO(
						31054,
						"Mantle of the Malefic",
						ItemRarity.EPIC,
						ItemType.SHOULDER,
						ArmorSubType.CLOTH,
						146,
						"BT",
						"BT - Mother Shahraz",
						List.of(BLUE, YELLOW),
						"+4 Spell Damage and Healing",
						"inv_shoulder_68",
						"""
								183 Armor
								+45 Stamina
								+22 Intellect
								Equip: Improves spell hit rating by 21.
								Equip: Improves spell critical strike rating by 13.
								Equip: Increases damage and healing done by magical spells and effects by up to 46.
								Sockets: [B][Y] +4 Spell Damage and Healing
								Set: Malefic Raiment""",
						"Set: Malefic Raiment",
						new PhaseDTO(TBC_P3, "TBC P3", 70)
				)
		);
	}

	@Test
	void convertBack() {
		var item = new ItemDTO(34347, null, null, null, null, 0, null, null, null, null, null, null, null, null);
		var converted = itemConverter.convertBack(item, TBC_P5);

		assertThat(converted.getId()).isEqualTo(34347);
	}
}