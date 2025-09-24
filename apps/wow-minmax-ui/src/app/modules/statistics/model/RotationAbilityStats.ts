import { Ability } from "../../shared/model/spell/Ability";

export interface RotationAbilityStats {
	ability: Ability;
	numCasts: number;
	damage: number;
}
