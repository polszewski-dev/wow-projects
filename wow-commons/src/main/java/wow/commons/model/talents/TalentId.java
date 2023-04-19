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
	IMPROVED_DRAIN_LIFE("Improved Drain Life"),
	SOUL_SIPHON("Soul Siphon"),
	IMPROVED_CURSE_OF_AGONY("Improved Curse of Agony"),
	FEL_CONCENTRATION("Fel Concentration"),
	AMPLIFY_CURSE("Amplify Curse"),
	GRIM_REACH("Grim Reach"),
	NIGHTFALL("Nightfall"),
	IMPROVED_DRAIN_MANA("Improved Drain Mana"),
	EMPOWERED_CORRUPTION("Empowered Corruption"),
	SHADOW_EMBRACE("Shadow Embrace"),
	SIPHON_LIFE("Siphon Life"),
	CURSE_OF_EXHAUSTION("Curse of Exhaustion"),
	IMPROVED_CURSE_OF_EXHAUSTION("Improved Curse of Exhaustion"),
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
	IMPROVED_FIRESTONE("Improved Firestone"),
	MASTER_CONJUROR("Master Conjuror"),
	MANA_FEED("Mana Feed"),
	MASTER_DEMONOLOGIST("Master Demonologist"),
	DEMONIC_RESILIENCE("Demonic Resilience"),
	SOUL_LINK("Soul Link"),
	IMPROVED_SPELLSTONE("Improved Spellstone"),
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
	SHADOWFURY("Shadowfury"),

	// discipline

	UNBREAKABLE_WILL("Unbreakable Will"),
	WAND_SPECIALIZATION("Wand Specialization"),
	SILENT_RESOLVE("Silent Resolve"),
	IMPROVED_POWER_WORD_FORTITUDE("Improved Power Word: Fortitude"),
	IMPROVED_POWER_WORD_SHIELD("Improved Power Word: Shield"),
	MARTYRDOM("Martyrdom"),
	INNER_FOCUS("Inner Focus"),
	MEDITATION("Meditation"),
	IMPROVED_INNER_FIRE("Improved Inner Fire"),
	MENTAL_AGILITY("Mental Agility"),
	IMPROVED_MANA_BURN("Improved Mana Burn"),
	MENTAL_STRENGTH("Mental Strength"),
	DIVINE_SPIRIT("Divine Spirit"),
	FORCE_OF_WILL("Force of Will"),
	POWER_INFUSION("Power Infusion"),
	ABSOLUTION("Absolution"),
	IMPROVED_DIVINE_SPIRIT("Improved Divine Spirit"),
	FOCUSED_POWER("Focused Power"),
	FOCUSED_WILL("Focused Will"),
	REFLECTIVE_SHIELD("Reflective Shield"),
	ENLIGHTENMENT("Enlightenment"),
	PAIN_SUPPRESSION("Pain Suppression"),

	// holy

	HEALING_FOCUS("Healing Focus"),
	IMPROVED_RENEW("Improved Renew"),
	HOLY_SPECIALIZATION("Holy Specialization"),
	SPELL_WARDING("Spell Warding"),
	DIVINE_FURY("Divine Fury"),
	HOLY_NOVA("Holy Nova"),
	BLESSED_RECOVERY("Blessed Recovery"),
	INSPIRATION("Inspiration"),
	HOLY_REACH("Holy Reach"),
	IMPROVED_HEALING("Improved Healing"),
	SEARING_LIGHT("Searing Light"),
	IMPROVED_PRAYER_OF_HEALING("Improved Prayer of Healing"),
	SPIRIT_OF_REDEMPTION("Spirit of Redemption"),
	SPIRITUAL_GUIDANCE("Spiritual Guidance"),
	SPIRITUAL_HEALING("Spiritual Healing"),
	LIGHTWELL("Lightwell"),
	HEALING_PRAYERS("Healing Prayers"),
	SURGE_OF_LIGHT("Surge of Light"),
	HOLY_CONCENTRATION("Holy Concentration"),
	BLESSED_RESILIENCE("Blessed Resilience"),
	EMPOWERED_HEALING("Empowered Healing"),
	CIRCLE_OF_HEALING("Circle of Healing"),

	// shadow

	SPIRIT_TAP("Spirit Tap"),
	BLACKOUT("Blackout"),
	SHADOW_AFFINITY("Shadow Affinity"),
	IMPROVED_SHADOW_WORD_PAIN("Improved Shadow Word: Pain"),
	SHADOW_FOCUS("Shadow Focus"),
	IMPROVED_PSYCHIC_SCREAM("Improved Psychic Scream"),
	IMPROVED_MIND_BLAST("Improved Mind Blast"),
	MIND_FLAY("Mind Flay"),
	IMPROVED_FADE("Improved Fade"),
	SHADOW_REACH("Shadow Reach"),
	SHADOW_WEAVING("Shadow Weaving"),
	SILENCE("Silence"),
	VAMPIRIC_EMBRACE("Vampiric Embrace"),
	IMPROVED_VAMPIRIC_EMBRACE("Improved Vampiric Embrace"),
	DARKNESS("Darkness"),
	SHADOWFORM("Shadowform"),
	FOCUSED_MIND("Focused Mind"),
	SHADOW_RESILIENCE("Shadow Resilience"),
	SHADOW_POWER("Shadow Power"),
	MISERY("Misery"),
	VAMPIRIC_TOUCH("Vampiric Touch");

	private final String name;

	public static TalentId parse(String name) {
		return EnumUtil.parse(name, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
