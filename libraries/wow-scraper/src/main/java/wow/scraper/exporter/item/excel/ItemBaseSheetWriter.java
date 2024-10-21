package wow.scraper.exporter.item.excel;

import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.effect.Effect;
import wow.commons.model.pve.Side;
import wow.commons.model.spell.ActivatedAbility;
import wow.scraper.classifier.PveRoleStatClassifier;
import wow.scraper.exporter.excel.ExcelSheetWriter;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.model.WowheadItemQuality;
import wow.scraper.parser.WowheadSourceParser;
import wow.scraper.parser.tooltip.AbstractItemTooltipParser;
import wow.scraper.parser.tooltip.AbstractTooltipParser;

import java.util.List;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.ID;
import static wow.commons.repository.impl.parser.excel.CommonColumnNames.NAME;
import static wow.commons.repository.impl.parser.item.ItemBaseExcelColumnNames.*;
import static wow.scraper.util.CommonAssertions.assertBothAreEqual;

/**
 * User: POlszewski
 * Date: 2023-05-18
 */
public abstract class ItemBaseSheetWriter<T> extends ExcelSheetWriter<T, WowExcelBuilder> {
	protected ItemBaseSheetWriter(WowExcelBuilder builder) {
		super(builder);
	}

	protected void writeCommonHeader() {
		setHeader(ID);
		setHeader(NAME, 30);
		setHeader(ITEM_TYPE);
		setHeader(ITEM_SUBTYPE);
		setHeader(RARITY);
		setHeader(BINDING);
		setHeader(UNIQUE);
		setHeader(ITEM_LEVEL);
		setHeader(SOURCE, 20);
		writeTimeRestrictionHeader();
	}

	protected void writeCommonColumns(AbstractItemTooltipParser parser) {
		setValue(parser.getItemId());
		setValue(parser.getName());
		setValue(parser.getItemType());
		setValue(parser.getItemSubType() != null ? parser.getItemSubType().toString() : null);
		setValue(getItemRarity(parser));
		setValue(parser.getBinding());
		setValue(parser.isUnique());
		setValue(parser.getItemLevel());
		setValue(parseSource(parser));
		writeTimeRestriction(parser.getTimeRestriction());
	}

	protected void writeIconAndTooltip(AbstractTooltipParser<?> parser) {
		writeIconAndTooltip(parser.getIcon(), "");
	}

	protected void writePveRoles(List<Effect> effects, ActivatedAbility activatedAbility, int itemId) {
		var pveRoles = getPveRoles(effects, activatedAbility, itemId);
		setValue(pveRoles);
	}

	private List<PveRole> getPveRoles(List<Effect> effects, ActivatedAbility activatedAbility, int itemId) {
		var override = datafixes.getPveRoleOverrides().get(itemId);
		if (override != null) {
			return List.of(override);
		}
		return PveRoleStatClassifier.classify(effects, activatedAbility);
	}

	protected ItemRarity getItemRarity(AbstractTooltipParser<?> parser) {
		Integer quality = parser.getDetails().getQuality();
		return WowheadItemQuality.fromCode(quality).getItemRarity();
	}

	protected Side getRequiredSide(AbstractItemTooltipParser parser) {
		Side side = datafixes.getItemRequiredSideOverride(parser.getItemId())
				.orElse(parser.getRequiredSide());

		if (parser.getRequiredFactionName() == null) {
			return side;
		}

		Side factionSide = datafixes.getRequiredSideFromFaction(parser.getRequiredFactionName());

		if (side != null && factionSide != null) {
			assertBothAreEqual("side", side, factionSide);
			return side;
		}

		if (factionSide == null) {
			return side;
		}

		return factionSide;
	}

	protected String parseSource(AbstractItemTooltipParser parser) {
		WowheadSourceParser sourceParser = new WowheadSourceParser(parser.getDetails(), parser.getRequiredFactionName(), parser.getGameVersion());
		List<String> sources = sourceParser.getSource();
		return String.join("#", sources);
	}
}
