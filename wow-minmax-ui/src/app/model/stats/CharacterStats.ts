import { SpellSchool } from "../../modules/shared/model/spell/SpellSchool";

export interface CharacterStats {
	type: string;
	sp: number;
	spellDamageBySchool: Record<SpellSchool, number | undefined>;
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
