package wow.commons.repository.impl.parser.item;

import wow.commons.model.item.Consumable;
import wow.commons.model.item.ItemSource;
import wow.commons.model.item.impl.ConsumableImpl;
import wow.commons.repository.impl.item.ConsumableRepositoryImpl;
import wow.commons.repository.spell.SpellRepository;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class ConsumableSheetParser extends AbstractItemSheetParser {
	private final ConsumableRepositoryImpl consumableRepository;

	public ConsumableSheetParser(String sheetName, SourceParserFactory sourceParserFactory, SpellRepository spellRepository, ConsumableRepositoryImpl consumableRepository) {
		super(sheetName, sourceParserFactory, spellRepository);
		this.consumableRepository = consumableRepository;
	}

	@Override
	protected void readSingleRow() {
		var consumable = getConsumable();
		consumableRepository.addConsumable(consumable);
	}

	private Consumable getConsumable() {
		var id = getId();

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