package wow.character.repository.impl.parser.character;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.character.model.character.GearSet;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.TimeRestriction;
import wow.commons.repository.item.EnchantRepository;
import wow.commons.repository.item.GemRepository;
import wow.commons.repository.item.ItemRepository;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class GearSetExcelParser extends ExcelParser {
	@Value("${gear.sets.xls.file.path}")
	private final String xlsFilePath;

	private final ItemRepository itemRepository;
	private final EnchantRepository enchantRepository;
	private final GemRepository gemRepository;

	private record Key(String name, TimeRestriction timeRestriction, CharacterRestriction characterRestriction) {}

	private final Map<Key, List<GearSet>> unmergedGearSets = new HashMap<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new GearSetSheetParser("gear_sets", itemRepository, enchantRepository, gemRepository, this)
		);
	}

	void addGearSet(GearSet gearSet) {
		var key = new Key(gearSet.getName(), gearSet.getTimeRestriction(), gearSet.getCharacterRestriction());

		unmergedGearSets.computeIfAbsent(key, x -> new ArrayList<>()).add(gearSet);
	}

	public List<GearSet> getGearSets() {
		return unmergedGearSets.values().stream()
				.map(this::merge)
				.toList();
	}

	private GearSet merge(List<GearSet> gearSets) {
		var itemsBySlot = new EnumMap<ItemSlot, EquippableItem>(ItemSlot.class);

		for (var gearSet : gearSets) {
			itemsBySlot.putAll(gearSet.getItemsBySlot());
		}

		return new GearSet(
				gearSets.getFirst().getName(),
				gearSets.getFirst().getCharacterRestriction(),
				gearSets.getFirst().getTimeRestriction(),
				itemsBySlot
		);
	}
}
