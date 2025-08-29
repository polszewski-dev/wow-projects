import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { BuffListType } from '../model/buff/BuffListType';
import { BuffStatus } from '../model/buff/BuffStatus';

@Injectable({
	providedIn: 'root'
})
export class BuffService {
	private readonly apiUrl = environment.buffApiUrl;

	constructor(private http: HttpClient) { }

	getBuffStatuses(characterId: string, buffListType: BuffListType) {
		return this.http.get<BuffStatus[]>(`${this.apiUrl}/${characterId}/${buffListType}`);
	}

	changeBuffStatus(characterId: string, buffListType: BuffListType, buffStatus: BuffStatus) {
		return this.http.put<BuffStatus[]>(`${this.apiUrl}/${characterId}/${buffListType}`, buffStatus);
	}
}
