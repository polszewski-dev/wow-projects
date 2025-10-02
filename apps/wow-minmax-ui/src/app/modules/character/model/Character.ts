import { CharacterClass } from "../../shared/model/character/CharacterClass";
import { Race } from "../../shared/model/character/Race";
import { Profession } from "./Profession";

export interface Character {
	characterId: string;
	characterClass: CharacterClass;
	race: Race;
	professions: (Profession | null)[];
}
