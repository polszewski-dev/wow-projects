package wow.commons.repository.impl.parser.item;

import wow.commons.model.item.Consumable;
import wow.commons.model.item.ConsumableId;
import wow.commons.model.item.impl.ConsumableImpl;
import wow.commons.repository.spell.SpellRepository;

import static wow.commons.model.effect.EffectSource.ItemSource;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class ConsumableSheetParser extends AbstractItemSheetParser {
	private final ConsumableExcelParser parser;

	public ConsumableSheetParser(String sheetName, SourceParserFactory sourceParserFactory, SpellRepository spellRepository, ConsumableExcelParser parser) {
		super(sheetName, sourceParserFactory, spellRepository);
		this.parser = parser;
	}

	@Override
	protected void readSingleRow() {
		var consumable = getConsumable();
		parser.addConsumable(consumable);
	}

	private Consumable getConsumable() {
		var id = ConsumableId.of(getId());

		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();
		var basicItemInfo = getBasicItemInfo();

		var consumable = new ConsumableImpl(id, description, timeRestriction, characterRestriction, basicItemInfo);

		var activatedAbility = getActivatedAbility(new ItemSource(consumable));

		if (activatedAbility == null) {
			throw new IllegalArgumentException();
		}

		consumable.setActivatedAbility(activatedAbility);

		return consumable;
	}
}
