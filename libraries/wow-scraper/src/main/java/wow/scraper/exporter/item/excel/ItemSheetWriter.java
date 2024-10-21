package wow.scraper.exporter.item.excel;

import wow.commons.model.character.CharacterClassId;
import wow.commons.model.spell.Spell;
import wow.scraper.parser.tooltip.ItemTooltipParser;

import java.util.List;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.*;
import static wow.commons.repository.impl.parser.item.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-05-18
 */
public class ItemSheetWriter extends ItemBaseSheetWriter<ItemTooltipParser> {
	private final ItemExcelBuilder builder;

	public ItemSheetWriter(ItemExcelBuilder builder) {
		super(builder);
		this.builder = builder;
	}

	@Override
	public void writeHeader() {
		writeCommonHeader();
		setHeader(REQ_LEVEL);
		setHeader(REQ_CLASS);
		setHeader(REQ_RACE);
		setHeader(REQ_SIDE);
		setHeader(REQ_PROFESSION);
		setHeader(REQ_PROFESSION_LEVEL);
		setHeader(REQ_PROFESSION_SPEC);
		setHeader(REQ_XFACTION);
		setHeader(PVE_ROLES);
		setHeader(ITEM_SOCKET_TYPES);
		writeEffectHeader(SOCKET_BONUS_PREFIX, 1);
		setHeader(ITEM_ITEM_SET, 30);
		writeEffectHeader(ITEM_EFFECT_PREFIX, ITEM_MAX_EFFECTS);
		setHeader(ITEM_ACTIVATED_ABILITY);
		writeIconAndTooltipHeader();
	}

	@Override
	public void writeRow(ItemTooltipParser parser) {
		writeCommonColumns(parser);
		setValue(parser.getRequiredLevel());
		setValue(getRequiredClass(parser));
		setValue(parser.getRequiredRace());
		setValue(getRequiredSide(parser));
		setValue(parser.getRequiredProfession());
		setValue(parser.getRequiredProfessionLevel());
		setValue(parser.getRequiredProfessionSpec());
		setValue(parser.getExclusiveFaction());
		writePveRoles(parser.getEffects(), parser.getActivatedAbility().orElse(null), parser.getItemId());
		setValue(parser.getSocketTypes());
		writeEffect(parser.getSocketBonus().orElse(null));
		setValue(parser.getItemSetName());
		writeEffects(parser.getEffects(), ITEM_MAX_EFFECTS);
		setValue(parser.getActivatedAbility().map(Spell::getId).orElse(null));
		writeIconAndTooltip(parser);
	}

	private List<CharacterClassId> getRequiredClass(ItemTooltipParser parser) {
		if (parser.getItemSetName() != null) {
			return builder.getSavedSets().get(parser).getItemSetRequiredClass();
		}
		return parser.getRequiredClass();
	}
}
