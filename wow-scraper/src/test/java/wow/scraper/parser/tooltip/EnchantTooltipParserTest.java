package wow.scraper.parser.tooltip;

import org.junit.jupiter.api.Test;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadItemQuality;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemType.*;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.ExclusiveFaction.SCRYERS;
import static wow.commons.model.profession.ProfessionId.ENCHANTING;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.PhaseId.TBC_P1;

/**
 * User: POlszewski
 * Date: 2022-11-17
 */
class EnchantTooltipParserTest extends TooltipParserTest<JsonSpellDetails, EnchantTooltipParser> {
	@Test
	void enchantRingsSpellpower() throws IOException {
		EnchantTooltipParser underTest = getTooltip("enchant/27924");

		assertThat(underTest.getSpellId()).isEqualTo(27924);
		assertThat(underTest.getName()).isEqualTo("Enchant Ring - Spellpower");
		assertThat(underTest.getItemTypes()).hasSameElementsAs(List.of(FINGER));
		assertThat(underTest.getItemSubTypes()).isEmpty();
		assertThat(underTest.getRequiredClass()).isNull();
		assertThat(underTest.getRequiredProfession()).isEqualTo(ENCHANTING);
		assertThat(underTest.getRequiredProfessionLevel()).isEqualTo(360);
		assertThat(underTest.getParsedStats().getSpellPower()).isEqualTo(12);
		assertThat(underTest.getDetails().getEnchantId()).isEqualTo(2928);
		assertThat(underTest.getExclusiveFaction()).isNull();
		assertThat(underTest.getDetails().getQuality()).isEqualTo(-1);
		assertThat(underTest.getPhase()).isEqualTo(TBC_P1);
	}

	@Test
	void greaterInscriptionOfTheOrb() throws IOException {
		EnchantTooltipParser underTest = getTooltip("enchant/35437");

		assertThat(underTest.getSpellId()).isEqualTo(35437);
		assertThat(underTest.getName()).isEqualTo("Greater Inscription of the Orb");
		assertThat(underTest.getItemTypes()).hasSameElementsAs(List.of(SHOULDER));
		assertThat(underTest.getItemSubTypes()).isEmpty();
		assertThat(underTest.getRequiredClass()).isNull();
		assertThat(underTest.getRequiredProfession()).isNull();
		assertThat(underTest.getRequiredProfessionLevel()).isNull();
		assertThat(underTest.getParsedStats().getSpellDamage()).isEqualTo(12);
		assertThat(underTest.getParsedStats().getSpellCritRating()).isEqualTo(15);
		assertThat(underTest.getDetails().getEnchantId()).isEqualTo(2995);
		assertThat(underTest.getExclusiveFaction()).isEqualTo(SCRYERS);
		assertThat(underTest.getDetails().getQuality()).isEqualTo(WowheadItemQuality.RARE.getCode());
		assertThat(underTest.getPhase()).isEqualTo(TBC_P1);
	}

	@Test
	void hoodooHex() throws IOException {
		EnchantTooltipParser underTest = getTooltip("enchant/24165");

		assertThat(underTest.getSpellId()).isEqualTo(24165);
		assertThat(underTest.getName()).isEqualTo("Hoodoo Hex");
		assertThat(underTest.getItemTypes()).hasSameElementsAs(List.of(HEAD, LEGS));
		assertThat(underTest.getItemSubTypes()).isEmpty();
		assertThat(underTest.getRequiredClass()).hasSameElementsAs(List.of(WARLOCK));
		assertThat(underTest.getRequiredProfession()).isNull();
		assertThat(underTest.getRequiredProfessionLevel()).isNull();
		assertThat(underTest.getParsedStats().getSpellPower()).isEqualTo(18);
		assertThat(underTest.getParsedStats().getStamina()).isEqualTo(10);
		assertThat(underTest.getDetails().getEnchantId()).isEqualTo(2589);
		assertThat(underTest.getExclusiveFaction()).isNull();
		assertThat(underTest.getDetails().getQuality()).isEqualTo(WowheadItemQuality.RARE.getCode());
		assertThat(underTest.getPhase()).isEqualTo(TBC_P1);
	}

	@Override
	protected EnchantTooltipParser createParser(JsonSpellDetails data) {
		return new EnchantTooltipParser(data, TBC, scraperContext);
	}

	@Override
	protected Class<JsonSpellDetails> getDetailsClass() {
		return JsonSpellDetails.class;
	}
}