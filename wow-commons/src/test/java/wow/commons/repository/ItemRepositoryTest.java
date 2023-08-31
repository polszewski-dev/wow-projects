package wow.commons.repository;

import org.junit.jupiter.api.Test;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.condition.MiscCondition;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.primitive.PrimitiveAttributeId.*;
import static wow.commons.model.categorization.ArmorSubType.CLOTH;
import static wow.commons.model.categorization.Binding.BINDS_ON_PICK_UP;
import static wow.commons.model.categorization.Binding.NO_BINDING;
import static wow.commons.model.categorization.ItemRarity.EPIC;
import static wow.commons.model.categorization.ItemType.CHEST;
import static wow.commons.model.categorization.ItemType.TOKEN;
import static wow.commons.model.character.CharacterClassId.*;
import static wow.commons.model.profession.ProfessionId.TAILORING;
import static wow.commons.model.pve.PhaseId.TBC_P3;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
class ItemRepositoryTest extends WowCommonsSpringTest {
	/*
		Sunfire Robe	Phase 5

		Item Level 159
		Binds when picked up
		Chest	Cloth
		266 Armor
		+36 Stamina
		+34 Intellect

		Red Socket
		Red Socket
		Red Socket
		Socket Bonus: +5 Spell Damage and Healing

		Durability 100 / 100
		Requires Level 70
		Requires Tailoring (350)
		Equip: Improves spell critical strike rating by 40 (1.81% @ L70).
		Equip: Improves spell haste rating by 40 (2.54% @ L70).
		Equip: Increases damage and healing done by magical spells and effects by up to 71.
		Sell Price: 6 8 49
		 */

	@Test
	void basicItemInfo() {
		var item = itemRepository.getItem(34364, TBC_P5).orElseThrow();

		assertThat(item.getId()).isEqualTo(34364);
		assertThat(item.getName()).isEqualTo("Sunfire Robe");
		assertThat(item.getRarity()).isEqualTo(EPIC);
		assertThat(item.getBinding()).isEqualTo(BINDS_ON_PICK_UP);
		assertThat(item.isUnique()).isFalse();
		assertThat(item.getItemLevel()).isEqualTo(159);
		assertThat(item.getTimeRestriction().phaseId()).isEqualTo(TBC_P5);
		assertThat(item.getCharacterRestriction().level()).isEqualTo(70);
		assertThat(item.getCharacterRestriction().professionRestriction().professionId()).isEqualTo(TAILORING);
		assertThat(item.getCharacterRestriction().professionRestriction().level()).isEqualTo(350);
		assertThat(item.getItemType()).isEqualTo(CHEST);
		assertThat(item.getItemSubType()).isEqualTo(CLOTH);
		assertThat(item.getItemSet()).isNull();
		assertThat(item.getIcon()).isEqualTo("inv_chest_cloth_02");
		assertThat(item.getWeaponStats()).isNull();
	}

	@Test
	void socketInfo() {
		var item = itemRepository.getItem(34364, TBC_P5).orElseThrow();

		var socketSpecification = item.getSocketSpecification();

		assertThat(socketSpecification.getSocketCount()).isEqualTo(3);
		assertThat(socketSpecification.getSocketType(0)).isEqualTo(SocketType.RED);
		assertThat(socketSpecification.getSocketType(1)).isEqualTo(SocketType.RED);
		assertThat(socketSpecification.getSocketType(2)).isEqualTo(SocketType.RED);

		assertEffect(socketSpecification.socketBonus(), POWER, 5, MiscCondition.SPELL, "+5 Spell Damage and Healing", new ItemSource(item));
	}

	@Test
	void itemStats() {
		var item = itemRepository.getItem(34364, TBC_P5).orElseThrow();
		var source = new ItemSource(item);

		assertThat(item.getEffects()).hasSize(6);
		assertEffect(item.getEffects().get(0), ARMOR, 266, "266 Armor", source);
		assertEffect(item.getEffects().get(1), STAMINA, 36, "+36 Stamina", source);
		assertEffect(item.getEffects().get(2), INTELLECT, 34, "+34 Intellect", source);
		assertEffect(item.getEffects().get(3), CRIT_RATING, 40, MiscCondition.SPELL, "Equip: Improves spell critical strike rating by 40.", source);
		assertEffect(item.getEffects().get(4), HASTE_RATING, 40, MiscCondition.SPELL, "Equip: Improves spell haste rating by 40.", source);
		assertEffect(item.getEffects().get(5), POWER, 71, MiscCondition.SPELL, "Equip: Increases damage and healing done by magical spells and effects by up to 71.", source);
	}

	@Test
	void itemSet() {
		var item = itemRepository.getItem(24262, TBC_P5).orElseThrow();
		var itemSet = item.getItemSet();

		assertThat(itemSet.getName()).isEqualTo("Spellstrike Infusion");
		assertThat(itemSet.getRequiredProfession()).isEqualTo(TAILORING);
		assertThat(itemSet.getItemSetBonuses()).hasSize(1);

		var bonus = itemSet.getItemSetBonuses().get(0);

		assertThat(bonus.numPieces()).isEqualTo(2);
		assertEffect(bonus.bonusEffect(), 224266, "Gives a chance when your harmful spells land to increase the damage of your spells and effects by 92 for 10 sec. (Proc chance: 5%)");
	}

