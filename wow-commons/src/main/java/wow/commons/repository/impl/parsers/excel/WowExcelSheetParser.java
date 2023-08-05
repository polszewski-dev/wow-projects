package wow.commons.repository.impl.parsers.excel;

import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.ExclusiveFaction;
import wow.commons.model.character.PetType;
import wow.commons.model.character.RaceId;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.ProfessionRestriction;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.professions.ProfessionSpecializationId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Side;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.TalentId;
import wow.commons.repository.impl.parsers.excel.mapper.ComplexAttributeMapper;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.PrimitiveAttributeSupplier;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parsers.excel.CommonColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-24
 */
public abstract class WowExcelSheetParser extends ExcelSheetParser {
	protected WowExcelSheetParser(String sheetName) {
		super(sheetName);
	}

	protected WowExcelSheetParser(Pattern sheetNamePattern) {
		super(sheetNamePattern);
	}

	protected class ExcelColumn extends ExcelSheetParser.ExcelColumn {
		public ExcelColumn(String name, boolean optional) {
			super(name, optional);
		}

		public Percent getPercent(Percent defaultValue) {
			return getOptionalPercent().orElse(defaultValue);
		}

		public Percent getPercent() {
			return getOptionalPercent().orElseThrow(this::columnIsEmpty);
		}

		public Duration getDuration(Duration defaultValue) {
			return getOptionalDuration().orElse(defaultValue);
		}

		public Duration getDuration() {
			return getOptionalDuration().orElseThrow(this::columnIsEmpty);
		}

		private Optional<Percent> getOptionalPercent() {
			return getOptionalString().map(Percent::parse);
		}

		private Optional<Duration> getOptionalDuration() {
			return getOptionalString().map(Duration::parse);
		}

		public <K, V> Map<K, V> getMap(K[] keys, Function<ExcelColumn, V> mapper) {
			return Stream.of(keys)
					.collect(Collectors.toMap(
							Function.identity(),
							key -> mapper.apply(subColumn(key.toString().toLowerCase()))
					));
		}

		private ExcelColumn subColumn(String subName) {
			return new ExcelColumn(getName() + subName, isOptional());
		}
	}

	@Override
	protected ExcelColumn column(String name, boolean optional) {
		return new ExcelColumn(name, optional);
	}

	@Override
	protected ExcelColumn column(String name) {
		return column(name, false);
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colName;
	}

	private final ExcelColumn colName = column(NAME);
	private final ExcelColumn colIcon = column(ICON, true);
	private final ExcelColumn colTooltip = column(TOOLTIP, true);

	protected Description getDescription() {
		var name = colName.getString();
		var icon = colIcon.getString(null);
		var tooltip = colTooltip.getString(null);

		return new Description(name, icon, tooltip);
	}

	protected Description getDescription(String name) {
		var icon = colIcon.getString(null);
		var tooltip = colTooltip.getString(null);

		return new Description(name, icon, tooltip);
	}

	private final ExcelColumn colReqVersion = column(REQ_VERSION, true);
	private final ExcelColumn colReqPhase = column(REQ_PHASE, true);

	protected TimeRestriction getTimeRestriction() {
		var versions = colReqVersion.getList(GameVersionId::parse);
		var phase = colReqPhase.getEnum(PhaseId::parse, null);

		return new TimeRestriction(versions, phase);
	}

	private final ExcelColumn colReqLevel = column(REQ_LEVEL, true);
	private final ExcelColumn colReqClass = column(REQ_CLASS, true);
	private final ExcelColumn colReqRace = column(REQ_RACE, true);
	private final ExcelColumn colReqSide = column(REQ_SIDE, true);
	private final ExcelColumn colReqProfession = column(REQ_PROFESSION, true);
	private final ExcelColumn colReqProfessionLevel = column(REQ_PROFESSION_LEVEL, true);
	private final ExcelColumn colReqProfessionSpec = column(REQ_PROFESSION_SPEC, true);
	private final ExcelColumn colExclusiveFaction = column(REQ_XFACTION, true);
	private final ExcelColumn colReqPet = column(REQ_PET, true);
	private final ExcelColumn colReqSpell = column(REQ_SPELL, true);
	private final ExcelColumn colReqTalent = column(REQ_TALENT, true);
	private final ExcelColumn colReqRole = column(REQ_ROLE, true);
	private final ExcelColumn colReqMaxLevel = column(REQ_MAX_LEVEL, true);

