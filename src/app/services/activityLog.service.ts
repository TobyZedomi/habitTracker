import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable} from 'rxjs';
import { ActivityLogCardResponse, ActivityLogRequest } from '../models/activityLog.model';
import { ActivityLogResponse, ActivityLogDetailResponseRaw } from '../models/activityLog.model';

@Injectable({ providedIn: 'root' })

export class ActivityLogService {
     //  private baseUrl = 'http://192.168.1.16:8080/api/habitTrackerLog';
       // private baseUrl = 'http://localhost:8080/api/habitTrackerLog';
  private baseUrl = 'http://habit-tracker-env.eba-ykqxpvem.eu-west-1.elasticbeanstalk.com/api/habitTrackerLog';

        constructor(private http: HttpClient) { }


        logHabit(logData: ActivityLogRequest): Observable<any> {
            return this.http.post(`${this.baseUrl}/logHabit`, logData, { responseType: 'text' } );
        }

        getLogsForHabit(habitId: number): Observable<ActivityLogDetailResponseRaw[]> {
            return this.http.get<ActivityLogDetailResponseRaw[]>(`${this.baseUrl}/getAllLogsForAHabit/${habitId}`);
        }

        
        getRecentLogsForCurrentUser(): Observable<ActivityLogResponse[]> {
            return this.http.get<ActivityLogResponse[]>(`${this.baseUrl}/getAllRecentLogsForAHabit`);
        }


        getAllLoggedHabitsForCurrentUser(): Observable<ActivityLogResponse[]> {
            return this.http.get<ActivityLogResponse[]>(`${this.baseUrl}/getAllLoggedHabitsForCurrentUser`);
        }

        updateHabitLog(habitTrackerLogId: number, logData: any): Observable<any> {
            return this.http.put(`${this.baseUrl}/updateHabitLog`, { habitTrackerLogId, ...logData }, { responseType: 'text' });
        }


        deleteHabitLog(habitTrackerLogId: number): Observable<any> {
            return this.http.delete(`${this.baseUrl}/deleteHabitLog`, { body: { habitTrackerLogId }, responseType: 'text' });
        }
        
        getWeeklyActivityCards(): Observable<ActivityLogCardResponse[]> {
        return this.http.get<ActivityLogCardResponse[]>(`${this.baseUrl}/weekly-cards`);
    }
        
    }