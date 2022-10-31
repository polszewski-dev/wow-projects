"use strict";

const getSpellIcon = spellName => {
	if (!spellName || !spellIconBySpellName[spellName]) {console.log('icon', spellName)}
	return `https://wow.zamimg.com/images/wow/icons/small/${spellIconBySpellName[spellName]}.jpg`
}

const getItemIcon = item => {
	return `https://wow.zamimg.com/images/wow/icons/small/${item.icon}.jpg`
}

const getEnchantIcon = enchant => {
	let icon = enchantIconByEnchantId[enchant.id]
	if (!icon) {
		icon = 'spell_holy_greaterheal'
	}
	return `https://wow.zamimg.com/images/wow/icons/small/${icon}.jpg`
}

const getGemIcon = gem => {
	return `https://wow.zamimg.com/images/wow/icons/small/${gem.icon}.jpg`
}

const getBuffIcon = buff => {
	if (!buffIconByBuffId[buff.id]) {console.log('icon',buff.name)}
	return `https://wow.zamimg.com/images/wow/icons/small/${buffIconByBuffId[buff.id]}.jpg`
}

const spellIconBySpellName = {
	"Shadow Bolt": "spell_shadow_shadowbolt",
	"Curse of Doom": "spell_shadow_auraofdarkness",
	"Curse of Agony": "spell_shadow_curseofsargeras",
	"Corruption": "spell_shadow_abominationexplosion",
	"Immolate": "spell_fire_immolation",
	"Shadowburn": "spell_shadow_scourgebuild",
	"Seed of Corruption": "spell_shadow_seedofdestruction",
	"Seed of Corruption (direct)": "spell_shadow_seedofdestruction",
}

const enchantIconByEnchantId = {
	"3002": "spell_nature_lightningoverload",
	"2995": "inv_misc_orb_03",
	"2748": "spell_nature_astralrecalgroup",
}

const buffIconByBuffId = {
	"27127": "spell_holy_arcaneintellect",
	"26991": "spell_nature_giftofthewild",
    "25898": "spell_magic_magearmor",
    "28189": "spell_shadow_felarmour",
    "18791": "spell_shadow_psychicscream",
    "18789": "spell_shadow_psychicscream",
    "20749": "inv_potion_105",
    "28017": "inv_potion_141",
    "33263": "spell_misc_food",
    "17628": "inv_potion_41",
    "28540": "inv_potion_115",
    "24907": "spell_nature_moonglow",
    "3738": "spell_nature_slowingtotem",
    "30706": "spell_fire_totemofwrath",
    "33195": "spell_shadow_misery",
    "15334": "spell_shadow_blackplague",
    "12873": "spell_fire_soulburn",
    "27228": "spell_shadow_chilltouch",
    "227228": "spell_shadow_chilltouch",
    "35543": "inv_misc_drum_02",
    "28508": "inv_potion_107",
    "33702": "racial_orc_berserkerstrength",
}
