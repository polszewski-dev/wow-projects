package wow.commons.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.commons.WowCommonsTestConfig;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.SPELL_POWER;
import static wow.commons.model.categorization.ArmorSubType.CLOTH;
import static wow.commons.model.categorization.Binding.BINDS_ON_PICK_UP;
import static wow.commons.model.categorization.Binding.NO_BINDING;
import static wow.commons.model.categorization.ItemRarity.EPIC;
import static wow.commons.model.categorization.ItemType.CHEST;
import static wow.commons.model.categorization.ItemType.TOKEN;
import static wow.commons.model.character.CharacterClass.*;
import static wow.commons.model.professions.Profession.TAILORING;
import static wow.commons.model.pve.Phase.TBC_P3;
import static wow.commons.model.pve.Phase.TBC_P5;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowCommonsTestConfig.class)
class ItemDataRepositoryTest {
	@Autowired
	ItemDataRepository underTest;

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
	@DisplayName("Basic Item info is read correctly")
	void basicItemInfoIsCorrect() {
		Optional<Item> optionalItem = underTest.getItem(34364);

		assertThat(optionalItem).isPresent();

		Item item = optionalItem.orElseThrow();

		assertThat(item.getId()).isEqualTo(34364);
		assertThat(item.getName()).isEqualTo("Sunfire Robe");
		assertThat(item.getRarity()).isEqualTo(EPIC);
		assertThat(item.getBinding()).isEqualTo(BINDS_ON_PICK_UP);
		assertThat(item.isUnique()).isFalse();
		assertThat(item.getItemLevel()).isEqualTo(159);
		assertThat(item.getRestriction().getLevel()).isEqualTo(70);
		assertThat(item.getRestriction().getPhase()).isEqualTo(TBC_P5);
		assertThat(item.getRestriction().getProfessionRestriction().getProfession()).isEqualTo(TAILORING);
		assertThat(item.getRestriction().getProfessionRestriction().getLevel()).isEqualTo(350);
		assertThat(item.getIcon()).isEqualTo("inv_chest_cloth_02");
		assertThat(item.getTooltip()).isNotBlank();
		assertThat(item.getItemType()).isEqualTo(CHEST);
		assertThat(item.getItemSubType()).isEqualTo(CLOTH);
		assertThat(item.getItemSet()).isNull();
		assertThat(item.getWeaponStats()).isNull();
	}

	@Test
	@DisplayName("Socket info is read correctly")
	void socketInfoIsCorrect() {
		Optional<Item> optionalItem = underTest.getItem(34364);

		assertThat(optionalItem).isPresent();

		Item item = optionalItem.orElseThrow();

		ItemSocketSpecification socketSpecification = item.getSocketSpecification();

		assertThat(socketSpecification.getSocketCount()).isEqualTo(3);
		assertThat(socketSpecification.getSocketType(0)).isEqualTo(SocketType.RED);
		assertThat(socketSpecification.getSocketType(1)).isEqualTo(SocketType.RED);
		assertThat(socketSpecification.getSocketType(2)).isEqualTo(SocketType.RED);
		assertThat(socketSpecification.getSocketBonus().getPrimitiveAttributeList().get(0)).isEqualTo(Attribute.of(SPELL_POWER, 5));
	}

	@Test
	@DisplayName("Item stats are read correctly")
	void itemStatsAreCorrect() {
		Optional<Item> optionalItem = underTest.getItem(34364);

		assertThat(optionalItem).isPresent();

		Item item = optionalItem.orElseThrow();

		assertThat(item.getArmor()).isEqualTo(266);
		assertThat(item.getStamina()).isEqualTo(36);
		assertThat(item.getIntellect()).isEqualTo(34);
		assertThat(item.getSpellCritRating()).isEqualTo(40);
		assertThat(item.getSpellHasteRating()).isEqualTo(40);
		assertThat(item.getSpellPower()).isEqualTo(71);
	}

	/*
	Reckless Pyrestone	Phase 3

	Item Level 100
	+5 Spell Haste Rating and +6 Spell Damage
	"Matches a Red or Yellow Socket."
	Sell Price: 6
	 */

