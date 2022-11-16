package wow.minmax.service;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.pve.Phase;
import wow.commons.model.spells.Spell;
import wow.commons.model.unit.CharacterInfo;
import wow.commons.repository.ItemDataRepository;
import wow.commons.util.AttributeEvaluator;
import wow.commons.util.Snapshot;
import wow.minmax.WowMinMaxTestConfig;
import wow.minmax.model.BuildIds;
import wow.minmax.model.PlayerProfile;
import wow.minmax.repository.BuildRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spells.SpellId.SHADOW_BOLT;
import static wow.commons.model.unit.Build.BuffSetId.*;
import static wow.commons.model.unit.CharacterClass.WARLOCK;
import static wow.commons.model.unit.CreatureType.UNDEAD;
import static wow.commons.model.unit.Race.ORC;

/**
 * User: POlszewski
 * Date: 2022-11-12
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowMinMaxTestConfig.class)
class CalculationServiceTest {
	@Autowired
	ItemDataRepository itemDataRepository;

	@Autowired
	SpellService spellService;

	@Autowired
	BuildRepository buildRepository;

	@Autowired
	CalculationService underTest;

	@Test
	@DisplayName("No talents, no buffs, no items")
	void noTalentsNoBuffsNoItems() {
		PlayerProfile playerProfile = getPlayerProfile(BuildIds.NONE);

		playerProfile.setEquipment(new Equipment());
		playerProfile.setBuffs(List.of());

		Spell spell = spellService.getSpell(SHADOW_BOLT, playerProfile.getLevel());
		Attributes stats = getCurrentStats(playerProfile);
		Snapshot snapshot = underTest.getSnapshot(playerProfile, spell, stats);

		assertThat(snapshot.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(78);
		assertThat(snapshot.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(130);
		assertThat(snapshot.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(114);

		assertThat(snapshot.getTotalCrit()).isEqualTo(3.29, PRECISION);
		assertThat(snapshot.getTotalHit()).isEqualTo(0, PRECISION);
		assertThat(snapshot.getTotalHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getSp()).usingComparator(ROUNDED_DOWN).isEqualTo(0);
		assertThat(snapshot.getSpMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getHitChance()).isEqualTo(0.83, PRECISION);
		assertThat(snapshot.getCritChance()).isEqualTo(0.03, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(1.5, PRECISION);
		assertThat(snapshot.getHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getDirectDamageDoneMultiplier()).isEqualTo(1, PRECISION);
		assertThat(snapshot.getDotDamageDoneMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getCastTime().getSeconds()).isEqualTo(3, PRECISION);
		assertThat(snapshot.getGcd().getSeconds()).isEqualTo(1.5, PRECISION);
		assertThat(snapshot.getEffectiveCastTime().getSeconds()).isEqualTo(3, PRECISION);

		assertThat(snapshot.getSpellCoeffDirect()).isEqualTo(0.8571, PRECISION);
		assertThat(snapshot.getSpellCoeffDoT()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(420);
	}

	@Test
	@DisplayName("Has talents, no buffs, no items")
	void hasTalentsNoBuffsNoItems() {
		PlayerProfile playerProfile = getPlayerProfile(BuildIds.DESTRO_SHADOW_BUILD);

		playerProfile.setEquipment(new Equipment());
		playerProfile.setBuffs(List.of());

		Spell spell = playerProfile.getDamagingSpell();
		Attributes stats = getCurrentStats(playerProfile);
		Snapshot snapshot = underTest.getSnapshot(playerProfile, spell, stats);

		assertThat(snapshot.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(89);
		assertThat(snapshot.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(130);
		assertThat(snapshot.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(108);

		assertThat(snapshot.getTotalCrit()).isEqualTo(11.29, PRECISION);
		assertThat(snapshot.getTotalHit()).isEqualTo(0, PRECISION);
		assertThat(snapshot.getTotalHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getSp()).usingComparator(ROUNDED_DOWN).isEqualTo(0);
		assertThat(snapshot.getSpMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getHitChance()).isEqualTo(0.83, PRECISION);
		assertThat(snapshot.getCritChance()).isEqualTo(0.11, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(2.76, PRECISION);
		assertThat(snapshot.getHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getDirectDamageDoneMultiplier()).isEqualTo(1, PRECISION);
		assertThat(snapshot.getDotDamageDoneMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getCastTime().getSeconds()).isEqualTo(2.5, PRECISION);
		assertThat(snapshot.getGcd().getSeconds()).isEqualTo(1.5, PRECISION);
		assertThat(snapshot.getEffectiveCastTime().getSeconds()).isEqualTo(2.5, PRECISION);

		assertThat(snapshot.getSpellCoeffDirect()).isEqualTo(1.0571, PRECISION);
		assertThat(snapshot.getSpellCoeffDoT()).isEqualTo(0.2, PRECISION);

		assertThat(snapshot.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
	}

	@Test
	@DisplayName("Has talents, has buffs, no items")
	void hasTalentsHasBuffsNoItems() {
		PlayerProfile playerProfile = getPlayerProfile(BuildIds.DESTRO_SHADOW_BUILD);

		playerProfile.setEquipment(new Equipment());
		playerProfile.setBuffs(playerProfile.getBuild().getBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES));

		Spell spell = playerProfile.getDamagingSpell();
		Attributes stats = getCurrentStats(playerProfile);
		Snapshot snapshot = underTest.getSnapshot(playerProfile, spell, stats);

		assertThat(snapshot.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(115);
		assertThat(snapshot.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(202);
		assertThat(snapshot.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(155);

		assertThat(snapshot.getTotalCrit()).isEqualTo(15.82, PRECISION);
		assertThat(snapshot.getTotalHit()).isEqualTo(3, PRECISION);
		assertThat(snapshot.getTotalHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getSp()).usingComparator(ROUNDED_DOWN).isEqualTo(370);
		assertThat(snapshot.getSpMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getHitChance()).isEqualTo(0.86, PRECISION);
		assertThat(snapshot.getCritChance()).isEqualTo(0.15, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(2.74, PRECISION);
		assertThat(snapshot.getHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getDirectDamageDoneMultiplier()).isEqualTo(1.25, PRECISION);
		assertThat(snapshot.getDotDamageDoneMultiplier()).isEqualTo(1.25, PRECISION);

		assertThat(snapshot.getCastTime().getSeconds()).isEqualTo(2.5, PRECISION);
		assertThat(snapshot.getGcd().getSeconds()).isEqualTo(1.5, PRECISION);
		assertThat(snapshot.getEffectiveCastTime().getSeconds()).isEqualTo(2.5, PRECISION);

		assertThat(snapshot.getSpellCoeffDirect()).isEqualTo(1.0571, PRECISION);
		assertThat(snapshot.getSpellCoeffDoT()).isEqualTo(0.2, PRECISION);

		assertThat(snapshot.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
	}

	@Test
	@DisplayName("Has talents, has buffs, has items")
	void hasTalentsHasBuffsHasItems() {
		PlayerProfile playerProfile = getPlayerProfile(BuildIds.DESTRO_SHADOW_BUILD);

		playerProfile.setEquipment(getEquipment());
		playerProfile.setBuffs(playerProfile.getBuild().getBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES));

		Spell spell = playerProfile.getDamagingSpell();
		Attributes stats = getCurrentStats(playerProfile);
		Snapshot snapshot = underTest.getSnapshot(playerProfile, spell, stats);

		assertThat(snapshot.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(733);
		assertThat(snapshot.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(643);
		assertThat(snapshot.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(161);

		assertThat(snapshot.getTotalCrit()).isEqualTo(36.55, PRECISION);
		assertThat(snapshot.getTotalHit()).isEqualTo(17.02, PRECISION);
		assertThat(snapshot.getTotalHaste()).isEqualTo(31.10, PRECISION);

		assertThat(snapshot.getSp()).usingComparator(ROUNDED_DOWN).isEqualTo(1713);
		assertThat(snapshot.getSpMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getHitChance()).isEqualTo(0.99, PRECISION);
		assertThat(snapshot.getCritChance()).isEqualTo(0.36, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(2.76, PRECISION);
		assertThat(snapshot.getHaste()).isEqualTo(0.3110, PRECISION);

		assertThat(snapshot.getDirectDamageDoneMultiplier()).isEqualTo(1.37, PRECISION);
		assertThat(snapshot.getDotDamageDoneMultiplier()).isEqualTo(1.37, PRECISION);

		assertThat(snapshot.getCastTime().getSeconds()).isEqualTo(1.91, PRECISION);
		assertThat(snapshot.getGcd().getSeconds()).isEqualTo(1.14, PRECISION);
		assertThat(snapshot.getEffectiveCastTime().getSeconds()).isEqualTo(1.91, PRECISION);

		assertThat(snapshot.getSpellCoeffDirect()).isEqualTo(1.0571, PRECISION);
		assertThat(snapshot.getSpellCoeffDoT()).isEqualTo(0.2, PRECISION);

		assertThat(snapshot.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
	}

	static final Comparator<Double> ROUNDED_DOWN = Comparator.comparingDouble(Double::intValue);
	static final Offset<Double> PRECISION = Offset.offset(0.01);

	private PlayerProfile getPlayerProfile(String buildId) {
		return new PlayerProfile(
				UUID.randomUUID(),
				"test",
				new CharacterInfo(WARLOCK, ORC, 70, List.of()),
				UNDEAD,
				Phase.TBC_P5,
				buildRepository.getBuild(buildId, 70).orElseThrow()
		);
	}
	private Attributes getCurrentStats(PlayerProfile playerProfile) {
		return AttributeEvaluator.of()
				.addAttributes(playerProfile)
				.solveAllLeaveAbilities();
	}

	private Equipment getEquipment() {
		Equipment equipment = new Equipment();

		Gem metaGem = getGem("Chaotic Skyfire Diamond").orElseThrow();
		Gem orangeGem = getGem("Reckless Pyrestone").orElseThrow();
		Gem violetGem = getGem("Glowing Shadowsong Amethyst").orElseThrow();

		equipment.set(getItem("Dark Conjuror's Collar").enchant(getEnchant("Glyph of Power")).gem(metaGem, violetGem));
		equipment.set(getItem("Pendant of Sunfire").gem(orangeGem));
		equipment.set(getItem("Mantle of the Malefic").enchant(getEnchant("Greater Inscription of the Orb")).gem(violetGem, orangeGem));
		equipment.set(getItem("Tattered Cape of Antonidas").enchant(getEnchant("Enchant Cloak - Subtlety")).gem(orangeGem));
		equipment.set(getItem("Sunfire Robe").enchant(getEnchant("Enchant Chest - Exceptional Stats")).gem(orangeGem, orangeGem, orangeGem));
		equipment.set(getItem("Bracers of the Malefic").enchant(getEnchant("Enchant Bracer – Spellpower")).gem(orangeGem));
		equipment.set(getItem("Sunfire Handwraps").enchant(getEnchant("Enchant Gloves - Major Spellpower")).gem(orangeGem, orangeGem));
		equipment.set(getItem("Belt of the Malefic").gem(orangeGem));
		equipment.set(getItem("Leggings of Calamity").enchant(getEnchant("Runic Spellthread")).gem(orangeGem, orangeGem, orangeGem));
		equipment.set(getItem("Boots of the Malefic").enchant(getEnchant("Enchant Boots - Boar's Speed")).gem(orangeGem));
		equipment.set(getItem("Ring of Omnipotence").enchant(getEnchant("Enchant Ring – Spellpower")), ItemSlot.FINGER_1);
		equipment.set(getItem("Loop of Forged Power").enchant(getEnchant("Enchant Ring – Spellpower")), ItemSlot.FINGER_2);
		equipment.set(getItem("The Skull of Gul'dan"), ItemSlot.TRINKET_1);
		equipment.set(getItem("Shifting Naaru Sliver"), ItemSlot.TRINKET_2);
		equipment.set(getItem("Grand Magister's Staff of Torrents").enchant(getEnchant("Enchant Weapon – Soulfrost")).gem(orangeGem, orangeGem, orangeGem));
		equipment.set(getItem("Wand of the Demonsoul").gem(orangeGem));

		return equipment;
	}

	private Optional<Gem> getGem(String name) {
		return itemDataRepository.getGem(name);
	}

	private EquippableItem getItem(String name) {
		return new EquippableItem(itemDataRepository.getItem(name).orElseThrow());
	}

	private Enchant getEnchant(String name) {
		return itemDataRepository.getEnchant(name).orElseThrow();
	}
}