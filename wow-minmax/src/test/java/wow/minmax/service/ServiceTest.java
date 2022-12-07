package wow.minmax.service;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.BuildId;
import wow.commons.model.character.CharacterProfession;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.professions.Profession;
import wow.commons.model.pve.Phase;
import wow.commons.repository.ItemDataRepository;
import wow.minmax.WowMinMaxTestConfig;
import wow.minmax.model.PlayerProfile;
import wow.minmax.repository.PlayerProfileRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

	@MockBean
	PlayerProfileRepository playerProfileRepository;

	@Autowired
	PlayerProfileService playerProfileService;

	PlayerProfile newPlayerProfile() {
		var professions = List.of(
				new CharacterProfession(Profession.TAILORING, 375, null),
				new CharacterProfession(Profession.ENCHANTING, 375, null)
		);
		return playerProfileService.createTemporaryPlayerProfile(
				UUID.randomUUID(),
				"test",
				WARLOCK,
				ORC,
				70,
				BuildId.DESTRO_SHADOW,
				professions,
				UNDEAD,
				PHASE
		);
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
		return itemDataRepository.getGem(name, PHASE);
	}

	EquippableItem getItem(String name) {
		return new EquippableItem(itemDataRepository.getItem(name, PHASE).orElseThrow());
	}

	Enchant getEnchant(String name) {
		return itemDataRepository.getEnchant(name, PHASE).orElseThrow();
	}

	static final Phase PHASE = Phase.TBC_P5;

	PlayerProfile playerProfile;

	@BeforeEach
	void setup() {
		playerProfile = newPlayerProfile();
	}
}
