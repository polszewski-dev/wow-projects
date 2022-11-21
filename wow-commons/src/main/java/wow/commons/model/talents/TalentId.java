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

	SUPPRESSION("Suppression"),
	IMPROVED_CORRUPTION("Improved Corruption"),
	IMPROVED_CURSE_OF_WEAKNESS("Improved Curse of Weakness"),
	IMPROVED_DRAIN_SOUL("Improved Drain Soul"),
	IMPROVED_LIFE_TAP("Improved Life Tap"),
	SOUL_SIPHON("Soul Siphon"),
	IMPROVED_CURSE_OF_AGONY("Improved Curse of Agony"),
	FEL_CONCENTRATION("Fel Concentration"),
	AMPLIFY_CURSE("Amplify Curse"),
	GRIM_REACH("Grim Reach"),
	NIGHTFALL("Nightfall"),
	EMPOWERED_CORRUPTION("Empowered Corruption"),
	SHADOW_EMBRACE("Shadow Embrace"),
	SIPHON_LIFE("Siphon Life"),
	CURSE_OF_EXHAUSTION("Curse of Exhaustion"),
	SHADOW_MASTERY("Shadow Mastery"),
	CONTAGION("Contagion"),
	DARK_PACT("Dark Pact"),
	IMPROVED_HOWL_OF_TERROR("Improved Howl of Terror"),
	MALEDICTION("Malediction"),
	UNSTABLE_AFFLICTION("Unstable Affliction"),

	// demonology

	IMPROVED_HEALTHSTONE("Improved Healthstone"),
	IMPROVED_IMP("Improved Imp"),
	DEMONIC_EMBRACE("Demonic Embrace"),
	IMPROVED_HEALTH_FUNNEL("Improved Health Funnel"),
	IMPROVED_VOIDWALKER("Improved Voidwalker"),
	FEL_INTELLECT("Fel Intellect"),
	IMPROVED_SUCCUBUS("Improved Succubus"),
	FEL_DOMINATION("Fel Domination"),
	FEL_STAMINA("Fel Stamina"),
	DEMONIC_AEGIS("Demonic Aegis"),
	MASTER_SUMMONER("Master Summoner"),
	UNHOLY_POWER("Unholy Power"),
	IMPROVED_ENSLAVE_DEMON("Improved Enslave Demon"),
	DEMONIC_SACRIFICE("Demonic Sacrifice"),
	MASTER_CONJUROR("Master Conjuror"),
	MANA_FEED("Mana Feed"),
	MASTER_DEMONOLOGIST("Master Demonologist"),
	DEMONIC_RESILIENCE("Demonic Resilience"),
	SOUL_LINK("Soul Link"),
	DEMONIC_KNOWLEDGE("Demonic Knowledge"),
	DEMONIC_TACTICS("Demonic Tactics"),
	SUMMON_FELGUARD("Summon Felguard"),

	// destruction

	IMPROVED_SHADOW_BOLT("Improved Shadow Bolt"),
	CATACLYSM("Cataclysm"),
	BANE("Bane"),
	AFTERMATH("Aftermath"),
	IMPROVED_FIREBOLT("Improved Firebolt"),
	IMPROVED_LASH_OF_PAIN("Improved Lash of Pain"),
	DEVASTATION("Devastation"),
	SHADOWBURN("Shadowburn"),
	INTENSITY("Intensity"),
	DESTRUCTIVE_REACH("Destructive Reach"),
	IMPROVED_SEARING_PAIN("Improved Searing Pain"),
	PYROCLASM("Pyroclasm"),
	IMPROVED_IMMOLATE("Improved Immolate"),
	RUIN("Ruin"),
	NETHER_PROTECTION("Nether Protection"),
	EMBERSTORM("Emberstorm"),
	BACKLASH("Backlash"),
	CONFLAGRATE("Conflagrate"),
	SOUL_LEECH("Soul Leech"),
	SHADOW_AND_FLAME("Shadow and Flame"),
	SHADOWFURY("Shadowfury");

	private final String name;

	public static TalentId parse(String name) {
		return EnumUtil.parse(name, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
