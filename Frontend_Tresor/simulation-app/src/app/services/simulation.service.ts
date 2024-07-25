import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Carte } from '../model/carte';

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

  simulate(filePath: string): Observable<{ message: string; result: Carte }> {
    return this.http.get<{ message: string; result: Carte }>(`${this.apiUrl}/simulate`, {
      params: { filePath }
    });
  }
  

  downloadFile(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/download`, { responseType: 'blob' });
  }

  
}
