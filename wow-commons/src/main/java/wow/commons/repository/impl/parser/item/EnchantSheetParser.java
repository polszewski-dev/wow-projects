package wow.commons.repository.impl.parser.item;

import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.EnchantSource;
import wow.commons.model.item.impl.EnchantImpl;
import wow.commons.repository.PveRepository;
import wow.commons.repository.SpellRepository;
import wow.commons.repository.impl.ItemRepositoryImpl;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class EnchantSheetParser extends AbstractItemSheetParser {
	private final ExcelColumn colItemTypes = column(ENCHANT_ITEM_TYPES);
	private final ExcelColumn colItemSubTypes = column(ENCHANT_ITEM_SUBTYPES);
	private final ExcelColumn colReqILvl = column(ENCHANT_REQ_ILVL);
	private final ExcelColumn colRarity = column(RARITY);
	private final ExcelColumn colEnchantId = column(ENCHANT_ENCHANT_ID);

	public EnchantSheetParser(String sheetName, PveRepository pveRepository, SpellRepository spellRepository, ItemRepositoryImpl itemRepository) {
		super(sheetName, pveRepository, spellRepository, itemRepository);
	}

	@Override
	protected void readSingleRow() {
		Enchant enchant = getEnchant();
		itemRepository.addEnchant(enchant);
	}

	private Enchant getEnchant() {
		var id = colId.getInteger();
		var itemTypes = colItemTypes.getList(ItemType::parse);
		var itemSubTypes = colItemSubTypes.getList(ItemSubType::parse);
		var requiredItemLevel = colReqILvl.getInteger(1);
		var rarity = colRarity.getEnum(ItemRarity::parse);
		var enchantId = colEnchantId.getInteger();

		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();
		var pveRoles = getPveRoles();

		var enchant = new EnchantImpl(
				id, description, timeRestriction, characterRestriction, itemTypes, itemSubTypes, requiredItemLevel, rarity, pveRoles, enchantId
		);

		var effect = readItemEffect(ENCHANT_EFFECT_PREFIX, 1, timeRestriction, new EnchantSource(enchant));

		enchant.setEffect(effect);
		return enchant;
	}
}
