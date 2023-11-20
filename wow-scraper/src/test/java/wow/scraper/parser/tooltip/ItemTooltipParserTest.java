package wow.scraper.parser.tooltip;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.commons.model.Money;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.condition.ConditionOperator;
import wow.commons.model.attribute.condition.MiscCondition;
import wow.commons.model.spell.AbilityId;
import wow.scraper.model.JsonItemDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.*;
import static wow.commons.model.categorization.ArmorSubType.CLOTH;
import static wow.commons.model.categorization.Binding.BINDS_ON_PICK_UP;
import static wow.commons.model.categorization.ItemType.*;
import static wow.commons.model.categorization.WeaponSubType.STAFF;
import static wow.commons.model.categorization.WeaponSubType.WAND;
import static wow.commons.model.item.SocketType.*;
import static wow.commons.model.profession.ProfessionId.TAILORING;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2022-11-14
 */
class ItemTooltipParserTest extends TooltipParserTest<JsonItemDetails, ItemTooltipParser> {
	@Test
	@DisplayName("Item id/name are parsed correctly")
	void idAndName() {
		assertThat(sunfireRobe.getItemId()).isEqualTo(34364);
		assertThat(sunfireRobe.getName()).isEqualTo("Sunfire Robe");
	}

	@Test
	@DisplayName("Basic info is parsed correctly")
	void basicInfo() {
		assertThat(sunfireRobe.getTimeRestriction().phaseId()).isEqualTo(TBC_P5);
		assertThat(sunfireRobe.getRequiredLevel()).isEqualTo(70);
		assertThat(sunfireRobe.getItemLevel()).isEqualTo(159);
		assertThat(sunfireRobe.getBinding()).isEqualTo(BINDS_ON_PICK_UP);
		assertThat(sunfireRobe.isUnique()).isFalse();

		assertThat(hoodOfMalefic.getItemType()).isEqualTo(HEAD);
		assertThat(hoodOfMalefic.getItemSubType()).isEqualTo(CLOTH);

		assertThat(sunfireRobe.getItemType()).isEqualTo(CHEST);
		assertThat(sunfireRobe.getItemSubType()).isEqualTo(CLOTH);

		assertThat(scryersBlodgem.getItemType()).isEqualTo(TRINKET);
		assertThat(scryersBlodgem.getItemSubType()).isNull();

		assertThat(magistersStaff.getItemType()).isEqualTo(TWO_HAND);
		assertThat(magistersStaff.getItemSubType()).isEqualTo(STAFF);

		assertThat(wandOfDemonsoul.getItemType()).isEqualTo(RANGED);
		assertThat(wandOfDemonsoul.getItemSubType()).isEqualTo(WAND);
	}

	@Test
	@DisplayName("Socket info is parsed correctly")
	void socketInfo() {
		assertThat(hoodOfMalefic.getSocketTypes()).isEqualTo(List.of(META, YELLOW));

		var hoodSocketBonus = hoodOfMalefic.getSocketBonus().orElseThrow();
		assertEffect(hoodSocketBonus, POWER, 5, MiscCondition.SPELL, "+5 Spell Damage and Healing");

		assertThat(sunfireRobe.getSocketTypes()).isEqualTo(List.of(RED, RED, RED));

		var robeSocketBonus = sunfireRobe.getSocketBonus().orElseThrow();
		assertEffect(robeSocketBonus, POWER, 5, MiscCondition.SPELL, "+5 Spell Damage and Healing");
	}

	@Test
	@DisplayName("Stats are parsed correctly")
	void stats() {
		var parsedEffects = sunfireRobe.getItemStatParser().getItemEffects();

		assertThat(parsedEffects).hasSize(6);

		assertEffect(parsedEffects, 0, ARMOR, 266, "266 Armor");
		assertEffect(parsedEffects, 1, STAMINA, 36, "+36 Stamina");
		assertEffect(parsedEffects, 2, INTELLECT, 34, "+34 Intellect");
		assertEffect(parsedEffects, 3, CRIT_RATING, 40, MiscCondition.SPELL, "Equip: Improves spell critical strike rating by 40.");
		assertEffect(parsedEffects, 4, HASTE_RATING, 40, MiscCondition.SPELL, "Equip: Improves spell haste rating by 40.");
		assertEffect(parsedEffects, 5, POWER, 71, MiscCondition.SPELL, "Equip: Increases damage and healing done by magical spells and effects by up to 71.");
	}

