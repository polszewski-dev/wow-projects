package wow.commons.model.talents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2020-10-01
 */
@AllArgsConstructor
@Getter
public enum TalentId {
	// affliction

	SUPPRESSION("Suppression", 1),
	IMPROVED_CORRUPTION("Improved Corruption", 2),
	IMPROVED_CURSE_OF_WEAKNESS("Improved Curse of Weakness", 3),
	IMPROVED_DRAIN_SOUL("Improved Drain Soul", 4),
	IMPROVED_LIFE_TAP("Improved Life Tap", 5),
	SOUL_SIPHON("Soul Siphon", 6),
	IMPROVED_CURSE_OF_AGONY("Improved Curse of Agony", 7),
	FEL_CONCENTRATION("Fel Concentration", 8),
	AMPLIFY_CURSE("Amplify Curse", 9),
	GRIM_REACH("Grim Reach", 10),
	NIGHTFALL("Nightfall", 11),
	EMPOWERED_CORRUPTION("Empowered Corruption", 12),
	SHADOW_EMBRACE("Shadow Embrace", 13),
	SIPHON_LIFE("Siphon Life", 14),
	CURSE_OF_EXHAUSTION("Curse of Exhaustion", 15),
	SHADOW_MASTERY("Shadow Mastery", 16),
	CONTAGION("Contagion", 17),
	DARK_PACT("Dark Pact", 18),
	IMPROVED_HOWL_OF_TERROR("Improved Howl of Terror", 19),
	MALEDICTION("Malediction", 20),
	UNSTABLE_AFFLICTION("Unstable Affliction", 21),

	// demonology

	IMPROVED_HEALTHSTONE("Improved Healthstone", 22),
	IMPROVED_IMP("Improved Imp", 23),
	DEMONIC_EMBRACE("Demonic Embrace", 24),
	IMPROVED_HEALTH_FUNNEL("Improved Health Funnel", 25),
	IMPROVED_VOIDWALKER("Improved Voidwalker", 26),
	FEL_INTELLECT("Fel Intellect", 27),
	IMPROVED_SUCCUBUS("Improved Succubus", 28),
	FEL_DOMINATION("Fel Domination", 29),
	FEL_STAMINA("Fel Stamina", 30),
	DEMONIC_AEGIS("Demonic Aegis", 31),
	MASTER_SUMMONER("Master Summoner", 32),
	UNHOLY_POWER("Unholy Power", 33),
	IMPROVED_ENSLAVE_DEMON("Improved Enslave Demon", 34),
	DEMONIC_SACRIFICE("Demonic Sacrifice", 35),
	MASTER_CONJUROR("Master Conjuror", 36),
	MANA_FEED("Mana Feed", 37),
	MASTER_DEMONOLOGIST("Master Demonologist", 38),
	DEMONIC_RESILIENCE("Demonic Resilience", 39),
	SOUL_LINK("Soul Link", 40),
	DEMONIC_KNOWLEDGE("Demonic Knowledge", 41),
	DEMONIC_TACTICS("Demonic Tactics", 42),
	SUMMON_FELGUARD("Summon Felguard", 43),

	// destruction

	IMPROVED_SHADOW_BOLT("Improved Shadow Bolt", 44),
	CATACLYSM("Cataclysm", 45),
	BANE("Bane", 46),
	AFTERMATH("Aftermath", 47),
	IMPROVED_FIREBOLT("Improved Firebolt", 48),
	IMPROVED_LASH_OF_PAIN("Improved Lash of Pain", 49),
	DEVASTATION("Devastation", 50),
	SHADOWBURN("Shadowburn", 51),
	INTENSITY("Intensity", 52),
	DESTRUCTIVE_REACH("Destructive Reach", 53),
	IMPROVED_SEARING_PAIN("Improved Searing Pain", 54),
	PYROCLASM("Pyroclasm", 55),
	IMPROVED_IMMOLATE("Improved Immolate", 56),
	RUIN("Ruin", 57),
	NETHER_PROTECTION("Nether Protection", 58),
	EMBERSTORM("Emberstorm", 59),
	BACKLASH("Backlash", 60),
	CONFLAGRATE("Conflagrate", 61),
	SOUL_LEECH("Soul Leech", 62),
	SHADOW_AND_FLAME("Shadow and Flame", 63),
	SHADOWFURY("Shadowfury", 64);

	private final String name;
	private final int talentCalculatorPosition;

	public static TalentId parse(String name) {
		return EnumUtil.parse(name, values(), x -> x.name);
	}

	public static TalentId fromTalentCalculatorPosition(int pos) {
		for (TalentId value : values()) {
			if (value.talentCalculatorPosition == pos) {
				return value;
			}
		}
		throw new IllegalArgumentException("Unknown talent#: " + pos);
	}

	@Override
	public String toString() {
		return name;
	}
}
