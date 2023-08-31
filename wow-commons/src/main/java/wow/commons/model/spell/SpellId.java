package wow.commons.model.spell;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2020-10-01
 */
@AllArgsConstructor
@Getter
public enum SpellId {
	TRINKET("Trinket"),
	POTION("Potion"),

	SHOOT("Shoot"),

	// racial

	BLOOD_FURY("Blood Fury"),

	// warlock

	AMPLIFY_CURSE("Amplify Curse"),
	CORRUPTION("Corruption"),
	CURSE_OF_AGONY("Curse of Agony"),
	CURSE_OF_DOOM("Curse of Doom"),
	CURSE_OF_EXHAUSTION("Curse of Exhaustion"),
	CURSE_OF_RECKLESSNESS("Curse of Recklessness"),
	CURSE_OF_SHADOW("Curse of Shadow"),
	CURSE_OF_TONGUES("Curse of Tongues"),
	CURSE_OF_WEAKNESS("Curse of Weakness"),
	CURSE_OF_THE_ELEMENTS("Curse of the Elements"),
	DARK_PACT("Dark Pact"),
	DEATH_COIL("Death Coil"),
	DRAIN_LIFE("Drain Life"),
	DRAIN_MANA("Drain Mana"),
	DRAIN_SOUL("Drain Soul"),
	FEAR("Fear"),
	HOWL_OF_TERROR("Howl of Terror"),
	LIFE_TAP("Life Tap"),
	SEED_OF_CORRUPTION("Seed of Corruption"),
	SEED_OF_CORRUPTION_DIRECT("Seed of Corruption (direct)"),
	SIPHON_LIFE("Siphon Life"),
	UNSTABLE_AFFLICTION("Unstable Affliction"),

	BANISH("Banish"),
	CREATE_FIRESTONE("Create Firestone"),
	CREATE_FIRESTONE_GREATER("Create Firestone (Greater)"),
	CREATE_FIRESTONE_LESSER("Create Firestone (Lesser)"),
	CREATE_FIRESTONE_MAJOR("Create Firestone (Major)"),
	CREATE_HEALTHSTONE("Create Healthstone"),
	CREATE_HEALTHSTONE_GREATER("Create Healthstone (Greater)"),
	CREATE_HEALTHSTONE_LESSER("Create Healthstone (Lesser)"),
	CREATE_HEALTHSTONE_MAJOR("Create Healthstone (Major)"),
	CREATE_HEALTHSTONE_MINOR("Create Healthstone (Minor)"),
	CREATE_SOULSTONE("Create Soulstone"),
	CREATE_SOULSTONE_GREATER("Create Soulstone (Greater)"),
	CREATE_SOULSTONE_LESSER("Create Soulstone (Lesser)"),
	CREATE_SOULSTONE_MAJOR("Create Soulstone (Major)"),
	CREATE_SOULSTONE_MINOR("Create Soulstone (Minor)"),
	CREATE_SPELLSTONE("Create Spellstone"),
	CREATE_SPELLSTONE_GREATER("Create Spellstone (Greater)"),
	CREATE_SPELLSTONE_MAJOR("Create Spellstone (Major)"),
	DEMON_ARMOR("Demon Armor"),
	DEMON_SKIN("Demon Skin"),
	DEMONIC_SACRIFICE("Demonic Sacrifice"),
	DETECT_GREATER_INVISIBILITY("Detect Greater Invisibility"),
	DETECT_INVISIBILITY("Detect Invisibility"),
	DETECT_LESSER_INVISIBILITY("Detect Lesser Invisibility"),
	ENSLAVE_DEMON("Enslave Demon"),
	EYE_OF_KILROGG("Eye of Kilrogg"),
	FEL_ARMOR("Fel Armor"),
	FEL_DOMINATION("Fel Domination"),
	HEALTH_FUNNEL("Health Funnel"),
	INFERNO("Inferno"),
	RITUAL_OF_DOOM("Ritual of Doom"),
	RITUAL_OF_SOULS("Ritual of Souls"),
	RITUAL_OF_SUMMONING("Ritual of Summoning"),
	SENSE_DEMONS("Sense Demons"),
	SHADOW_WARD("Shadow Ward"),
	SOUL_LINK("Soul Link"),
	SOULSHATTER("Soulshatter"),
	SUMMON_DREADSTEED("Summon Dreadsteed"),
	SUMMON_FELGUARD("Summon Felguard"),
	SUMMON_FELHUNTER("Summon Felhunter"),
	SUMMON_FELSTEED("Summon Felsteed"),
	SUMMON_IMP("Summon Imp"),
	SUMMON_INCUBUS("Summon Incubus"),
	SUMMON_SUCCUBUS("Summon Succubus"),
	SUMMON_VOIDWALKER("Summon Voidwalker"),
	UNENDING_BREATH("Unending Breath"),

