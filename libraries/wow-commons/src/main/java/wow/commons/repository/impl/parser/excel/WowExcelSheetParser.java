package wow.commons.repository.impl.parser.excel;

import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.ExclusiveFaction;
import wow.commons.model.character.PetType;
import wow.commons.model.character.RaceId;
import wow.commons.model.config.*;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Side;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.talent.TalentId;
import wow.commons.util.AttributesParser;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.*;

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

		public AnyDuration getAnyDuration(AnyDuration defaultValue) {
			return getOptionalAnyDuration().orElse(defaultValue);
		}

		public AnyDuration getAnyDuration() {
			return getOptionalAnyDuration().orElseThrow(this::columnIsEmpty);
		}

		private Optional<Percent> getOptionalPercent() {
			return getOptionalString().map(Percent::parse);
		}

		private Optional<Duration> getOptionalDuration() {
			return getOptionalString().map(Duration::parse);
		}

		private Optional<AnyDuration> getOptionalAnyDuration() {
			return getOptionalString().map(AnyDuration::parse);
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

		@Override
		public ExcelColumn prefixed(String prefix) {
			if (prefix == null || prefix.isEmpty()) {
				return this;
			}
			return new ExcelColumn(prefix + getName(), isOptional());
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

	protected final ExcelColumn colId = column(ID);
	protected final ExcelColumn colName = column(NAME);

	private final ExcelColumn colIcon = column(ICON, true);
	private final ExcelColumn colTooltip = column(TOOLTIP, true);

	protected Description getDescription() {
		var name = colName.getString();
		var icon = colIcon.getString(null);
		var tooltip = colTooltip.getString(null);

		return new Description(name, icon, tooltip);
	}

	private final ExcelColumn colReqVersion = column(REQ_VERSION, true);
	private final ExcelColumn colReqPhase = column(REQ_PHASE, true);

	protected TimeRestriction getTimeRestriction() {
		var version = colReqVersion.getEnum(GameVersionId::parse, null);
		var phase = colReqPhase.getEnum(PhaseId::parse, null);

		if (version == null && phase == null) {
			return null;
		}

		return TimeRestriction.of(version, phase);
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
	private final ExcelColumn colReqTalentRank = column(REQ_TALENT_RANK, true);
	private final ExcelColumn colReqRole = column(REQ_ROLE, true);
	private final ExcelColumn colReqMaxLevel = column(REQ_MAX_LEVEL, true);

	protected CharacterRestriction getRestriction() {
		var level = colReqLevel.getNullableInteger();
		var characterClasses = colReqClass.getList(CharacterClassId::parse);
		var races = colReqRace.getList(RaceId::parse);
		var side = colReqSide.getEnum(Side::parse, null);
		var profession = colReqProfession.getEnum(ProfessionId::parse, null);
		var professionLevel = colReqProfessionLevel.getNullableInteger();
		var professionSpec = colReqProfessionSpec.getEnum(ProfessionSpecializationId::parse, null);
		var exclusiveFaction = colExclusiveFaction.getEnum(ExclusiveFaction::parse, null);
		var activePet = colReqPet.getList(PetType::parse);
		var spellId = colReqSpell.getEnum(AbilityId::parse, null);
		var talentId = colReqTalent.getEnum(TalentId::parse, null);
		var talentRank = colReqTalentRank.getNullableInteger();
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
				TalentRestriction.of(talentId, talentRank),
				role,
				maxLevel
		);
	}

	protected Attributes readAttributes(String prefix, int maxAttributes) {
		var list = IntStream.rangeClosed(1, maxAttributes)
				.mapToObj(idx -> readAttribute(prefix, idx))
				.filter(Objects::nonNull)
				.toList();

		return Attributes.of(list);
	}

	private Attribute readAttribute(String prefix, int statNo) {
		var colAttrId = column(getAttrId(statNo), true).prefixed(prefix);

		if (colAttrId.isEmpty()) {
			return null;
		}

		var colAttrValue = column(getAttrValue(statNo)).prefixed(prefix);

		var id = colAttrId.getString();
		var value = colAttrValue.getDouble();

		return AttributesParser.parse(id, value);
	}

	private final ExcelColumn coPveRoles = column("pve_roles");

	protected Set<PveRole> getPveRoles() {
		return coPveRoles.getSet(PveRole::parse);
	}
}
