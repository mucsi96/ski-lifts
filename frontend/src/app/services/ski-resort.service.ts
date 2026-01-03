import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SkiResort, SkiResortDetail, Lift } from '../models/ski-resort.model';

@Injectable({
  providedIn: 'root'
})
export class SkiResortService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/resorts';

  getAllResorts(): Observable<SkiResort[]> {
    return this.http.get<SkiResort[]>(this.baseUrl);
  }

  getResortById(id: number): Observable<SkiResortDetail> {
    return this.http.get<SkiResortDetail>(`${this.baseUrl}/${id}`);
  }

  getResortsByRegion(region: string): Observable<SkiResort[]> {
    return this.http.get<SkiResort[]>(`${this.baseUrl}/region/${region}`);
  }

  getResortsByMaxDriveTime(maxMinutes: number): Observable<SkiResort[]> {
    return this.http.get<SkiResort[]>(`${this.baseUrl}/drive-time`, {
      params: { maxMinutes: maxMinutes.toString() }
    });
  }

  searchResorts(query: string): Observable<SkiResort[]> {
    return this.http.get<SkiResort[]>(`${this.baseUrl}/search`, {
      params: { q: query }
    });
  }

  getResortLifts(resortId: number): Observable<Lift[]> {
    return this.http.get<Lift[]>(`${this.baseUrl}/${resortId}/lifts`);
  }

  getLiftById(liftId: number): Observable<Lift> {
    return this.http.get<Lift>(`${this.baseUrl}/lifts/${liftId}`);
  }

  fetchElevationProfile(liftId: number, numPoints: number = 20): Observable<Lift> {
    return this.http.post<Lift>(
      `${this.baseUrl}/lifts/${liftId}/elevation-profile`,
      null,
      { params: { numPoints: numPoints.toString() } }
    );
  }
}
