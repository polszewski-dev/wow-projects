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
public enum AbilityId {
	SHOOT("Shoot"),

	// racial

	CANNIBALIZE("Cannibalize"),
	WILL_OF_THE_FORSAKEN("Will of the Forsaken"),
	BLOOD_FURY("Blood Fury"),
	BERSERKING("Berserking"),
	WAR_STOMP("War Stomp"),
	ARCANE_TORRENT("Arcane Torrent"),
	MANA_TAP("Mana Tap"),
	PERCEPTION("Perception"),
	ESCAPE_ARTIST("Escape Artist"),
	STONEFORM("Stoneform"),
	FIND_TREASURE("Find Treasure"),
	SHADOWMELD("Shadowmeld"),
	GIFT_OF_THE_NAARU("Gift of the Naaru"),

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
	SUFFERING("Suffering"),
	SOOTHING_KISS("Soothing Kiss"),
	LASH_OF_PAIN("Lash of Pain"),
	LESSER_INVISIBILITY("Lesser Invisibility"),
	SEDUCTION("Seduction"),

	// mage

	ARCANE_BLAST("Arcane Blast"),
	ARCANE_BRILLIANCE("Arcane Brilliance"),
	ARCANE_EXPLOSION("Arcane Explosion"),
	ARCANE_INTELLECT("Arcane Intellect"),
	ARCANE_MISSILES("Arcane Missiles"),
	BLAST_WEAVE("Blast Weave"),
	BLIZZARD("Blizzard"),
	BLINK("Blink"),
	COUNTERSPELL("Counterspell"),
	EVOCATION("Evocation"),
	FIREBALL("Fireball"),
	FIRE_BLAST("Fire Blast"),
	FLAMESTRIKE("Flamestrike"),
	FROSTBOLT("Frostbolt"),
	ICE_BLOCK("Ice Block"),
	MAGE_ARMOR("Mage Armor"),
	MANA_SHIELD("Mana Shield"),
	POLYMORPH("Polymorph"),
	PRESENCE_OF_MIND("Presence of Mind"),
	SCORCH("Scorch"),

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

	WEAKENED_SOUL("Weakened Soul"),

	// druid

	FAERIE_FIRE("Faerie Fire"),
	FLIGHT_FORM("Flight Form"),
	HEALING_TOUCH("Healing Touch"),
	MANGLE("Mangle"),
	REGROWTH("Regrowth"),
	REJUVENATION("Rejuvenation"),
	STARFIRE("Starfire"),
	SWIFT_FLIGHT_FORM("Swift Flight Form"),
	TRANQUILITY("Tranquility"),
	WRATH("Wrath"),

	// rogue

	BLIND("Blind"),
	DEADLY_POISON("Deadly Poison"),
	INSTANT_POISON("Instant Poison"),
	KICK("Kick"),
	SLICE_AND_DICE("Slice and Dice"),

	BACKSTAB("Backstab"),
	EVISCERATE("Eviscerate"),
	HEMORRHAGE("Hemorrhage"),
	RUPTURE("Rupture"),
	SINISTER_STRIKE("Sinister Strike"),

	// hunter

	AIMED_SHOT("Aimed Shot"),
	ARCANE_SHOT("Arcane Shot"),
	CONCUSSIVE_SHOT("Concussive Shot"),
	FEIGN_DEATH("Feign Death"),
	MULTI_SHOT("Multi-Shot"),
	RAPID_FIRE("Rapid Fire"),
	SERPENT_STING("Serpent Sting"),
	STEADY_SHOT("Steady Shot"),

	// shaman

	CHAIN_LIGHTNING("Chain Lightning"),
	FROST_SHOCK("Frost Shock"),
	HEALING_STREAM_TOTEM("Healing Stream Totem"),
	HEALING_WAVE("Healing Wave"),
	LESSER_HEALING_WAVE("Lesser Healing Wave"),
	LIGHTNING_BOLT("Lightning Bolt"),
	LIGHTNING_SHIELD("Lightning Shield"),
	MANA_SPRING_TOTEM("Mana Spring Totem"),
	STORMSTRIKE("Stormstrike"),

	// paladin

	CLEANSE("Cleanse"),
	FLASH_OF_LIGHT("Flash of Light"),
	HAMMER_OF_JUSTICE("Hammer of Justice"),
	HOLY_LIGHT("Holy Light"),
	JUDGEMENT_OF_LIGHT("Judgement of Light"),
	LAY_ON_HANDS("Lay on Hands"),

