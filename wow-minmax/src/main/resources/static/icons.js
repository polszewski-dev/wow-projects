"use strict";

const getSpellIcon = spellName => {
	if (!spellName || !spellIconBySpellName[spellName]) {console.log('icon', spellName)}
	return `https://wow.zamimg.com/images/wow/icons/small/${spellIconBySpellName[spellName]}.jpg`
}

const getItemIcon = item => {
	return `https://wow.zamimg.com/images/wow/icons/small/${item.icon}.jpg`
}

const getEnchantIcon = enchant => {
	return `https://wow.zamimg.com/images/wow/icons/small/${enchant.icon}.jpg`
}

const getGemIcon = gem => {
	return `https://wow.zamimg.com/images/wow/icons/small/${gem.icon}.jpg`
}

const getBuffIcon = buff => {
	return `https://wow.zamimg.com/images/wow/icons/small/${buff.icon}.jpg`
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
