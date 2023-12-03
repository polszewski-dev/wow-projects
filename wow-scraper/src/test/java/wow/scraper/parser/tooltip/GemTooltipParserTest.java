package wow.scraper.parser.tooltip;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.commons.model.attribute.condition.MiscCondition;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.MetaEnabler;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.WowheadItemCategory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.*;
import static wow.commons.model.categorization.Binding.BINDS_ON_PICK_UP;
import static wow.commons.model.categorization.Binding.NO_BINDING;
import static wow.commons.model.item.GemColor.*;
import static wow.commons.model.profession.ProfessionId.JEWELCRAFTING;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.PhaseId.*;
import static wow.scraper.model.WowheadItemCategory.GEMS;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
class GemTooltipParserTest extends TooltipParserTest<JsonItemDetails, GemTooltipParser, WowheadItemCategory> {
	@Test
	@DisplayName("Item id/name are parsed correctly")
	void idAndName() {
		assertThat(redGem.getItemId()).isEqualTo(32196);
		assertThat(redGem.getName()).isEqualTo("Runed Crimson Spinel");

		assertThat(metaGem.getItemId()).isEqualTo(34220);
		assertThat(metaGem.getName()).isEqualTo("Chaotic Skyfire Diamond");
	}

	@Test
	@DisplayName("Basic info is parsed correctly")
	void basicInfo() {
		assertThat(redGem.getItemLevel()).isEqualTo(100);
		assertThat(redGem.getBinding()).isEqualTo(NO_BINDING);
		assertThat(redGem.isUnique()).isFalse();

		assertThat(blueGem.getItemLevel()).isEqualTo(130);
		assertThat(blueGem.getBinding()).isEqualTo(BINDS_ON_PICK_UP);
		assertThat(blueGem.isUnique()).isTrue();
	}

	@Test
	@DisplayName("Phase is parsed correctly")
	void gameVersionPhase() {
		assertThat(redGem.getTimeRestriction()).isEqualTo(TimeRestriction.of(TBC, TBC_P3));
		assertThat(blueGem.getTimeRestriction()).isEqualTo(TimeRestriction.of(TBC, TBC_P0));
		assertThat(metaGem.getTimeRestriction()).isEqualTo(TimeRestriction.of(TBC, TBC_P1));
	}

	@Test
	@DisplayName("Color are parsed correctly")
	void color() {
		assertThat(redGem.getColor()).isEqualTo(RED);
		assertThat(orangeGem.getColor()).isEqualTo(ORANGE);
		assertThat(yellowGem.getColor()).isEqualTo(YELLOW);
		assertThat(greenGem.getColor()).isEqualTo(GREEN);
		assertThat(blueGem.getColor()).isEqualTo(BLUE);
		assertThat(purpleGem.getColor()).isEqualTo(PURPLE);
		assertThat(metaGem.getColor()).isEqualTo(META);
	}

	@Test
	@DisplayName("Stats are parsed correctly")
	void stats() {
		assertEffect(redGem.getEffects(), 0, POWER, 12, MiscCondition.SPELL_DAMAGE, "+12 Spell Damage");
		assertEffect(orangeGem.getEffects(), 0, HASTE_RATING, 5, MiscCondition.SPELL, "+5 Spell Haste Rating");
		assertEffect(orangeGem.getEffects(), 1, POWER, 6, MiscCondition.SPELL_DAMAGE, "+6 Spell Damage");
		assertEffect(yellowGem.getEffects(), 0, HASTE_RATING, 10, MiscCondition.SPELL, "+10 Spell Haste Rating");
		assertEffect(greenGem.getEffects(), 0, HASTE_RATING, 5, MiscCondition.SPELL, "+5 Spell Haste Rating");
		assertEffect(greenGem.getEffects(), 1, STAMINA, 7, "+7 Stamina");
		assertEffect(blueGem.getEffects(), 0, STAMINA, 18, "+18 Stamina");

		assertEffect(metaGem.getEffects(), 0, CRIT_RATING, 12, MiscCondition.SPELL, "+12 Spell Critical");
		assertEffect(metaGem.getEffects(), 1, CRIT_DAMAGE_PCT, 3, "3% Increased Critical Damage");
		assertThat(metaGem.getMetaEnablers()).isEqualTo(List.of(MetaEnabler.AT_LEAST_2_BLUES));
	}

	@Test
	@DisplayName("Profession restriction is parsed correctly")
	void professionRestriction() {
		assertThat(blueGem.getRequiredProfession()).isEqualTo(JEWELCRAFTING);
		assertThat(blueGem.getRequiredProfessionLevel()).isEqualTo(360);
	}

	GemTooltipParser redGem;
	GemTooltipParser orangeGem;
	GemTooltipParser yellowGem;
	GemTooltipParser greenGem;
	GemTooltipParser blueGem;
	GemTooltipParser purpleGem;
	GemTooltipParser metaGem;

	@BeforeEach
	void readTestData() {
		redGem = getTooltip(32196, GEMS);
		orangeGem = getTooltip(35760, GEMS);
		yellowGem = getTooltip(35761, GEMS);
		greenGem = getTooltip(35759, GEMS);
		blueGem = getTooltip(33135, GEMS);
		purpleGem = getTooltip(32215, GEMS);
		metaGem = getTooltip(34220, GEMS);
	}

	@Override
	protected JsonItemDetails getData(int id, WowheadItemCategory category) {
		return itemDetailRepository.getDetail(TBC, category, id).orElseThrow();
	}

	@Override
	protected GemTooltipParser createParser(JsonItemDetails data) {
		return new GemTooltipParser(data, TBC, scraperContext);
	}
}