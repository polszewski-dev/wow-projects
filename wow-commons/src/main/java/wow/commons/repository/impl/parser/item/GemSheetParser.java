package wow.commons.repository.impl.parser.item;

import wow.commons.model.item.Gem;
import wow.commons.model.item.GemColor;
import wow.commons.model.item.ItemSource;
import wow.commons.model.item.MetaEnabler;
import wow.commons.model.item.impl.GemImpl;
import wow.commons.repository.SpellRepository;
import wow.commons.repository.impl.ItemRepositoryImpl;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class GemSheetParser extends AbstractItemSheetParser {
	private final ExcelColumn colColor = column(GEM_COLOR);
	private final ExcelColumn colMetaEnablers = column(GEM_META_ENABLERS);

	public GemSheetParser(String sheetName, SourceParserFactory sourceParserFactory, SpellRepository spellRepository, ItemRepositoryImpl itemRepository) {
		super(sheetName, sourceParserFactory, spellRepository, itemRepository);
	}

	@Override
	protected void readSingleRow() {
		Gem gem = getGem();
		itemRepository.addGem(gem);
	}

	private Gem getGem() {
		var id = getId();
		var color = colColor.getEnum(GemColor::valueOf);
		var metaEnablers = colMetaEnablers.getList(MetaEnabler::parse);

		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();
		var basicItemInfo = getBasicItemInfo();
		var pveRoles = getPveRoles();

		var gem = new GemImpl(id, description, timeRestriction, characterRestriction, basicItemInfo, color, metaEnablers, pveRoles);
		var effects = readItemEffects(GEM_EFFECT_PREFIX, GEM_MAX_EFFECTS, timeRestriction, new ItemSource(gem));

		gem.setEffects(effects);
		return gem;
	}
}
