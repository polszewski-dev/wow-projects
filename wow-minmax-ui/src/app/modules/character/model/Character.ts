import { CharacterClass } from "../../shared/model/character/CharacterClass";
import { Race } from "../../shared/model/character/Race";

export interface Character {
	characterId: string;
	characterClass: CharacterClass;
	race: Race;
}
