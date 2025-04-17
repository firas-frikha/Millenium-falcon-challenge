import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MillenniumFalconBackendService {
  private baseUrl = "http://localhost:8080";

  constructor(private http: HttpClient) {
  }

  compute(fileContent: string | ArrayBuffer | null): Observable<number> {
    const headers = new HttpHeaders({'Content-Type': 'application/json'})
    return this.http.post<number>(this.baseUrl + '/compute', fileContent, {headers})
  }
}
