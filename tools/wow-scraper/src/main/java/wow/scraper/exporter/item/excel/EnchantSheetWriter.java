package wow.scraper.exporter.item.excel;

import wow.scraper.parser.tooltip.EnchantTooltipParser;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.*;
import static wow.commons.repository.impl.parser.item.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
public class EnchantSheetWriter extends ItemBaseSheetWriter<EnchantTooltipParser> {
	public EnchantSheetWriter(EnchantExcelBuilder builder) {
		super(builder);
	}

	@Override
	public void writeHeader() {
		setHeader(ID);
		setHeader(NAME, 30);
		setHeader(ENCHANT_ITEM_TYPES, 20);
		setHeader(ENCHANT_ITEM_SUBTYPES, 20);
		setHeader(ENCHANT_REQ_ILVL);
		setHeader(RARITY);
		writeTimeRestrictionHeader();
		setHeader(REQ_CLASS);
		setHeader(REQ_PROFESSION);
		setHeader(REQ_PROFESSION_LEVEL);
		setHeader(REQ_XFACTION);
		setHeader(PVE_ROLES);
		setHeader(ENCHANT_ENCHANT_ID);
		writeEffectHeader(ENCHANT_EFFECT_PREFIX, 1);
		writeIconAndTooltipHeader();
	}

	@Override
	public void writeRow(EnchantTooltipParser parser) {
		setValue(parser.getSpellId());
		setValue(parser.getName());
		setValue(parser.getItemTypes());
		setValue(parser.getItemSubTypes(), Object::toString);
		setValue(parser.getRequiredItemLevel());
		setValue(getItemRarity(parser));
		writeTimeRestriction(parser.getTimeRestriction());
		setValue(parser.getRequiredClass());
		setValue(parser.getRequiredProfession());
		setValue(parser.getRequiredProfessionLevel());
		setValue(parser.getExclusiveFaction());
		writePveRoles(parser);
		setValue(parser.getDetails().getEnchantId());
		writeEffect(parser.getEffect().orElseThrow());
		writeIconAndTooltip(parser);
	}

	private void writePveRoles(EnchantTooltipParser parser) {
		if (!parser.getPveRoles().isEmpty()) {
			setValue(parser.getPveRoles());
		} else {
			writePveRoles(parser.getEffect().stream().toList(), null, 0);
		}
	}
}
