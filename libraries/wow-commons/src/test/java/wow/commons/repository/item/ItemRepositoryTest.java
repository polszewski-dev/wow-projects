package wow.commons.repository.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemId;
import wow.commons.model.item.ItemSource;
import wow.commons.model.item.SocketType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.constant.AttributeConditions.*;
import static wow.commons.model.attribute.AttributeId.*;
import static wow.commons.model.categorization.ArmorSubType.CLOTH;
import static wow.commons.model.categorization.Binding.BINDS_ON_PICK_UP;
import static wow.commons.model.categorization.ItemRarity.EPIC;
import static wow.commons.model.categorization.ItemType.CHEST;
import static wow.commons.model.profession.ProfessionId.TAILORING;
import static wow.commons.model.pve.PhaseId.TBC_P5;
import static wow.commons.model.pve.PhaseId.VANILLA_P6;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
class ItemRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	protected ItemRepository itemRepository;

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
		var itemId = ItemId.of(34364);

		var item = itemRepository.getItem(itemId, TBC_P5).orElseThrow();

		assertThat(item.getId()).isEqualTo(itemId);
		assertThat(item.getName()).isEqualTo("Sunfire Robe");
		assertThat(item.getRarity()).isEqualTo(EPIC);
		assertThat(item.getBinding()).isEqualTo(BINDS_ON_PICK_UP);
		assertThat(item.isUnique()).isFalse();
		assertThat(item.getItemLevel()).isEqualTo(159);
		assertThat(item.getTimeRestriction()).isEqualTo(TimeRestriction.of(TBC_P5));
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
		var itemId = ItemId.of(34364);

		var item = itemRepository.getItem(itemId, TBC_P5).orElseThrow();

		var socketSpecification = item.getSocketSpecification();

		assertThat(socketSpecification.getSocketCount()).isEqualTo(3);
		assertThat(socketSpecification.getSocketType(0)).isEqualTo(SocketType.RED);
		assertThat(socketSpecification.getSocketType(1)).isEqualTo(SocketType.RED);
		assertThat(socketSpecification.getSocketType(2)).isEqualTo(SocketType.RED);

		assertEffect(socketSpecification.socketBonus(), POWER, 5, SPELL, "+5 Spell Damage and Healing", new ItemSource(item));
	}

	@Test
	void itemStats() {
		var itemId = ItemId.of(34364);

		var item = itemRepository.getItem(itemId, TBC_P5).orElseThrow();
		var source = new ItemSource(item);

		assertThat(item.getEffects()).hasSize(6);
		assertEffect(item.getEffects().get(0), ARMOR, 266, "266 Armor", source);
		assertEffect(item.getEffects().get(1), STAMINA, 36, "+36 Stamina", source);
		assertEffect(item.getEffects().get(2), INTELLECT, 34, "+34 Intellect", source);
		assertEffect(item.getEffects().get(3), CRIT_RATING, 40, SPELL, "Equip: Improves spell critical strike rating by 40.", source);
		assertEffect(item.getEffects().get(4), HASTE_RATING, 40, SPELL, "Equip: Improves spell haste rating by 40.", source);
		assertEffect(item.getEffects().get(5), POWER, 71, SPELL, "Equip: Increases damage and healing done by magical spells and effects by up to 71.", source);
	}

	@Test
	void itemWithRandomEnchantStats() {
		var item = itemRepository.getItem("Drakestone of Shadow Wrath", VANILLA_P6).orElseThrow();
		var source = new ItemSource(item);

		assertThat(item.getEffects()).hasSize(2);
		assertEffect(item.getEffects().get(0), POWER, 7, SPELL, "Equip: Increases damage and healing done by magical spells and effects by up to 7.", source);
		assertEffect(item.getEffects().get(1), POWER, 21, AttributeCondition.and(SPELL_DAMAGE, SHADOW), "+21 Shadow Spell Damage", source);
	}

	@Test
	void itemSet() {
		var itemId = ItemId.of(24262);

		var item = itemRepository.getItem(itemId, TBC_P5).orElseThrow();
		var itemSet = item.getItemSet();

		assertThat(itemSet.getName()).isEqualTo("Spellstrike Infusion");
		assertThat(itemSet.getRequiredProfession()).isEqualTo(TAILORING);
		assertThat(itemSet.getItemSetBonuses()).hasSize(1);

		var bonus = itemSet.getItemSetBonuses().getFirst();

		assertThat(bonus.numPieces()).isEqualTo(2);
		assertEffect(bonus.bonusEffect(), 224266, "Gives a chance when your harmful spells land to increase the damage of your spells and effects by 92 for 10 sec. (Proc chance: 5%)");
	}

	@Test
	void getItemSet() {
		var itemSet = itemRepository.getItemSet("Spellstrike Infusion", TBC_P5).orElseThrow();

		assertThat(itemSet.getName()).isEqualTo("Spellstrike Infusion");
		assertThat(itemSet.getItemSetBonuses()).hasSize(1);
		assertThat(itemSet.getItemSetBonuses().getFirst().numPieces()).isEqualTo(2);
		assertThat(itemSet.getItemSetBonuses().getFirst().bonusEffect().getDescription().tooltip()).isEqualTo(
				"Gives a chance when your harmful spells land to increase the damage of your spells and effects by 92 for 10 sec. (Proc chance: 5%)"
		);
		assertThat(itemSet.getRequiredProfession()).isEqualTo(TAILORING);
		assertThat(itemSet.getPieces().stream().map(Item::getName)).hasSameElementsAs(List.of(
				"Spellstrike Hood",
				"Spellstrike Pants"
		));
	}
}