	// warrior

	BATTLE_SHOUT("Battle Shout"),
	BLOODTHIRST("Bloodthirst"),
	CHALLENGING_SHOUT("Challenging Shout"),
	DEVASTATE("Devastate"),
	HAMSTRING("Hamstring"),
	HEROIC_STRIKE("Heroic Strike"),
	INTERCEPT("Intercept"),
	INTIMIDATING_SHOUT("Intimidating Shout"),
	MORTAL_STRIKE("Mortal Strike"),
	REVENGE("Revenge"),
	SHIELD_SLAM("Shield Slam"),
	SUNDER_ARMOR("Sunder Armor"),
	TAUNT("Taunt"),
	WHIRLWIND("Whirlwind"),

	// activated

	ABACUS_OF_VIOLENT_ODDS("Abacus of Violent Odds"),
	ABYSS_SHARD("Abyss Shard"),
	ADAMANTINE_FIGURINE("Adamantine Figurine"),
	ADMIRALS_HAT("Admiral's Hat"),
	AEGIS_OF_PRESERVATION("Aegis of Preservation"),
	AMULET_OF_THE_MOON("Amulet of the Moon"),
	ANCIENT_AQIR_ARTIFACT("Ancient Aqir Artifact"),
	ANCIENT_CORNERSTONE_GRIMOIRE("Ancient Cornerstone Grimoire"),
	ANCIENT_CRYSTAL_TALISMAN("Ancient Crystal Talisman"),
	ANCIENT_DRAENEI_ARCANE_RELIC("Ancient Draenei Arcane Relic"),
	ANCIENT_DRAENEI_WAR_TALISMAN("Ancient Draenei War Talisman"),
	ANKH_OF_LIFE("Ankh of Life"),
	ANNIHILATOR_HOLO_GOGS("Annihilator Holo-Gogs"),
	AQUAMARINE_PENDANT_OF_THE_WARRIOR("Aquamarine Pendant of the Warrior"),
	ARCANE_INFUSED_GEM("Arcane Infused Gem"),
	ARCANE_ORB("Arcane Orb"),
	ARCANISTS_STONE("Arcanist's Stone"),
	ARCANITE_DRAGONLING("Arcanite Dragonling"),
	ARENA_GRAND_MASTER("Arena Grand Master"),
	ARGUSSIAN_COMPASS("Argussian Compass"),
	ATIESH_GREATSTAFF_OF_THE_GUARDIAN("Atiesh, Greatstaff of the Guardian"),
	AUSLESES_LIGHT_CHANNELER("Auslese's Light Channeler"),
	BADGE_OF_THE_SWARMGUARD("Badge of the Swarmguard"),
	BALEBREW_CHARM("Balebrew Charm"),
	BANGLE_OF_ENDLESS_BLESSINGS("Bangle of Endless Blessings"),
	BAROV_PEASANT_CALLER("Barov Peasant Caller"),
	BATTLEMASTERS_ALACRITY("Battlemaster's Alacrity"),
	BATTLEMASTERS_AUDACITY("Battlemaster's Audacity"),
	BERSERKERS_CALL("Berserker's Call"),
	BLAZING_EMBLEM("Blazing Emblem"),
	BLESSED_MEDALLION_OF_KARABOR("Blessed Medallion of Karabor"),
	BLESSED_PRAYER_BEADS("Blessed Prayer Beads"),
	BLOODLUST_BROOCH("Bloodlust Brooch"),
	BLOODSAIL_ADMIRALS_HAT("Bloodsail Admiral's Hat"),
	BOUQUET_OF_RED_ROSES("Bouquet of Red Roses"),
	BRAIDED_ETERNIUM_CHAIN("Braided Eternium Chain"),
	BRIGHTBREW_CHARM("Brightbrew Charm"),
	BROOCH_OF_THE_IMMORTAL_KING("Brooch of the Immortal King"),
	BURST_OF_KNOWLEDGE("Burst of Knowledge"),
	CANNONBALL_RUNNER("Cannonball Runner"),
	CAPTAINS_BADGE("Captain's Badge"),
	CELESTIAL_ORB("Celestial Orb"),
	CHAIN_OF_THE_TWILIGHT_OWL("Chain of the Twilight Owl"),
	CHAINED_ESSENCE_OF_ERANIKUS("Chained Essence of Eranikus"),
	CHARM_OF_ALACRITY("Charm of Alacrity"),
	CIRCLE_OF_FLAME("Circle of Flame"),
	CITRINE_PENDANT_OF_GOLDEN_HEALING("Citrine Pendant of Golden Healing"),
	CLOAK_OF_FIRE("Cloak of Fire"),
	COLD_BASILISK_EYE("Cold Basilisk Eye"),
	CORE_OF_ARKELOS("Core of Ar'kelos"),
	CORENS_LUCKY_COIN("Coren's Lucky Coin"),
	CRYSTALFORGED_TRINKET("Crystalforged Trinket"),
	DABIRIS_ENIGMA("Dabiri's Enigma"),
	DARK_IRON_SMOKING_PIPE("Dark Iron Smoking Pipe"),
	DEFENDER_OF_THE_TIMBERMAW("Defender of the Timbermaw"),
	DEFILERS_TALISMAN("Defiler's Talisman"),
	DESTRUCTION_HOLO_GOGS("Destruction Holo-gogs"),
	DEVILSAUR_EYE("Devilsaur Eye"),
	DEVILSAUR_TOOTH("Devilsaur Tooth"),
	DIAMOND_FLASK("Diamond Flask"),
	DIMENSIONAL_RIPPER_AREA_52("Dimensional Ripper - Area 52"),
	DIMENSIONAL_RIPPER_EVERLOOK("Dimensional Ripper - Everlook"),
	DIREBREW_HOPS("Direbrew Hops"),
	DON_CARLOS_FAMOUS_HAT("Don Carlos' Famous Hat"),
	DRACONIC_INFUSED_EMBLEM("Draconic Infused Emblem"),
	EARRING_OF_SOULFUL_MEDITATION("Earring of Soulful Meditation"),
	EARTHSTRIKE("Earthstrike"),
	ELECTROMAGNETIC_GIGAFLUX_REACTIVATOR("Electromagnetic Gigaflux Reactivator"),
	EMBRACE_OF_THE_DAWN("Embrace of the Dawn"),
	EMPTY_MUG_OF_DIREBREW("Empty Mug of Direbrew"),
	ENAMORED_WATER_SPIRIT("Enamored Water Spirit"),
	ENCHANTED_AZSHARITE_FELBANE_DAGGER("Enchanted Azsharite Felbane Dagger"),
	ENCHANTED_AZSHARITE_FELBANE_STAFF("Enchanted Azsharite Felbane Staff"),
	ENCHANTED_MOONSTALKER_CLOAK("Enchanted Moonstalker Cloak"),
	ENERGY_CLOAK("Energy Cloak"),
	EVERGLOW_LANTERN("Everglow Lantern"),
	EYE_OF_DIMINUTION("Eye of Diminution"),
	EYE_OF_MOAM("Eye of Moam"),
	EYE_OF_THE_DEAD("Eye of the Dead"),
	EYE_OF_THE_NIGHT("Eye of the Night"),
	FETISH_OF_CHITINOUS_SPIKES("Fetish of Chitinous Spikes"),
	FETISH_OF_THE_FALLEN("Fetish of the Fallen"),
	FETISH_OF_THE_SAND_REAVER("Fetish of the Sand Reaver"),
	FIGURINE_BLACK_DIAMOND_CRAB("Figurine - Black Diamond Crab"),
	FIGURINE_BLACK_PEARL_PANTHER("Figurine - Black Pearl Panther"),
	FIGURINE_CRIMSON_SERPENT("Figurine - Crimson Serpent"),
	FIGURINE_DARK_IRON_SCORPID("Figurine - Dark Iron Scorpid"),
	FIGURINE_DAWNSTONE_CRAB("Figurine - Dawnstone Crab"),
	FIGURINE_EMERALD_OWL("Figurine - Emerald Owl"),
	FIGURINE_EMPYREAN_TORTOISE("Figurine - Empyrean Tortoise"),
	FIGURINE_FELSTEEL_BOAR("Figurine - Felsteel Boar"),
	FIGURINE_GOLDEN_HARE("Figurine - Golden Hare"),
	FIGURINE_JADE_OWL("Figurine - Jade Owl"),
	FIGURINE_KHORIUM_BOAR("Figurine - Khorium Boar"),
	FIGURINE_LIVING_RUBY_SERPENT("Figurine - Living Ruby Serpent"),
	FIGURINE_NIGHTSEYE_PANTHER("Figurine - Nightseye Panther"),
	FIGURINE_OF_THE_COLOSSUS("Figurine of the Colossus"),
	FIGURINE_RUBY_SERPENT("Figurine - Ruby Serpent"),
	FIGURINE_SEASPRAY_ALBATROSS("Figurine - Seaspray Albatross"),
	FIGURINE_SHADOWSONG_PANTHER("Figurine - Shadowsong Panther"),
	FIGURINE_TALASITE_OWL("Figurine - Talasite Owl"),
	FIGURINE_TRUESILVER_BOAR("Figurine - Truesilver Boar"),
	FIGURINE_TRUESILVER_CRAB("Figurine - Truesilver Crab"),
	FIRE_RUBY("Fire Ruby"),
	FROSTWOLF_INSIGNIA_RANK_1("Frostwolf Insignia Rank 1"),
	FROSTWOLF_INSIGNIA_RANK_6("Frostwolf Insignia Rank 6"),
	FURBOLG_MEDICINE_POUCH("Furbolg Medicine Pouch"),
	GLIMMERING_MITHRIL_INSIGNIA("Glimmering Mithril Insignia"),
	GLIMMERING_NAARU_SLIVER("Glimmering Naaru Sliver"),
	GLYPH_OF_DEFLECTION("Glyph of Deflection"),
	GNOMISH_MIND_CONTROL_CAP("Gnomish Mind Control Cap"),
	GNOMISH_POULTRYIZER("Gnomish Poultryizer"),
	GNOMISH_ROCKET_BOOTS("Gnomish Rocket Boots"),
	GNOMISH_UNIVERSAL_REMOTE("Gnomish Universal Remote"),
	GOBLIN_CONSTRUCTION_HELMET("Goblin Construction Helmet"),
	GOBLIN_ROCKET_BOOTS("Goblin Rocket Boots"),
	GOBLIN_ROCKET_HELMET("Goblin Rocket Helmet"),
	GOBLIN_ROCKET_LAUNCHER("Goblin Rocket Launcher"),
	GRACE_OF_EARTH("Grace of Earth"),
	GYROFREEZE_ICE_REFLECTOR("Gyrofreeze Ice Reflector"),
	HEADMASTERS_CHARGE("Headmaster's Charge"),
	HEART_OF_NOXXION("Heart of Noxxion"),
	HEART_OF_THE_SCALE("Heart of the Scale"),
	HEAVENLY_INSPIRATION("Heavenly Inspiration"),
	HEAVY_GOLDEN_NECKLACE_OF_BATTLE("Heavy Golden Necklace of Battle"),
	HEX_SHRUNKEN_HEAD("Hex Shrunken Head"),
	HIBERNATION_CRYSTAL("Hibernation Crystal"),
	HOOK_OF_THE_MASTER_ANGLER("Hook of the Master Angler"),
	HYPER_RADIANT_FLAME_REFLECTOR("Hyper-Radiant Flame Reflector"),
	ICON_OF_UNYIELDING_COURAGE("Icon of Unyielding Courage"),
	INSIGNIA_OF_THE_ALLIANCE("Insignia of the Alliance"),
	JADE_PENDANT_OF_BLASTING("Jade Pendant of Blasting"),
	JANGTHRAZE_THE_PROTECTOR("Jang'thraze the Protector"),
	JOM_GABBAR("Jom Gabbar"),
	KISS_OF_THE_SPIDER("Kiss of the Spider"),
	LAPIDIS_TANKARD_OF_TIDESIPPE("Lapidis Tankard of Tidesippe"),
	LEI_OF_LILIES("Lei of Lilies"),
	LEY_ORB("Ley Orb"),
	LIFEGIVING_GEM("Lifegiving Gem"),
	LIFESTONE("Lifestone"),
	LINKENS_BOOMERANG("Linken's Boomerang"),
	LIVING_RUBY_PENDANT("Living Ruby Pendant"),
	LOATHEBS_REFLECTION("Loatheb's Reflection"),
	LOWER_CITY_PRAYERBOOK("Lower City Prayerbook"),
	LUFFA("Luffa"),
	MAJOR_RECOMBOBULATOR("Major Recombobulator"),
	MARK_OF_RESOLUTION("Mark of Resolution"),
	MARK_OF_THE_DRAGON_LORD("Mark of the Dragon Lord"),
	MARLIS_EYE("Mar'li's Eye"),
	MEDALLION_OF_THE_ALLIANCE("Medallion of the Alliance"),
	MIND_QUICKENING_GEM("Mind Quickening Gem"),
	MINOR_RECOMBOBULATOR("Minor Recombobulator"),
	MIRRENS_DRINKING_HAT("Mirren's Drinking Hat"),
	MITHRIL_MECHANICAL_DRAGONLING("Mithril Mechanical Dragonling"),
	MOROES_LUCKY_POCKET_WATCH("Moroes' Lucky Pocket Watch"),
	NAT_PAGLES_BROKEN_REEL("Nat Pagle's Broken Reel"),
	NATURAL_ALIGNMENT_CRYSTAL("Natural Alignment Crystal"),
	NIFTY_STOPWATCH("Nifty Stopwatch"),
	NIGH_INVULNERABILITY_BELT("Nigh Invulnerability Belt"),
	OCULUS_OF_THE_HIDDEN_EYE("Oculus of the Hidden Eye"),
	OGRE_MAULERS_BADGE("Ogre Mauler's Badge"),
	ORB_OF_DARORAHIL("Orb of Dar'Orahil"),
	ORB_OF_DECEPTION("Orb of Deception"),
	ORB_OF_SORANRUK("Orb of Soran'ruk"),
	OSHUGUN_RELIC("Oshu'gun Relic"),
	OVERSEERS_BADGE("Overseer's Badge"),
	PARACHUTE_CLOAK("Parachute Cloak"),
	PENDANT_OF_THE_AGATE_SHIELD("Pendant of the Agate Shield"),
	PENDANT_OF_FROZEN_FLAME("Pendant of Frozen Flame"),
	PENDANT_OF_SHADOWS_END("Pendant of Shadow's End"),
	PENDANT_OF_THAWING("Pendant of Thawing"),
	PENDANT_OF_THE_NULL_RUNE("Pendant of the Null Rune"),
	PENDANT_OF_THE_VIOLET_EYE("Pendant of the Violet Eye"),
	PENDANT_OF_WITHERING("Pendant of Withering"),
	PETRIFIED_SCARAB("Petrified Scarab"),
	PICCOLO_OF_THE_FLAMING_FIRE("Piccolo of the Flaming Fire"),
	PRISMCHARM("Prismcharm"),
	RAGGED_JOHNS_NEVERENDING_CUP("Ragged John's Neverending Cup"),
	RAMSTEINS_LIGHTNING_BOLTS("Ramstein's Lightning Bolts"),
	REGAL_PROTECTORATE("Regal Protectorate"),
	RIBBON_OF_SACRIFICE("Ribbon of Sacrifice"),
	RING_OF_SAVIORS("Ring of Saviors"),
	ROBE_OF_THE_ARCHMAGE("Robe of the Archmage"),
	ROBE_OF_THE_VOID("Robe of the Void"),
	ROBES_OF_INSIGHT("Robes of Insight"),
	ROCKET_BOOTS_XTREME_LITE("Rocket Boots Xtreme Lite"),
	RUBY_SLIPPERS("Ruby Slippers"),
	RUNE_OF_METAMORPHOSIS("Rune of Metamorphosis"),
	RUNED_FUNGALCAP("Runed Fungalcap"),
	SANCTIFIED_ORB("Sanctified Orb"),
	SCARAB_BROOCH("Scarab Brooch"),
	SCARAB_OF_DISPLACEMENT("Scarab of Displacement"),
	SCOURGEBANE("Scourgebane"),
	SCROLLS_OF_BLINDING_LIGHT("Scrolls of Blinding Light"),
	SCRYERS_BLOODGEM("Scryer's Bloodgem"),
	SECOND_WIND("Second Wind"),
	SHADOWMOON_INSIGNIA("Shadowmoon Insignia"),
	SHARD_OF_THE_FALLEN_STAR("Shard of the Fallen Star"),
	SHIFTING_NAARU_SLIVER("Shifting Naaru Sliver"),
	SIGNET_OF_EXPERTISE("Signet of Expertise"),
	SIX_DEMON_BAG("Six Demon Bag"),
	SKULL_OF_IMPENDING_DOOM("Skull of Impending Doom"),
	SKYGUARDS_DRAPE("Skyguard's Drape"),
	SLAYERS_CREST("Slayer's Crest"),
	SMOKEYS_LIGHTER("Smokey's Lighter"),
	SMOLDERWEBS_EYE("Smolderweb's Eye"),
	SPIDER_BELT("Spider Belt"),
	SPIRE_OF_THE_STONESHAPER("Spire of the Stoneshaper"),
	SPYGLASS_OF_THE_HIDDEN_FLEET("Spyglass of the Hidden Fleet"),
	STAFF_OF_CONJURING("Staff of Conjuring"),
	STAFF_OF_THE_PURIFIER("Staff of the Purifier"),
	STARKILLERS_BAUBLE("Starkiller's Bauble"),
	STEELY_NAARU_SLIVER("Steely Naaru Sliver"),
	STORMPIKE_INSIGNIA_RANK_1("Stormpike Insignia Rank 1"),
	STORMPIKE_INSIGNIA_RANK_6("Stormpike Insignia Rank 6"),
	SUNWELL_ORB("Sunwell Orb"),
	TALISMAN_OF_ASCENDANCE("Talisman of Ascendance"),
	TALISMAN_OF_EPHEMERAL_POWER("Talisman of Ephemeral Power"),
	TALISMAN_OF_THE_ALLIANCE("Talisman of the Alliance"),
	TALISMAN_OF_THE_HORDE("Talisman of the Horde"),
	TEROKKAR_TABLET_OF_PRECISION("Terokkar Tablet of Precision"),
	TEROKKAR_TABLET_OF_VIM("Terokkar Tablet of Vim"),
	THE_BLACK_BOOK("The Black Book"),
	THE_BURROWERS_SHELL("The Burrower's Shell"),
	THE_HORSEMANS_BLADE("The Horseman's Blade"),
	THE_RESTRAINED_ESSENCE_OF_SAPPHIRON("The Restrained Essence of Sapphiron"),
	THE_SKULL_OF_GULDAN("The Skull of Gul'dan"),
	THICK_FELSTEEL_NECKLACE("Thick Felsteel Necklace"),
	THUNDERBREWS_BOOT_FLASK("Thunderbrew's Boot Flask"),
	TIDAL_CHARM("Tidal Charm"),
	TIME_LOST_FIGURINE("Time-Lost Figurine"),
	TIMELAPSE_SHARD("Timelapse Shard"),
	TINY_VOODOO_MASK("Tiny Voodoo Mask"),
	TOME_OF_DIABOLIC_REMEDY("Tome of Diabolic Remedy"),
	TORCH_OF_HOLY_FLAME("Torch of Holy Flame"),
	ULTRA_FLASH_SHADOW_REFLECTOR("Ultra-Flash Shadow Reflector"),
	ULTRA_SPECTROPIC_DETECTION_GOGGLES("Ultra-Spectropic Detection Goggles"),
	ULTRASAFE_TRANSPORTER_GADGETZAN("Ultrasafe Transporter: Gadgetzan"),
	ULTRASAFE_TRANSPORTER_TOSHLEYS_STATION("Ultrasafe Transporter: Toshley's Station"),
	VANQUISHED_TENTACLE_OF_CTHUN("Vanquished Tentacle of C'Thun"),
	VEILDUST_MEDICINE_BAG("Veildust Medicine Bag"),
	VENOMOUS_TOTEM("Venomous Totem"),
	VIAL_OF_THE_SUNWELL("Vial of the Sunwell"),
	WARMTH_OF_FORGIVENESS("Warmth of Forgiveness"),
	WARP_SCARAB_BROOCH("Warp-Scarab Brooch"),
	WEATHER_BEATEN_FISHING_HAT("Weather-Beaten Fishing Hat"),
	X_52_ROCKET_HELMET("X-52 Rocket Helmet"),
	ZANDALARIAN_HERO_BADGE("Zandalarian Hero Badge"),
	ZANDALARIAN_HERO_CHARM("Zandalarian Hero Charm"),
	ZANDALARIAN_HERO_MEDALLION("Zandalarian Hero Medallion"),

	DESTRUCTION_POTION("Destruction Potion"),

	;

	private final String name;

	public static AbilityId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static AbilityId tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
