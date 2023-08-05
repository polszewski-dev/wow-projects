package wow.character.repository.impl.parsers.character;

import wow.character.model.build.RotationTemplate;
import wow.character.model.character.CharacterProfession;
import wow.character.model.character.CharacterTemplate;
import wow.character.model.character.CharacterTemplateId;
import wow.character.model.character.Phase;
import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.model.buffs.BuffId;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.ExclusiveFaction;
import wow.commons.model.character.PetType;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.professions.ProfessionSpecializationId;
import wow.commons.model.pve.PhaseId;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public class TemplateSheetParser extends CharacterSheetParser {
	private final ExcelColumn colTemplate = column("template");
	private final ExcelColumn colReqLevel = column("req_level");
	private final ExcelColumn colReqClass = column("req_class");
	private final ExcelColumn colTalentLink = column("talent_link");
	private final ExcelColumn colRole = column("role");
	private final ExcelColumn colDefaultRotation = column("default_rotation");
	private final ExcelColumn colActivePet = column("active_pet");
	private final ExcelColumn colDefaultBuffs = column("default_buffs");
	private final ExcelColumn colDefaultDebuffs = column("default_debuffs");
	private final ExcelColumn colProf1 = column("prof1");
	private final ExcelColumn colProf1Spec = column("prof1_spec");
	private final ExcelColumn colProf2 = column("prof2");
	private final ExcelColumn colProf2Spec = column("prof2_spec");
	private final ExcelColumn colXFactions = column("xfactions");

	public TemplateSheetParser(String sheetName, CharacterRepositoryImpl characterRepository) {
		super(sheetName, characterRepository);
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colTemplate;
	}

	@Override
	protected void readSingleRow() {
		CharacterTemplate characterTemplate = getCharacterTemplate();
		characterRepository.addCharacterTemplate(characterTemplate);
	}

	private CharacterTemplate getCharacterTemplate() {
		var templateId = colTemplate.getEnum(CharacterTemplateId::parse);
		var characterClass = colReqClass.getEnum(CharacterClassId::parse);
		var level = colReqLevel.getInteger();
		var timeRestriction = getTimeRestriction();
		var talentLink = colTalentLink.getString();
		var pveRole = colRole.getEnum(PveRole::parse);
		var defaultRotation = RotationTemplate.parse(colDefaultRotation.getString());
		var activePet = colActivePet.getEnum(PetType::parse, PetType.NONE);
		var defaultBuffs = colDefaultBuffs.getList(BuffId::parse);
		var defaultDebuffs = colDefaultDebuffs.getList(BuffId::parse);
		var professions = getProfessions(timeRestriction, level);
		var exclusiveFactions = colXFactions.getList(ExclusiveFaction::parse);

		return new CharacterTemplate(
				templateId,
				characterClass,
				level,
				timeRestriction,
				talentLink,
				pveRole,
				defaultRotation,
				activePet,
				defaultBuffs,
				defaultDebuffs,
				professions,
				exclusiveFactions
		);
	}

	private List<CharacterProfession> getProfessions(TimeRestriction timeRestriction, int characterLevel) {
		return Stream.of(
				getProfession(colProf1, colProf1Spec, timeRestriction, characterLevel),
				getProfession(colProf2, colProf2Spec, timeRestriction, characterLevel)
		)
				.filter(Objects::nonNull)
				.toList();
	}

	private CharacterProfession getProfession(ExcelColumn colProf, ExcelColumn colProfSpec, TimeRestriction timeRestriction, int characterLevel) {
		var prof = colProf.getEnum(ProfessionId::parse, null);
		var spec = colProfSpec.getEnum(ProfessionSpecializationId::parse, null);

		if (prof == null) {
			return null;
		}

		var phase = getPhase(timeRestriction);

		return phase.getCharacterProfessionMaxLevel(prof, spec, characterLevel);
	}

	private Phase getPhase(TimeRestriction timeRestriction) {
		PhaseId phaseId;

		if (timeRestriction.phaseId() != null) {
			phaseId = timeRestriction.phaseId();
		} else {
			phaseId = timeRestriction.getUniqueVersion().getLastPhase();
		}

		return characterRepository.getPhase(phaseId).orElseThrow();
	}
}
