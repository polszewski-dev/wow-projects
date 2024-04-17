import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Buff } from '../model/buff/Buff';
import { BuffListType } from '../model/buff/BuffListType';

@Injectable({
	providedIn: 'root'
})
export class BuffService {
	private readonly apiUrl = 'http://localhost:8080/api/v1/buffs';

	constructor(private http: HttpClient) { }

	getBuffs(characterId: string, buffListType: BuffListType) {
		return this.http.get<Buff[]>(`${this.apiUrl}/${characterId}/${buffListType}`);
	}

	enableBuff(characterId: string, buffListType: BuffListType, buff: Buff) {
		return this.http.put<Buff[]>(`${this.apiUrl}/${characterId}/${buffListType}`, buff);
	}
}
