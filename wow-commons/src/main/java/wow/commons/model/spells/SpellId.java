package wow.commons.model.spells;

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
	CURSE_OF_THE_ELEMENTS("Curse of the Elements"),
	CURSE_OF_DOOM("Curse of Doom"),
	CURSE_OF_EXHAUSTION("Curse of Exhaustion"),
	CURSE_OF_SHADOWS("Curse of Shadow"),
	CURSE_OF_WEAKNESS("Curse of Weakness"),
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

	CREATE_HEALTHSTONE("Create Healthstone"),
	DEMON_ARMOR("Demon Armor"),
	DEMONIC_SACRIFICE("Demonic Sacrifice"),
	ENSLAVE_DEMON("Enslave Demon"),
	FEL_ARMOR("Fel Armor"),
	FEL_DOMINATION("Fel Domination"),
	HEALTH_FUNNEL("Health Funnel"),
	SHADOW_WARD("Shadow Ward"),
	SUMMON_IMP("Summon Imp"),
	SUMMON_VOIDWALKER("Summon Voidwalker"),
	SUMMON_SUCCUBUS("Summon Succubus"),
	SUMMON_FELHUNTER("Summon Felhunter"),
	SUMMON_FELGUARD("Summon Felguard"),

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
	SOUL_LINK("Soul Link"),

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

	POWER_WORD_FORTITUDE("Power Word: Fortitude"),
	PRAYER_OF_FORTITUDE("Prayer of Fortitude"),
	POWER_WORD_SHIELD("Power Word: Shield"),
	DISPEL_MAGIC("Dispel Magic"),
	CURE_DISEASE("Cure Disease"),
	ABOLISH_DISEASE("Abolish Disease"),
	MASS_DISPEL("Mass Dispel"),
	INNER_FIRE("Inner Fire"),
	MANA_BURN("Mana Burn"),
	SMITE("Smite"),

	BINDING_HEAL("Binding Heal"),
	MIND_CONTROL("Mind Control"),
	PSYCHIC_SCREAM("Psychic Scream"),

	DEVOURING_PLAGUE("Devouring Plague"),
	MIND_BLAST("Mind Blast"),
	MIND_FLAY("Mind Flay"),
	SHADOWFORM("Shadowform"),
	SHADOW_WORD_DEATH("Shadow Word: Death"),
	SHADOW_WORD_PAIN("Shadow Word: Pain"),
	VAMPIRIC_EMBRACE("Vampiric Embrace"),
	VAMPIRIC_TOUCH("Vampiric Touch"),

	STARSHARDS("Starshards"),

	// rogue

	BLIND("Blind"),
	FADE("Fade"),
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
