import { Spell } from "../spell/Spell";

export interface RotationSpellStats {
	spell: Spell;
	numCasts: number;
	damage: number;
}
