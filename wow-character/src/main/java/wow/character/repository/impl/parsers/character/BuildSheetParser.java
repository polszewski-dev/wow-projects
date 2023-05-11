package wow.character.repository.impl.parsers.character;

import wow.character.model.build.BuffSetId;
import wow.character.model.build.BuildId;
import wow.character.model.build.BuildTemplate;
import wow.character.model.character.CharacterProfession;
import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.PetType;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.professions.ProfessionSpecializationId;
import wow.commons.model.spells.SpellId;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public class BuildSheetParser extends CharacterSheetParser {
	private final ExcelColumn colBuild = column("build");
	private final ExcelColumn colReqLevel = column("req_level");
	private final ExcelColumn colReqClass = column("req_class");
	private final ExcelColumn colTalentLink = column("talent_link");
	private final ExcelColumn colRole = column("role");
	private final ExcelColumn colDefaultRotation = column("default_rotation");
	private final ExcelColumn colActivePet = column("active_pet");
	private final ExcelColumn colProf1 = column("prof1");
	private final ExcelColumn colProf1Spec = column("prof1_spec");
	private final ExcelColumn colProf2 = column("prof2");
	private final ExcelColumn colProf2Spec = column("prof2_spec");

	public BuildSheetParser(String sheetName, CharacterRepositoryImpl characterRepository) {
		super(sheetName, characterRepository);
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
		var characterClass = colReqClass.getEnum(CharacterClassId::parse);
		var level = colReqLevel.getInteger();
		var timeRestriction = getTimeRestriction();
		var talentLink = colTalentLink.getString();
		var pveRole = colRole.getEnum(PveRole::parse);
		var defaultRotation = colDefaultRotation.getList(SpellId::parse);
		var activePet = colActivePet.getEnum(PetType::parse, null);
		var buffSets = getBuffSets();
		var professions = getProfessions(timeRestriction);

		return new BuildTemplate(
				buildId,
				characterClass,
				level,
				timeRestriction,
				talentLink,
				pveRole,
				defaultRotation,
				activePet,
				buffSets,
				professions
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

	private List<CharacterProfession> getProfessions(TimeRestriction timeRestriction) {
		return Stream.of(
				getProfession(colProf1, colProf1Spec, timeRestriction),
				getProfession(colProf2, colProf2Spec, timeRestriction)
		)
				.filter(Objects::nonNull)
				.toList();
	}

	private CharacterProfession getProfession(ExcelColumn colProf, ExcelColumn colProfSpec, TimeRestriction timeRestriction) {
		var prof = colProf.getEnum(ProfessionId::parse, null);
		var spec = colProfSpec.getEnum(ProfessionSpecializationId::parse, null);

		if (prof == null) {
			return null;
		}

		var gameVersion = characterRepository.getGameVersion(timeRestriction.getUniqueVersion()).orElseThrow();
		var profession = gameVersion.getProfession(prof);

		return new CharacterProfession(profession, profession.getSpecialization(spec));
	}
}
