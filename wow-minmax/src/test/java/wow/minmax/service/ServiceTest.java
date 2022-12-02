package wow.minmax.service;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.commons.model.buffs.Buff;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.*;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.professions.Profession;
import wow.commons.model.pve.Phase;
import wow.commons.model.spells.SpellId;
import wow.commons.repository.ItemDataRepository;
import wow.commons.repository.SpellDataRepository;
import wow.commons.util.TalentCalculatorUtil;
import wow.minmax.WowMinMaxTestConfig;
import wow.minmax.model.PlayerProfile;

import java.util.*;
import java.util.stream.Collectors;

import static wow.commons.model.character.BuffSetId.*;
import static wow.commons.model.character.CharacterClass.WARLOCK;
import static wow.commons.model.character.CreatureType.UNDEAD;
import static wow.commons.model.character.Race.ORC;

/**
 * User: POlszewski
 * Date: 2022-11-20
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowMinMaxTestConfig.class)
@TestPropertySource("classpath:application.properties")
abstract class ServiceTest {
	static final Comparator<Double> ROUNDED_DOWN = Comparator.comparingDouble(Double::intValue);
	static final Offset<Double> PRECISION = Offset.offset(0.01);

	@Autowired
	ItemDataRepository itemDataRepository;

	@Autowired
	SpellDataRepository spellDataRepository;

	PlayerProfile getPlayerProfile(BuildId buildId) {
		Build build = getBuild(buildId);
		CharacterProfessions characterProfessions = new CharacterProfessions(List.of(
				new CharacterProfession(Profession.TAILORING, 375, null),
				new CharacterProfession(Profession.ENCHANTING, 375, null)
		));
		return new PlayerProfile(
				UUID.randomUUID(),
				"test",
				new CharacterInfo(WARLOCK, ORC, 70, build, characterProfessions, Phase.TBC_P5),
				UNDEAD
		);
	}

	private Build getBuild(BuildId buildId) {
		if (buildId == null) {
			return Build.EMPTY;
		}

		String talentLink = "https://legacy-wow.com/tbc-talents/warlock-talents/?tal=0000000000000000000002050130133200100000000555000512210013030250";

		return new Build(
				BuildId.DESTRO_SHADOW,
				talentLink,
				TalentCalculatorUtil.parseFromLink(talentLink, spellDataRepository),
				PveRole.CASTER_DPS,
				spellDataRepository.getSpellHighestRank(SpellId.SHADOW_BOLT, 70).orElseThrow(),
				List.of(),
				null,
				getBuffSets()
		);
	}

	Map<BuffSetId, List<Buff>> getBuffSets() {
		Map<BuffSetId, List<String>> map = Map.of(
				SELF_BUFFS, List.of("Fel Armor", "Touch of Shadow"),
				PARTY_BUFFS, List.of("Arcane Brilliance", "Gift of the Wild", "Greater Blessing of Kings"),
				RAID_BUFFS, List.of("Curse of the Elements", "Wrath of Air Totem", "Totem of Wrath"),
				WORLD_BUFFS, List.of(),
				CONSUMES, List.of("Well Fed (sp)", "Brilliant Wizard Oil", "Flask of Pure Death")
		);

		return map.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> spellDataRepository.getBuffs(e.getValue())));
	}

	Equipment getEquipment() {
		Equipment equipment = new Equipment();

		Gem metaGem = getGem("Chaotic Skyfire Diamond").orElseThrow();
		Gem hitSpGem = getGem("Veiled Pyrestone").orElseThrow();
		Gem critSpGem = getGem("Potent Pyrestone").orElseThrow();
		Gem hasteSpGem = getGem("Reckless Pyrestone").orElseThrow();
		Gem hasteGem = getGem("Quick Lionseye").orElseThrow();
		Gem staSpGem = getGem("Glowing Shadowsong Amethyst").orElseThrow();

		equipment.set(getItem("Dark Conjuror's Collar").enchant(getEnchant("Glyph of Power")).gem(metaGem, staSpGem));
		equipment.set(getItem("Amulet of Unfettered Magics"));
		equipment.set(getItem("Mantle of the Malefic").enchant(getEnchant("Greater Inscription of the Orb")).gem(staSpGem, hasteSpGem));
		equipment.set(getItem("Tattered Cape of Antonidas").enchant(getEnchant("Enchant Cloak - Subtlety")).gem(critSpGem));
		equipment.set(getItem("Sunfire Robe").enchant(getEnchant("Enchant Chest - Exceptional Stats")).gem(hasteSpGem, hasteSpGem, hasteSpGem));
		equipment.set(getItem("Bracers of the Malefic").enchant(getEnchant("Enchant Bracer – Spellpower")).gem(hasteSpGem));
		equipment.set(getItem("Sunfire Handwraps").enchant(getEnchant("Enchant Gloves - Major Spellpower")).gem(hasteSpGem, hasteSpGem));
		equipment.set(getItem("Belt of the Malefic").gem(hasteSpGem));
		equipment.set(getItem("Leggings of Calamity").enchant(getEnchant("Runic Spellthread")).gem(hasteSpGem, hasteSpGem, hasteGem));
		equipment.set(getItem("Boots of the Malefic").enchant(getEnchant("Enchant Boots - Boar's Speed")).gem(critSpGem));
		equipment.set(getItem("Ring of Omnipotence").enchant(getEnchant("Enchant Ring – Spellpower")), ItemSlot.FINGER_1);
		equipment.set(getItem("Loop of Forged Power").enchant(getEnchant("Enchant Ring – Spellpower")), ItemSlot.FINGER_2);
		equipment.set(getItem("The Skull of Gul'dan"), ItemSlot.TRINKET_1);
		equipment.set(getItem("Shifting Naaru Sliver"), ItemSlot.TRINKET_2);
		equipment.set(getItem("Sunflare").enchant(getEnchant("Enchant Weapon – Soulfrost")));
		equipment.set(getItem("Chronicle of Dark Secrets"));
		equipment.set(getItem("Wand of the Demonsoul").gem(hitSpGem));

		return equipment;
	}

	Optional<Gem> getGem(String name) {
		return itemDataRepository.getGem(name);
	}

	EquippableItem getItem(String name) {
		return new EquippableItem(itemDataRepository.getItem(name).orElseThrow());
	}

	Enchant getEnchant(String name) {
		return itemDataRepository.getEnchant(name).orElseThrow();
	}
}
