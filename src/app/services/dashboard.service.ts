import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DashboardResponse } from '../models/dashboard.model';
import { ActivityLogResponse } from '../models/activityLog.model';

@Injectable({ providedIn: 'root' })

export class DashboardService {

    // private baseUrl = 'http://192.168.1.16:8080/api/dashboard';
     // private baseUrl = 'http://localhost:8080/api/dashboard';
       private baseUrl = 'http://habit-tracker-env.eba-ykqxpvem.eu-west-1.elasticbeanstalk.com/api/dashboard';


     constructor(private http: HttpClient) { }

     getDashboardSummary(days: number): Observable<DashboardResponse> {
         return this.http.get<DashboardResponse>(`${this.baseUrl}/getDashboardSummary?days=${days}`);
     }

     getDashboardRecentActivities(): Observable<ActivityLogResponse[]> {
         return this.http.get<ActivityLogResponse[]>(`${this.baseUrl}/getDashboardRecentActivities`);
     }

}