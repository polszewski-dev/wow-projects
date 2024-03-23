import { CharacterClass } from "../../modules/shared/model/character/CharacterClass";
import { Race } from "../../modules/shared/model/character/Race";

export interface Character {
	characterId: string;
	characterClass: CharacterClass;
	race: Race;
}
