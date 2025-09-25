import { Spell } from "../../shared/model/spell/Spell";

export interface CooldownStats {
	spell: Spell;
	uptime: number;
}
