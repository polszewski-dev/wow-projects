package wow.scraper.exporters.item.excel;

import wow.commons.model.character.CharacterClassId;
import wow.scraper.parsers.tooltip.ItemTooltipParser;

import java.util.List;

import static wow.commons.repository.impl.parsers.excel.CommonColumnNames.*;
import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-05-18
 */
public class ItemSheetWriter extends WowExcelSheetWriter<ItemTooltipParser> {
	public ItemSheetWriter(ItemBaseExcelBuilder builder) {
		super(builder);
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
		writeAttributeHeader(SOCKET_BONUS_PREFIX, SOCKET_BONUS_MAX_STATS);
		setHeader(ITEM_ITEM_SET, 30);
		writeAttributeHeader("", ITEM_MAX_STATS);
		writeIconAndTooltipHeader();
		writer.nextRow().freeze(2, 1);
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
		writePveRoles(parser.getStatParser().getParsedStats());
		setValue(parser.getSocketTypes());
		writeAttributes(parser.getSocketBonus(), SOCKET_BONUS_MAX_STATS);
		setValue(parser.getItemSetName());
		writeAttributes(parser.getStatParser().getParsedStats(), ITEM_MAX_STATS);
		writeIconAndTooltip(parser);
		writer.nextRow();
	}

	private List<CharacterClassId> getRequiredClass(ItemTooltipParser parser) {
		if (parser.getItemSetName() != null) {
			return builder.getSavedSets().get(parser).getItemSetRequiredClass();
		}
		return parser.getRequiredClass();
	}
}
