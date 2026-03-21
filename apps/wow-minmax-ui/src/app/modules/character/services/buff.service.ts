import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { BuffGroup, BuffStatus } from '../model/buff/BuffStatus';

@Injectable({
	providedIn: 'root'
})
export class BuffService {
	private readonly apiUrl = environment.buffApiUrl;

	constructor(private http: HttpClient) { }

	getBuffStatuses(playerId: string) {
		return this.http.get<BuffGroup[]>(`${this.apiUrl}/${playerId}`);
	}

	changeBuffStatus(playerId: string, buffStatus: BuffStatus) {
		return this.http.put<void>(`${this.apiUrl}/${playerId}`, buffStatus);
	}
}