	CONFLAGRATE("Conflagrate"),
	HELLFIRE("Hellfire"),
	IMMOLATE("Immolate"),
	INCINERATE("Incinerate"),
	RAIN_OF_FIRE("Rain of Fire"),
	SEARING_PAIN("Searing Pain"),
	SHADOW_BOLT("Shadow Bolt"),
	SHADOWBURN("Shadowburn"),
	SHADOWFURY("Shadowfury"),
	SOUL_FIRE("Soul Fire"),

	// warlock's pet

	FIREBOLT("Firebolt"),
	BLOOD_PACT("Blood Pact"),
	FIRE_SHIELD("Fire Shield"),
	PHASE_SHIFT("Phase Shift"),
	TORMENT("Torment"),
	CONSUME_SHADOWS("Consume Shadows"),
	SACRIFICE("Sacrifice"),
	SOOTHING_KISS("Soothing Kiss"),
	LASH_OF_PAIN("Lash of Pain"),
	LESSER_INVISIBILITY("Lesser Invisibility"),
	SEDUCTION("Seduction"),

	// mage

	BLINK("Blink"),
	COUNTERSPELL("Counterspell"),
	FIREBALL("Fireball"),
	FLAMESTRIKE("Flamestrike"),
	FROSTBOLT("Frostbolt"),
	POLYMORPH("Polymorph"),

	// priest

	CONSUME_MAGIC("Consume Magic"),
	DISPEL_MAGIC("Dispel Magic"),
	DIVINE_SPIRIT("Divine Spirit"),
	ELUNE_S_GRACE("Elune's Grace"),
	FEAR_WARD("Fear Ward"),
	FEEDBACK("Feedback"),
	INNER_FIRE("Inner Fire"),
	INNER_FOCUS("Inner Focus"),
	LEVITATE("Levitate"),
	MANA_BURN("Mana Burn"),
	MASS_DISPEL("Mass Dispel"),
	PAIN_SUPPRESSION("Pain Suppression"),
	POWER_INFUSION("Power Infusion"),
	POWER_WORD_FORTITUDE("Power Word: Fortitude"),
	POWER_WORD_SHIELD("Power Word: Shield"),
	PRAYER_OF_FORTITUDE("Prayer of Fortitude"),
	PRAYER_OF_SPIRIT("Prayer of Spirit"),
	SHACKLE_UNDEAD("Shackle Undead"),
	STARSHARDS("Starshards"),
	SYMBOL_OF_HOPE("Symbol of Hope"),

	ABOLISH_DISEASE("Abolish Disease"),
	BINDING_HEAL("Binding Heal"),
	CHASTISE("Chastise"),
	CIRCLE_OF_HEALING("Circle of Healing"),
	CURE_DISEASE("Cure Disease"),
	DESPERATE_PRAYER("Desperate Prayer"),
	FLASH_HEAL("Flash Heal"),
	GREATER_HEAL("Greater Heal"),
	HEAL("Heal"),
	HOLY_FIRE("Holy Fire"),
	HOLY_NOVA("Holy Nova"),
	IMPROVED_PRAYER_OF_HEALING("Improved Prayer of Healing"),
	LESSER_HEAL("Lesser Heal"),
	LIGHTWELL("Lightwell"),
	PRAYER_OF_HEALING("Prayer of Healing"),
	PRAYER_OF_MENDING("Prayer of Mending"),
	RENEW("Renew"),
	RESURRECTION("Resurrection"),
	SMITE("Smite"),

	DEVOURING_PLAGUE("Devouring Plague"),
	FADE("Fade"),
	HEX_OF_WEAKNESS("Hex of Weakness"),
	MIND_BLAST("Mind Blast"),
	MIND_CONTROL("Mind Control"),
	MIND_FLAY("Mind Flay"),
	MIND_SOOTHE("Mind Soothe"),
	MIND_VISION("Mind Vision"),
	PRAYER_OF_SHADOW_PROTECTION("Prayer of Shadow Protection"),
	PSYCHIC_SCREAM("Psychic Scream"),
	SHADOW_PROTECTION("Shadow Protection"),
	SHADOW_WORD_DEATH("Shadow Word: Death"),
	SHADOW_WORD_PAIN("Shadow Word: Pain"),
	SHADOWFIEND("Shadowfiend"),
	SHADOWFORM("Shadowform"),
	SHADOWGUARD("Shadowguard"),
	SILENCE("Silence"),
	TOUCH_OF_WEAKNESS("Touch of Weakness"),
	VAMPIRIC_EMBRACE("Vampiric Embrace"),
	VAMPIRIC_TOUCH("Vampiric Touch"),

	// rogue

	BLIND("Blind"),
	KICK("Kick"),

	// hunter

	CONCUSSIVE_SHOT("Concussive Shot"),
	FEIGN_DEATH("Feign Death"),

	// paladin

	HOLY_LIGHT("Holy Light"),

	// warrior

	INTIMIDATING_SHOUT("Intimidating Shout"),

	;

	private final String name;

	public static SpellId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static SpellId tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
