package wow.commons.repository.impl.parsers.items;

import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;
import wow.commons.model.item.BasicItemInfo;
import wow.commons.model.item.Gem;
import wow.commons.model.item.GemColor;
import wow.commons.model.item.MetaEnabler;
import wow.commons.model.professions.Profession;
import wow.commons.model.pve.Phase;
import wow.commons.model.sources.Source;
import wow.commons.repository.PVERepository;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;
import wow.commons.repository.impl.parsers.gems.GemStatsParser;
import wow.commons.util.SourceParser;

import java.util.Set;

import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class GemSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colId = column(GEM_ID);
	private final ExcelColumn colName = column(GEM_NAME);
	private final ExcelColumn colRarity = column(GEM_RARITY);
	private final ExcelColumn colItemLevel = column(GEM_ITEM_LEVEL);
	private final ExcelColumn colPhase = column(GEM_PHASE);
	private final ExcelColumn colSource = column(GEM_SOURCE);
	private final ExcelColumn colColor = column(GEM_COLOR);
	private final ExcelColumn colBinding = column(GEM_BINDING);
	private final ExcelColumn colUnique = column(GEM_UNIQUE);
	private final ExcelColumn colReqProfession = column(GEM_REQ_PROFESSION);
	private final ExcelColumn colReqProfessionLevel = column(GEM_REQ_PROFESSION_LEVEL);
	private final ExcelColumn colStats = column(GEM_STATS);
	private final ExcelColumn colMetaEnablers = column(GEM_META_ENABLERS);
	private final ExcelColumn colIcon = column(GEM_ICON);
	private final ExcelColumn colTooltip = column(GEM_TOOLTIP);

	private final PVERepository pveRepository;
	private final ItemDataRepositoryImpl itemDataRepository;

	public GemSheetParser(String sheetName, PVERepository pveRepository, ItemDataRepositoryImpl itemDataRepository) {
		super(sheetName);
		this.pveRepository = pveRepository;
		this.itemDataRepository = itemDataRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colName;
	}

	@Override
	protected void readSingleRow() {
		Gem gem = getGem();
		itemDataRepository.addGem(gem);
	}

	private Gem getGem() {
		var id = colId.getInteger();
		var name = colName.getString();
		var rarity = colRarity.getEnum(ItemRarity::valueOf);
		var itemLevel = colItemLevel.getInteger();
		var phase = colPhase.getEnum(Phase::valueOf);
		var source = colSource.getString();
		var color = colColor.getEnum(GemColor::valueOf);
		var binding = colBinding.getEnum(Binding::valueOf, Binding.BINDS_ON_EQUIP);
		var unique = colUnique.getBoolean();
		var requiredProfession = colReqProfession.getEnum(Profession::valueOf, null);
		var requiredProfessionLevel = colReqProfessionLevel.getInteger(0);
		var metaEnablers = colMetaEnablers.getList(MetaEnabler::valueOf);
		var icon = colIcon.getString();
		var tooltip = colTooltip.getString();
		var stats = GemStatsParser.tryParseStats(colStats.getString());

		Description description = new Description(name, icon, tooltip);
		Restriction restriction = Restriction.builder()
				.phase(phase)
				.requiredProfession(requiredProfession)
				.requiredProfessionLevel(requiredProfessionLevel)
				.build();
		Set<Source> sources = new SourceParser(pveRepository, itemDataRepository).parse(source);
		BasicItemInfo basicItemInfo = new BasicItemInfo(rarity, binding, unique, itemLevel, sources);

		return new Gem(id, description, restriction, stats, basicItemInfo, color, metaEnablers);
	}
}
