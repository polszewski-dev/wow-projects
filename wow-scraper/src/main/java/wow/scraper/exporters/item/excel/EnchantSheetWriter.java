package wow.scraper.exporters.item.excel;

import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.PveRole;
import wow.scraper.classifiers.PveRoleStatClassifier;
import wow.scraper.parsers.tooltip.EnchantTooltipParser;

import java.util.List;

import static wow.commons.repository.impl.parsers.excel.CommonColumnNames.*;
import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
public class EnchantSheetWriter extends ItemBaseSheetWriter<EnchantTooltipParser> {
	public EnchantSheetWriter(ItemBaseExcelBuilder builder) {
		super(builder);
	}

	@Override
	public void writeHeader() {
		setHeader(ID);
		setHeader(NAME, 30);
		setHeader(ENCHANT_ITEM_TYPES, 20);
		setHeader(ENCHANT_ITEM_SUBTYPES, 20);
		setHeader(RARITY);
		setHeader(REQ_VERSION);
		setHeader(REQ_PHASE);
		setHeader(REQ_CLASS);
		setHeader(REQ_PROFESSION);
		setHeader(REQ_PROFESSION_LEVEL);
		setHeader(REQ_XFACTION);
		setHeader(PVE_ROLES);
		setHeader(ENCHANT_ENCHANT_ID);
		writeAttributeHeader("", ENCHANT_MAX_STATS);
		writeIconAndTooltipHeader();
	}

	@Override
	public void writeRow(EnchantTooltipParser parser) {
		setValue(parser.getSpellId());
		setValue(parser.getName());
		setValue(parser.getItemTypes());
		setValue(parser.getItemSubTypes(), ItemSubType::name);
		setValue(getItemRarity(parser));
		setValue(parser.getGameVersion());
		setValue(parser.getPhase());
		setValue(parser.getRequiredClass());
		setValue(parser.getRequiredProfession());
		setValue(parser.getRequiredProfessionLevel());
		setValue(parser.getExclusiveFaction());
		setValue(getPveRoles(parser));
		setValue(parser.getDetails().getEnchantId());
		writeAttributes(parser.getParsedStats(), ENCHANT_MAX_STATS);
		setValue(getIcon(parser));
		setValue(parser.getTooltip());
	}

	private List<PveRole> getPveRoles(EnchantTooltipParser parser) {
		if (!parser.getPveRoles().isEmpty()) {
			return parser.getPveRoles();
		}
		return PveRoleStatClassifier.classify(parser.getParsedStats());
	}

	private String getIcon(EnchantTooltipParser parser) {
		if ("inv_misc_note_01".equals(parser.getIcon())) {
			return "spell_holy_greaterheal";
		}
		return parser.getIcon();
	}
}
