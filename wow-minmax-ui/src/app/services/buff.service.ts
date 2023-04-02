import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Buff } from '../model/buff/Buff';

@Injectable({
	providedIn: 'root'
})
export class BuffService {
	private readonly apiUrl = 'http://localhost:8080/api/v1/buff';

	constructor(private http: HttpClient) { }

	getBuffs(characterId: string): Observable<Buff[]> {
		return this.http.get<Buff[]>(`${this.apiUrl}/${characterId}/list`);
	}

	changeBuff(characterId: string, buff: Buff): Observable<Buff[]> {
		return this.http.get<Buff[]>(`${this.apiUrl}/${characterId}/enable/${buff.id}/${buff.enabled}`);
	}
}
