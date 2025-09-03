package wow.character.repository.impl.parser.character;

import wow.character.model.character.GearSet;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.commons.repository.item.EnchantRepository;
import wow.commons.repository.item.GemRepository;
import wow.commons.repository.item.ItemRepository;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public class GearSetSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colSlot = column("slot");
	private final ExcelColumn colItem = column("item", true);
	private final ExcelColumn colEnchant = column("enchant");
	private final ExcelColumn colGemNo1 = column("gem #1");
	private final ExcelColumn colGemNo2 = column("gem #2");
	private final ExcelColumn colGemNo3 = column("gem #3");

	private final ItemRepository itemRepository;
	private final EnchantRepository enchantRepository;
	private final GemRepository gemRepository;

	private final GearSetExcelParser parser;

	public GearSetSheetParser(String sheetName, ItemRepository itemRepository, EnchantRepository enchantRepository, GemRepository gemRepository, GearSetExcelParser parser) {
		super(sheetName);
		this.itemRepository = itemRepository;
		this.enchantRepository = enchantRepository;
		this.gemRepository = gemRepository;
		this.parser = parser;
	}

	@Override
	protected void readSingleRow() {
		if (colItem.isEmpty()) {
			return;
		}

		var gearSet = getGearSet();
		parser.addGearSet(gearSet);
	}

	private GearSet getGearSet() {
		var name = colName.getString();
		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();
		var slot = colSlot.getEnum(ItemSlot::parse);
		var item = getEquippableItem(timeRestriction.earliestPhaseId());

		return new GearSet(
				name,
				characterRestriction,
				timeRestriction,
				Map.of(slot, item)
		);
	}

	private EquippableItem getEquippableItem(PhaseId phaseId) {
		var optionalItem = getItem(phaseId);

		var enchantName = colEnchant.getString(null);
		var gemNo1Name = colGemNo1.getString(null);
		var gemNo2Name = colGemNo2.getString(null);
		var gemNo3Name = colGemNo3.getString(null);

		var item = optionalItem.orElseThrow();
		var enchant = enchantName != null
				? enchantRepository.getEnchant(enchantName, phaseId).orElseThrow()
				: null;
		var gemNo1 = getGem(gemNo1Name, phaseId);
		var gemNo2 = getGem(gemNo2Name, phaseId);
		var gemNo3 = getGem(gemNo3Name, phaseId);
		var gems = Stream.of(gemNo1, gemNo2, gemNo3)
				.map(x -> x.orElse(null))
				.limit(item.getSocketCount())
				.toList();

		return new EquippableItem(item).enchant(enchant).gem(gems);
	}

	private Optional<Item> getItem(PhaseId phaseId) {
		var itemName = colItem.getString(null);

		if (itemName == null) {
			return Optional.empty();
		}

		if (itemName.contains(ID_PREFIX)) {
			var itemId = getId(itemName);
			return itemRepository.getItem(itemId, phaseId);
		}

		return itemRepository.getItem(itemName, phaseId);
	}

	private Optional<Gem> getGem(String gemName, PhaseId phaseId) {
		if (gemName == null) {
			return Optional.empty();
		}

		if (gemName.contains(ID_PREFIX)) {
			var gemId = getId(gemName);
			return gemRepository.getGem(gemId, phaseId);
		}

		return gemRepository.getGem(gemName, phaseId);
	}

	private static int getId(String itemName) {
		var itemIdStr = itemName.substring(itemName.indexOf(ID_PREFIX) + 1);
		return Integer.parseInt(itemIdStr);
	}

	private static final String ID_PREFIX = "#";
}
