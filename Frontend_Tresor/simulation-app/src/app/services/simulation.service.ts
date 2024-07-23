import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SimulationService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  uploadFile(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file, file.name);

    return this.http.post(`${this.apiUrl}/upload`, formData);
  }

  simulate(filePath: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/simulate`, {
      params: {
        filePath
      }
    });
  }
}
