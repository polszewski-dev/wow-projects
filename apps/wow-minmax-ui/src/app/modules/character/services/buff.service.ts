import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Buff } from '../model/buff/Buff';
import { BuffListType } from '../model/buff/BuffListType';
import { environment } from 'src/environments/environment';

@Injectable({
	providedIn: 'root'
})
export class BuffService {
	private readonly apiUrl = environment.buffApiUrl;

	constructor(private http: HttpClient) { }

	getBuffs(characterId: string, buffListType: BuffListType) {
		return this.http.get<Buff[]>(`${this.apiUrl}/${characterId}/${buffListType}`);
	}

	enableBuff(characterId: string, buffListType: BuffListType, buff: Buff) {
		return this.http.put<Buff[]>(`${this.apiUrl}/${characterId}/${buffListType}`, buff);
	}
}