	protected CharacterRestriction getRestriction() {
		var level = colReqLevel.getNullableInteger();
		var characterClasses = colReqClass.getList(CharacterClassId::parse);
		var races = colReqRace.getList(RaceId::parse);
		var side = colReqSide.getEnum(Side::parse, null);
		var profession = colReqProfession.getEnum(ProfessionId::parse, null);
		var professionLevel = colReqProfessionLevel.getNullableInteger();
		var professionSpec = colReqProfessionSpec.getEnum(ProfessionSpecializationId::valueOf, null);
		var exclusiveFaction = colExclusiveFaction.getEnum(ExclusiveFaction::parse, null);
		var activePet = colReqPet.getEnum(PetType::parse, null);
		var spellId = colReqSpell.getEnum(SpellId::parse, null);
		var talentId = colReqTalent.getEnum(TalentId::parse, null);
		var role = colReqRole.getEnum(PveRole::parse, null);
		var maxLevel = colReqMaxLevel.getNullableInteger();

		return new CharacterRestriction(
				level,
				characterClasses,
				races,
				side,
				ProfessionRestriction.of(profession, professionLevel),
				professionSpec,
				exclusiveFaction,
				activePet,
				spellId,
				talentId,
				role,
				maxLevel
		);
	}

	private static final int MAX_ATTRIBUTES = 20;

	protected Attributes readAttributes() {
		return readAttributes("");
	}

	protected Attributes readAttributes(String prefix) {
		AttributesBuilder builder = new AttributesBuilder();

		for (int statNo = 1; statNo <= MAX_ATTRIBUTES; ++statNo) {
			readAttribute(builder, prefix, statNo);
		}

		assertNoMoreAttributeColumns(prefix);

		return builder.toAttributes();
	}

	private void readAttribute(AttributesBuilder builder, String prefix, int statNo) {
		ExcelColumn colStat = getColStat(prefix, statNo);
		ExcelColumn colAmount = getColAmount(prefix, statNo);

		var attributeStr = colStat.getString(null);

		if (attributeStr == null) {
			return;
		}

		if (colAmount.getString(null) != null) {
			var attributeSupplier = PrimitiveAttributeSupplier.fromString(attributeStr);
			attributeSupplier.addAttributeList(builder, colAmount.getDouble());
		} else {
			attributeStr = substitutePlaceholders(attributeStr);
			var attribute = ComplexAttributeMapper.fromString(attributeStr);
			builder.addAttribute(attribute);
		}
	}

	private ExcelColumn getColStat(String prefix, int statNo) {
		return column(colStat(prefix, statNo), true);
	}

	private ExcelColumn getColAmount(String prefix, int statNo) {
		return column(colAmount(prefix, statNo), true);
	}

	private void assertNoMoreAttributeColumns(String prefix) {
		if (getColStat(prefix, MAX_ATTRIBUTES + 1).getString(null) != null || getColAmount(prefix, MAX_ATTRIBUTES + 1).getString(null) != null) {
			throw new IllegalArgumentException("There are more attribute columns than " + MAX_ATTRIBUTES);
		}
	}

	private String substitutePlaceholders(String attributeStr) {
		String tooltip = colTooltip.getString("no tooltip");
		return attributeStr.replace("${tooltip}", tooltip);
	}

	private final ExcelColumn coPveRoles = column("pve_roles");

	protected Set<PveRole> getPveRoles() {
		return coPveRoles.getSet(PveRole::parse);
	}
}
