package wow.commons.repository.impl.parsers.excel;

import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.talents.TalentId;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.Race;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.PrimitiveAttributeSupplier;

import java.util.Optional;

import static wow.commons.repository.impl.parsers.excel.CommonColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-24
 */
public abstract class WowExcelSheetParser extends ExcelSheetParser {
	protected WowExcelSheetParser(String sheetName) {
		super(sheetName);
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
			return getOptionalString()
					.map(value -> Percent.of(Double.parseDouble(value)));
		}

		private Optional<Duration> getOptionalDuration() {
			return getOptionalString()
					.map(seconds -> Duration.seconds(Double.parseDouble(seconds)));
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

	private final ExcelColumn colPhase = column(PHASE, true);
	private final ExcelColumn colReqLevel = column(REQ_LEVEL, true);
	private final ExcelColumn colReqClass = column(REQ_CLASS, true);
	private final ExcelColumn colReqRace = column(REQ_RACE, true);
	private final ExcelColumn colReqSide = column(REQ_SIDE, true);
	private final ExcelColumn colReqProfession = column(REQ_PROFESSION, true);
	private final ExcelColumn colReqProfessionLevel = column(REQ_PROFESSION_LEVEL, true);
	private final ExcelColumn colReqProfessionSpec = column(REQ_PROFESSION_SPEC, true);
	private final ExcelColumn colReqTalent = column(REQ_TALENT, true);

	protected Restriction getRestriction() {
		var phase = colPhase.getEnum(Phase::parse, null);
		var requiredLevel = colReqLevel.getInteger(0);
		var requiredClass = colReqClass.getList(CharacterClass::parse);
		var requiredRace = colReqRace.getList(Race::parse);
		var requiredSide = colReqSide.getEnum(Side::parse, null);
		var requiredProfession = colReqProfession.getEnum(Profession::parse, null);
		var requiredProfessionLevel = colReqProfessionLevel.getInteger(0);
		var requiredProfessionSpec = colReqProfessionSpec.getEnum(ProfessionSpecialization::valueOf, null);
		var requiredTalent = colReqTalent.getEnum(TalentId::parse, null);

		return Restriction.builder()
				.phase(phase)
				.requiredLevel(requiredLevel)
				.requiredClass(requiredClass)
				.requiredRace(requiredRace)
				.requiredSide(requiredSide)
				.requiredProfession(requiredProfession)
				.requiredProfessionLevel(requiredProfessionLevel)
				.requiredProfessionSpec(requiredProfessionSpec)
				.requiredTalent(requiredTalent)
				.build();
	}

	protected Attributes readAttributes(int maxAttributes) {
		return readAttributes("", maxAttributes);
	}

	protected Attributes readAttributes(String prefix, int maxAttributes) {
		AttributesBuilder builder = new AttributesBuilder();
		for (int statNo = 1; statNo <= maxAttributes; ++statNo) {
			readAttribute(builder, prefix, statNo);
		}

		return builder.toAttributes();
	}

	private void readAttribute(AttributesBuilder builder, String prefix, int statNo) {
		ExcelColumn colStat = column(colStat(prefix, statNo));
		ExcelColumn colAmount = column(colAmount(prefix, statNo));

		var attributeStr = colStat.getString(null);

		if (attributeStr == null) {
			return;
		}

		if (colAmount.getString(null) != null) {
			var attributeSupplier = PrimitiveAttributeSupplier.fromString(attributeStr);
			attributeSupplier.addAttributeList(builder, colAmount.getDouble());
		} else {
			var attribute = ComplexAttributeMapper.fromString(attributeStr);
			builder.addAttribute(attribute);
		}
	}
}
