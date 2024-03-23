import { Spell } from "../../modules/shared/model/spell/Spell";

export interface RotationSpellStats {
	spell: Spell;
	numCasts: number;
	damage: number;
}
