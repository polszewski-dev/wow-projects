package wow.scraper.parser.tooltip;

import org.junit.jupiter.api.Test;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.condition.MiscCondition;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadItemQuality;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.primitive.PrimitiveAttributeId.*;
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
	void enchantRingsSpellpower() {
		EnchantTooltipParser underTest = getTooltip("enchant/27924");

		assertThat(underTest.getSpellId()).isEqualTo(27924);
		assertThat(underTest.getName()).isEqualTo("Enchant Ring - Spellpower");
		assertThat(underTest.getItemTypes()).hasSameElementsAs(List.of(FINGER));
		assertThat(underTest.getItemSubTypes()).isEmpty();
		assertThat(underTest.getRequiredClass()).isNull();
		assertThat(underTest.getRequiredProfession()).isEqualTo(ENCHANTING);
		assertThat(underTest.getRequiredProfessionLevel()).isEqualTo(360);
		assertEffect(underTest.getEffect().orElseThrow(), POWER, 12, MiscCondition.SPELL, "Permanently enchant a ring to increase spell damage and healing by up to 12.  Only the Enchanter's rings can be enchanted, and enchanting a ring will cause it to become soulbound.");
		assertThat(underTest.getDetails().getEnchantId()).isEqualTo(2928);
		assertThat(underTest.getExclusiveFaction()).isNull();
		assertThat(underTest.getDetails().getQuality()).isEqualTo(-1);
		assertThat(underTest.getTimeRestriction().phaseId()).isNull();
	}

	@Test
	void greaterInscriptionOfTheOrb() {
		EnchantTooltipParser underTest = getTooltip("enchant/35437");

		assertThat(underTest.getSpellId()).isEqualTo(35437);
		assertThat(underTest.getName()).isEqualTo("Greater Inscription of the Orb");
		assertThat(underTest.getItemTypes()).hasSameElementsAs(List.of(SHOULDER));
		assertThat(underTest.getItemSubTypes()).isEmpty();
		assertThat(underTest.getRequiredClass()).isNull();
		assertThat(underTest.getRequiredProfession()).isNull();
		assertThat(underTest.getRequiredProfessionLevel()).isNull();
		assertEffect(
				underTest.getEffect().orElseThrow(),
				Attributes.of(
						Attribute.of(CRIT_RATING, 15, MiscCondition.SPELL),
						Attribute.of(POWER, 12, MiscCondition.SPELL_DAMAGE)
				),
				"Use: Permanently adds 15 spell critical strike rating and up to 12 spell damage to a shoulder slot item."
		);
		assertThat(underTest.getDetails().getEnchantId()).isEqualTo(2995);
		assertThat(underTest.getExclusiveFaction()).isEqualTo(SCRYERS);
		assertThat(underTest.getDetails().getQuality()).isEqualTo(WowheadItemQuality.RARE.getCode());
		assertThat(underTest.getTimeRestriction().phaseId()).isEqualTo(TBC_P1);
	}

	@Test
	void hoodooHex() {
		EnchantTooltipParser underTest = getTooltip("enchant/24165");

		assertThat(underTest.getSpellId()).isEqualTo(24165);
		assertThat(underTest.getName()).isEqualTo("Hoodoo Hex");
		assertThat(underTest.getItemTypes()).hasSameElementsAs(List.of(HEAD, LEGS));
		assertThat(underTest.getItemSubTypes()).isEmpty();
		assertThat(underTest.getRequiredClass()).hasSameElementsAs(List.of(WARLOCK));
		assertThat(underTest.getRequiredProfession()).isNull();
		assertThat(underTest.getRequiredProfessionLevel()).isNull();
		assertEffect(
				underTest.getEffect().orElseThrow(),
				Attributes.of(
						Attribute.of(STAMINA, 10),
						Attribute.of(POWER, 18, MiscCondition.SPELL)
				),
				"Use: Permanently adds 10 Stamina and increases spell damage and healing by up to 18 to a leg or head slot item."
		);
		assertThat(underTest.getDetails().getEnchantId()).isEqualTo(2589);
		assertThat(underTest.getExclusiveFaction()).isNull();
		assertThat(underTest.getDetails().getQuality()).isEqualTo(WowheadItemQuality.RARE.getCode());
		assertThat(underTest.getTimeRestriction().phaseId()).isNull();
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