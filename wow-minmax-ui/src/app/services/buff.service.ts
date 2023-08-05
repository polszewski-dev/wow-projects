import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Buff } from '../model/buff/Buff';
import { BuffListType } from '../model/buff/BuffListType';

@Injectable({
	providedIn: 'root'
})
export class BuffService {
	private readonly apiUrl = 'http://localhost:8080/api/v1/buff';

	constructor(private http: HttpClient) { }

	getBuffs(characterId: string, buffListType: BuffListType): Observable<Buff[]> {
		return this.http.get<Buff[]>(`${this.apiUrl}/${characterId}/${buffListType}/list`);
	}

	enableBuff(characterId: string, buffListType: BuffListType, buff: Buff): Observable<Buff[]> {
		return this.http.get<Buff[]>(`${this.apiUrl}/${characterId}/${buffListType}/enable/${buff.buffId}/${buff.rank}/${buff.enabled}`);
	}
}