	/*
		Reckless Pyrestone	Phase 3

		Item Level 100
		+5 Spell Haste Rating and +6 Spell Damage
		"Matches a Red or Yellow Socket."
		Sell Price: 6
	 */

	@Test
	void basicGemInfo() {
		var gem = itemRepository.getGem(35760, TBC_P5).orElseThrow();

		assertThat(gem.getId()).isEqualTo(35760);
		assertThat(gem.getName()).isEqualTo("Reckless Pyrestone");
		assertThat(gem.getRarity()).isEqualTo(EPIC);
		assertThat(gem.getBinding()).isEqualTo(NO_BINDING);
		assertThat(gem.isUnique()).isFalse();
		assertThat(gem.getItemLevel()).isEqualTo(100);
		assertThat(gem.getTimeRestriction().phaseId()).isEqualTo(TBC_P5);
		assertThat(gem.getCharacterRestriction().level()).isNull();
		assertThat(gem.getIcon()).isEqualTo("inv_jewelcrafting_pyrestone_02");
	}

	@Test
	void normalGemStats() {
		var gem = itemRepository.getGem(35760, TBC_P5).orElseThrow();
		var source = new ItemSource(gem);

		assertThat(gem.getColor()).isEqualTo(GemColor.ORANGE);

		assertThat(gem.getEffects()).hasSize(2);

		assertEffect(gem.getEffects().get(0), HASTE_RATING, 5, MiscCondition.SPELL, "+5 Spell Haste Rating", source);
		assertEffect(gem.getEffects().get(1), POWER, 6, MiscCondition.SPELL_DAMAGE, "+6 Spell Damage", source);
	}

	/*
		Chaotic Skyfire Diamond	Phase 1

		Item Level 70
		+12 Spell Critical & 3% Increased Critical Damage
		Requires at least 2 Blue Gems
		"Only fits in a meta gem slot."
		Sell Price: 3
	 */

	@Test
	void metaGemStats() {
		var gem = itemRepository.getGem(34220, TBC_P5).orElseThrow();
		var source = new ItemSource(gem);

		assertThat(gem.getColor()).isEqualTo(GemColor.META);
		assertThat(gem.getMetaEnablers()).hasSameElementsAs(List.of(MetaEnabler.AT_LEAST_2_BLUES));

		assertThat(gem.getEffects()).hasSize(2);
		assertEffect(gem.getEffects().get(0), CRIT_RATING, 12, MiscCondition.SPELL, "+12 Spell Critical", source);
		assertEffect(gem.getEffects().get(1), CRIT_DAMAGE_PCT, 3, "3% Increased Critical Damage", source);
	}

	@Test
	void basicEnchantInfo() {
		var enchant = itemRepository.getEnchant(31372, TBC_P5).orElseThrow();

		assertThat(enchant.getId()).isEqualTo(31372);
		assertThat(enchant.getName()).isEqualTo("Runic Spellthread");
		assertThat(enchant.getItemTypes()).hasSameElementsAs(List.of(ItemType.LEGS));
		assertThat(enchant.getItemSubTypes()).isEmpty();
		assertThat(enchant.getRarity()).isEqualTo(EPIC);
	}

	@Test
	void enchantStats() {
		var enchant = itemRepository.getEnchant(31372, TBC_P5).orElseThrow();

		assertEffect(
				enchant.getEffect(),
				Attributes.of(
						Attribute.of(POWER, 35, MiscCondition.SPELL),
						Attribute.of(STAMINA, 20)
				),
				"Use: Permanently embroiders spellthread into pants, increasing spell damage and healing by up to 35 and Stamina by 20.",
				new EnchantSource(enchant)
		);
	}

	@Test
	void token() {
		var tradedItem = itemRepository.getTradedItem(31101, TBC_P5).orElseThrow();

		assertThat(tradedItem.getId()).isEqualTo(31101);
		assertThat(tradedItem.getName()).isEqualTo("Pauldrons of the Forgotten Conqueror");
		assertThat(tradedItem.getItemType()).isEqualTo(TOKEN);
		assertThat(tradedItem.getRarity()).isEqualTo(EPIC);
		assertThat(tradedItem.getBinding()).isEqualTo(BINDS_ON_PICK_UP);
		assertThat(tradedItem.isUnique()).isFalse();
		assertThat(tradedItem.getItemLevel()).isEqualTo(70);
		assertThat(tradedItem.getTimeRestriction().phaseId()).isEqualTo(TBC_P3);
		assertThat(tradedItem.getCharacterRestriction().level()).isEqualTo(70);
		assertThat(tradedItem.getCharacterRestriction().characterClassIds()).hasSameElementsAs(List.of(PALADIN, PRIEST, WARLOCK));
	}
}
