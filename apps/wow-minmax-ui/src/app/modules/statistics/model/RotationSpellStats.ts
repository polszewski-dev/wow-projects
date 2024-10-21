import { Spell } from "../../shared/model/spell/Spell";

export interface RotationSpellStats {
	spell: Spell;
	numCasts: number;
	damage: number;
}