	@Test
	@DisplayName("Basic Gem info is read correctly")
	void basicGemInfoIsCorrect() {
		Optional<Gem> optionalGem = underTest.getGem(35760);

		assertThat(optionalGem).isPresent();

		Gem gem = optionalGem.orElseThrow();

		assertThat(gem.getId()).isEqualTo(35760);
		assertThat(gem.getName()).isEqualTo("Reckless Pyrestone");
		assertThat(gem.getRarity()).isEqualTo(EPIC);
		assertThat(gem.getBinding()).isEqualTo(NO_BINDING);
		assertThat(gem.isUnique()).isFalse();
		assertThat(gem.getItemLevel()).isEqualTo(100);
		assertThat(gem.getRestriction().getLevel()).isNull();
		assertThat(gem.getRestriction().getPhase()).isEqualTo(TBC_P3);
		assertThat(gem.getIcon()).isEqualTo("inv_jewelcrafting_pyrestone_02");
		assertThat(gem.getTooltip()).isNotBlank();
	}

	@Test
	@DisplayName("Normal Gem stats are read correctly")
	void normalGemStatsAreCorrect() {
		Optional<Gem> optionalGem = underTest.getGem(35760);

		assertThat(optionalGem).isPresent();

		Gem gem = optionalGem.orElseThrow();

		assertThat(gem.getColor()).isEqualTo(GemColor.ORANGE);
		assertThat(gem.getSpellHasteRating()).isEqualTo(5);
		assertThat(gem.getSpellDamage()).isEqualTo(6);
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
	@DisplayName("Meta Gem stats are read correctly")
	void metaGemStatsAreCorrect() {
		Optional<Gem> optionalGem = underTest.getGem(34220);

		assertThat(optionalGem).isPresent();

		Gem gem = optionalGem.orElseThrow();

		assertThat(gem.getColor()).isEqualTo(GemColor.META);
		assertThat(gem.getSpellCritRating()).isEqualTo(12);
		assertThat(gem.getIncreasedCriticalDamagePct()).isEqualTo(Percent.of(3));
		assertThat(gem.getMetaEnablers()).hasSameElementsAs(List.of(MetaEnabler.AT_LEAST_2_BLUES));
	}

	@Test
	@DisplayName("Enchant is read correctly")
	void enchantIsCorrect() {
		Optional<Enchant> optionalEnchant = underTest.getEnchant(2748);

		assertThat(optionalEnchant).isPresent();

		Enchant enchant = optionalEnchant.orElseThrow();

		assertThat(enchant.getId()).isEqualTo(2748);
		assertThat(enchant.getName()).isEqualTo("Runic Spellthread");
		assertThat(enchant.getItemTypes()).hasSameElementsAs(List.of(ItemType.LEGS));
		assertThat(enchant.getAttributes().getSpellPower()).isEqualTo(35);
		assertThat(enchant.getAttributes().getStamina()).isEqualTo(20);
	}

	@Test
	@DisplayName("Token is read correctly")
	void tokenIsCorrect() {
		Optional<TradedItem> optionalTradedItem = underTest.getTradedItem(31101);

		assertThat(optionalTradedItem).isPresent();

		TradedItem tradedItem = optionalTradedItem.orElseThrow();

		assertThat(tradedItem.getId()).isEqualTo(31101);
		assertThat(tradedItem.getName()).isEqualTo("Pauldrons of the Forgotten Conqueror");
		assertThat(tradedItem.getItemType()).isEqualTo(TOKEN);
		assertThat(tradedItem.getRarity()).isEqualTo(EPIC);
		assertThat(tradedItem.getBinding()).isEqualTo(BINDS_ON_PICK_UP);
		assertThat(tradedItem.isUnique()).isFalse();
		assertThat(tradedItem.getItemLevel()).isEqualTo(70);
		assertThat(tradedItem.getRestriction().getLevel()).isEqualTo(70);
		assertThat(tradedItem.getRestriction().getPhase()).isEqualTo(TBC_P3);
		assertThat(tradedItem.getRestriction().getCharacterClasses()).hasSameElementsAs(List.of(PALADIN, PRIEST, WARLOCK));
	}
}
