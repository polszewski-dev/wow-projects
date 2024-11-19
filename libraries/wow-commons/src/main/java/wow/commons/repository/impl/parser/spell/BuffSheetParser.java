package wow.commons.repository.impl.parser.spell;

import wow.commons.model.buff.*;
import wow.commons.model.buff.impl.BuffImpl;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.repository.impl.spell.BuffRepositoryImpl;
import wow.commons.repository.spell.SpellRepository;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class BuffSheetParser extends AbstractSpellSheetParser {
	private final ExcelColumn colRank = column("rank");
	private final ExcelColumn colType = column("type");
	private final ExcelColumn colExclusionGroup = column("exclusion group");
	private final ExcelColumn colCategories = column("categories");
	private final ExcelColumn colStacks = column("stacks");

	private final BuffRepositoryImpl buffRepository;
	private final SpellRepository spellRepository;

	public BuffSheetParser(String sheetName, BuffRepositoryImpl buffRepository, SpellRepository spellRepository) {
		super(sheetName);
		this.buffRepository = buffRepository;
		this.spellRepository = spellRepository;
	}

	@Override
	protected void readSingleRow() {
		Buff buff = getBuff();
		buffRepository.addBuff(buff);
	}

	private Buff getBuff() {
		var effectId = colId.getInteger();
		var buffId = colName.getEnum(BuffId::parse);
		var rank = colRank.getInteger(0);
		var type = colType.getEnum(BuffType::parse);
		var exclusionGroup = colExclusionGroup.getEnum(BuffExclusionGroup::parse, null);
		var pveRoles = getPveRoles();
		var categories = colCategories.getSet(BuffCategory::parse);

		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();

		var effect = spellRepository.getEffect(effectId, timeRestriction.getGameVersionId().getLastPhase()).orElseThrow();
		var stacks = colStacks.getInteger(1);

		if (effect.getSource() == null) {
			((EffectImpl) effect).setSource(new BuffSource(effect.getDescription()));
		}

		return new BuffImpl(
				new BuffIdAndRank(buffId, rank), timeRestriction, characterRestriction, type, exclusionGroup, pveRoles, categories, effect, stacks
		);
	}
}
