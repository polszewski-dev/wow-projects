package wow.character.repository.impl.parser.character;

import wow.character.model.build.RotationTemplate;
import wow.character.model.character.CharacterProfession;
import wow.character.model.character.CharacterTemplate;
import wow.character.repository.impl.CharacterTemplateRepositoryImpl;
import wow.character.util.TalentLinkParser;
import wow.commons.model.buff.BuffId;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.ExclusiveFaction;
import wow.commons.model.character.PetType;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.pve.Phase;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.commons.repository.pve.PhaseRepository;
import wow.commons.repository.spell.TalentRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public class CharacterTemplateSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colReqLevel = column("req_level");
	private final ExcelColumn colReqClass = column("req_class");
	private final ExcelColumn colTalentLink = column("talent_link");
	private final ExcelColumn colRole = column("role");
	private final ExcelColumn colDefaultRotation = column("default_rotation");
	private final ExcelColumn colActivePet = column("active_pet");
	private final ExcelColumn colDefaultBuffs = column("default_buffs");
	private final ExcelColumn colDefaultDebuffs = column("default_debuffs");
	private final ExcelColumn colDefaultConsumables = column("default_consumables");
	private final ExcelColumn colProf1 = column("prof1");
	private final ExcelColumn colProf1Spec = column("prof1_spec");
	private final ExcelColumn colProf2 = column("prof2");
	private final ExcelColumn colProf2Spec = column("prof2_spec");
	private final ExcelColumn colXFactions = column("xfactions");
	private final ExcelColumn colDefault = column("default");

	private final CharacterTemplateRepositoryImpl characterTemplateRepository;
	private final TalentRepository talentRepository;
	private final PhaseRepository phaseRepository;

	public CharacterTemplateSheetParser(String sheetName, CharacterTemplateRepositoryImpl characterTemplateRepository, TalentRepository talentRepository, PhaseRepository phaseRepository) {
		super(sheetName);
		this.characterTemplateRepository = characterTemplateRepository;
		this.talentRepository = talentRepository;
		this.phaseRepository = phaseRepository;
	}

	@Override
	protected void readSingleRow() {
		CharacterTemplate characterTemplate = getCharacterTemplate();
		characterTemplateRepository.addCharacterTemplate(characterTemplate);
	}

	private CharacterTemplate getCharacterTemplate() {
		var name = colName.getString();
		var characterClass = colReqClass.getEnum(CharacterClassId::parse);
		var level = colReqLevel.getInteger();
		var timeRestriction = getTimeRestriction();
		var talentLink = colTalentLink.getEnum(x -> TalentLinkParser.parse(x, talentRepository));
		var pveRole = colRole.getEnum(PveRole::parse);
		var defaultRotation = RotationTemplate.parse(colDefaultRotation.getString());
		var activePet = colActivePet.getEnum(PetType::parse, null);
		var defaultBuffs = colDefaultBuffs.getList(BuffId::parse);
		var defaultDebuffs = colDefaultDebuffs.getList(BuffId::parse);
		var consumables = colDefaultConsumables.getList(x -> x);
		var professions = getProfessions(timeRestriction);
		var exclusiveFactions = colXFactions.getList(ExclusiveFaction::parse);
		var isDefault = colDefault.getBoolean();

		return new CharacterTemplate(
				name,
				characterClass,
				level,
				timeRestriction,
				talentLink,
				pveRole,
				defaultRotation,
				activePet,
				defaultBuffs,
				defaultDebuffs,
				consumables,
				professions,
				exclusiveFactions,
				isDefault
		);
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

		var phase = getPhase(timeRestriction);

		return CharacterProfession.getCharacterProfession(phase, prof, spec, 1);
	}

	private Phase getPhase(TimeRestriction timeRestriction) {
		var phaseId = timeRestriction.earliestPhaseId();

		return phaseRepository.getPhase(phaseId).orElseThrow();
	}
}
