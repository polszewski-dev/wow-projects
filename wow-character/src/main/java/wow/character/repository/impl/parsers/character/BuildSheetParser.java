package wow.character.repository.impl.parsers.character;

import wow.character.model.build.BuffSetId;
import wow.character.model.build.BuildId;
import wow.character.model.build.BuildTemplate;
import wow.character.model.build.PveRole;
import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.PetType;
import wow.commons.model.spells.SpellId;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public class BuildSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colBuild = column("build");
	private final ExcelColumn colReqLevel = column("req_level");
	private final ExcelColumn colReqClass = column("req_class");
	private final ExcelColumn colTalentLink = column("talent_link");
	private final ExcelColumn colRole = column("role");
	private final ExcelColumn colDamagingSpell = column("damaging_spell");
	private final ExcelColumn colRelevantSpells = column("relevant_spells");
	private final ExcelColumn colActivePet = column("active_pet");

	private final CharacterRepositoryImpl characterRepository;

	public BuildSheetParser(String sheetName, CharacterRepositoryImpl characterRepository) {
		super(sheetName);
		this.characterRepository = characterRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colBuild;
	}

	@Override
	protected void readSingleRow() {
		BuildTemplate buildTemplate = getBuildTemplate();
		characterRepository.addBuildTemplate(buildTemplate);
	}

	private BuildTemplate getBuildTemplate() {
		var buildId = colBuild.getEnum(BuildId::parse);
		var characterClass = colReqClass.getEnum(CharacterClass::parse);
		var level = colReqLevel.getInteger();
		var timeRestriction = getTimeRestriction();
		var talentLink = colTalentLink.getString();
		var pveRole = colRole.getEnum(PveRole::parse);
		var damagingSpell = colDamagingSpell.getEnum(SpellId::parse);
		var relevantSpells = colRelevantSpells.getList(SpellId::parse);
		var activePet = colActivePet.getEnum(PetType::parse, null);
		var buffSets = getBuffSets();

		return new BuildTemplate(
				buildId,
				characterClass,
				level,
				timeRestriction,
				talentLink,
				pveRole,
				damagingSpell,
				relevantSpells,
				activePet,
				buffSets
		);
	}

	private Map<BuffSetId, List<String>> getBuffSets() {
		Map<BuffSetId, List<String>> result = new EnumMap<>(BuffSetId.class);

		for (BuffSetId buffSetId : BuffSetId.values()) {
			String columnName = "buff_set:" + buffSetId.name().toLowerCase();
			List<String> buffNames = column(columnName, true).getList(x -> x);
			result.put(buffSetId, buffNames);
		}

		return result;
	}
}
