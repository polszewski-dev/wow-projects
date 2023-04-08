import { CharacterClass } from "./CharacterClass";
import { Race } from "./Race";

export interface Character {
	characterId: string;
	characterClass: CharacterClass;
	race: Race;
}
