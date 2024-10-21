import { SpellSchool } from "../spell/SpellSchool";
import { Race } from "./Race";

export interface CharacterClass {
	id: string;
	name: string;
	icon: string;
	races: Race[];
	spellSchools: SpellSchool[];
}