	@Test
	@DisplayName("Weapon stats are parsed correctly")
	void weaponStats() {
		assertThat(sunfireRobe.getWeaponStats()).isNull();
		assertThat(magistersStaff.getWeaponStats()).isNotNull();
		assertThat(magistersStaff.getWeaponStats().weaponDamageMin()).isEqualTo(146);
		assertThat(magistersStaff.getWeaponStats().weaponDamageMax()).isEqualTo(326);
		assertThat(magistersStaff.getWeaponStats().damageType()).isNull();
		assertThat(magistersStaff.getWeaponStats().weaponDps()).isEqualTo(73.75);
		assertThat(magistersStaff.getWeaponStats().weaponSpeed()).isEqualTo(3.20);
	}

	@Test
	@DisplayName("Item set info is parsed correctly")
	void itemSet() {
		assertThat(hoodOfMalefic.getItemSetName()).isEqualTo("Malefic Raiment");
		assertThat(hoodOfMalefic.getItemSetPieces()).isEqualTo(List.of(
				"Gloves of the Malefic",
				"Hood of the Malefic",
				"Leggings of the Malefic",
				"Mantle of the Malefic",
				"Robe of the Malefic",
				"Boots of the Malefic",
				"Bracers of the Malefic",
				"Belt of the Malefic"
		));
		var itemSetBonuses = hoodOfMalefic.getItemSetBonuses();
		assertThat(itemSetBonuses).hasSize(2);
		assertThat(itemSetBonuses.get(0).numPieces()).isEqualTo(2);
		assertEffect(itemSetBonuses.get(0).bonusEffect(), 0, "Each time one of your Corruption or Immolate spells deals periodic damage, you heal 70 health.");
		assertThat(itemSetBonuses.get(1).numPieces()).isEqualTo(4);
		assertEffect(
				itemSetBonuses.get(1).bonusEffect(),
				Attributes.of(
						 Attribute.of(DAMAGE_PCT, 6, ConditionOperator.comma(
								 AttributeCondition.of(AbilityId.SHADOW_BOLT),
								 AttributeCondition.of(AbilityId.INCINERATE)
						 ))
				 ),
				"Increases the damage dealt by your Shadow Bolt and Incinerate abilities by 6%."
		);
		assertThat(hoodOfMalefic.getItemSetRequiredProfession()).isNull();
		assertThat(hoodOfMalefic.getItemSetRequiredProfessionLevel()).isNull();
	}

	@Test
	@DisplayName("Faction restriction is parsed correctly")
	void factionRestriction() {
		assertThat(scryersBlodgem.getRequiredFactionName()).isEqualTo("The Scryers");
		assertThat(scryersBlodgem.getRequiredFactionStanding()).isEqualTo("Revered");
	}

	@Test
	@DisplayName("Profession restriction is parsed correctly")
	void professionRestriction() {
		assertThat(sunfireRobe.getRequiredProfession()).isEqualTo(TAILORING);
		assertThat(sunfireRobe.getRequiredProfessionLevel()).isEqualTo(350);
		assertThat(sunfireRobe.getRequiredProfessionSpec()).isNull();
	}

	@Test
	@DisplayName("Misc restriction is parsed correctly")
	void miscRestriction() {
		assertThat(sunfireRobe.getRequiredClass()).isNull();
		assertThat(sunfireRobe.getRequiredRace()).isNull();
		assertThat(sunfireRobe.getRequiredSide()).isNull();

	}

	@Test
	@DisplayName("Misc info is parsed correctly")
	void misc() {
		assertThat(sunfireRobe.isRandomEnchantment()).isFalse();
		assertThat(sunfireRobe.getDroppedBy()).isNull();
		assertThat(sunfireRobe.getDropChance()).isNull();
		assertThat(sunfireRobe.getSellPrice()).isEqualTo(Money.parse("6g8s49c"));
	}

	ItemTooltipParser hoodOfMalefic;
	ItemTooltipParser sunfireRobe;
	ItemTooltipParser scryersBlodgem;
	ItemTooltipParser magistersStaff;
	ItemTooltipParser wandOfDemonsoul;

	@BeforeEach
	void readTestData() {
		hoodOfMalefic = getTooltip("item/31051");
		sunfireRobe = getTooltip("item/34364");
		scryersBlodgem = getTooltip("item/29132");
		magistersStaff = getTooltip("item/34182");
		wandOfDemonsoul = getTooltip("item/34347");
	}

	@Override
	protected ItemTooltipParser createParser(JsonItemDetails data) {
		return new ItemTooltipParser(data, TBC, scraperContext);
	}

	@Override
	protected Class<JsonItemDetails> getDetailsClass() {
		return JsonItemDetails.class;
	}
}