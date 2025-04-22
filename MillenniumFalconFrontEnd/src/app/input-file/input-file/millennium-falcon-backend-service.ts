import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MillenniumFalconBackendService {
  private baseUrl = environment.apiUrl
  constructor(private http: HttpClient) {
  }

  compute(fileContent: string | ArrayBuffer | null): Observable<number> {
    const headers = new HttpHeaders({'Content-Type': 'application/json'})
    return this.http.post<number>(this.baseUrl + '/compute', fileContent, {headers})
  }
}
