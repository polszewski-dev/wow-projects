import { SpellSchool } from "../../shared/model/spell/SpellSchool";

export interface CharacterStats {
	type: string;
	sp: number;
	spellDamageBySchool: Partial<Record<SpellSchool, number>>;
	hitRating: number;
	hitPct: number;
	critRating: number;
	critPct: number;
	hasteRating: number;
	hastePct: number;
	stamina: number;
	intellect: number;
	spirit: number;
	maxHealth: number;
	maxMana: number;
}
