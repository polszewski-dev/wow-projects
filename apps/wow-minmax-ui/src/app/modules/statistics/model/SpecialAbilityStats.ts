import { Ability } from "../../shared/model/spell/Ability";

export interface SpecialAbilityStats {
	ability: Ability;
	attributes: string;
	statEquivalent: string;
	spEquivalent: number;
}
