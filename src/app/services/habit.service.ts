import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Observer } from 'rxjs';
import { environment } from 'src/environments/environment.prod';
import { HabitResponse, HabitRequest, HabitListResponse } from '../models/habit.model';


@Injectable({ providedIn: 'root' })

export class HabitService {
        //private baseUrl = 'http://192.168.1.16:8080/api/habits';
       // private baseUrl = 'http://localhost:8080/api/habits';
         private baseUrl = 'http://habit-tracker-env.eba-ykqxpvem.eu-west-1.elasticbeanstalk.com/api/habits';




        constructor(private http: HttpClient) { }

        getAllHabitsForCurrentUser(): Observable<HabitResponse[]> {
            return this.http.get<HabitResponse[]>(`${this.baseUrl}/getAllHabitsForLoggedInUser`);
        }


        getOneHabitById(habitId: number): Observable<HabitResponse> {
            return this.http.get<HabitResponse>(`${this.baseUrl}/getOneHabitById/${habitId}`);
        }

        createHabit(habitData: any): Observable<any> {
            return this.http.post(`${this.baseUrl}/createHabit`, habitData, { responseType: 'text'});
        }

        updateHabit(request: HabitRequest): Observable<any> {
            return this.http.put(`${this.baseUrl}/updateHabit`, request, { responseType: 'text' });
        }   
        
        deleteHabit(habitId: number): Observable<any> {
            return this.http.delete(`${this.baseUrl}/deleteHabit`, { body: { habitId }, responseType: 'text' });
        }

        getLatestHabit() {
            return this.http.get<HabitResponse>(`${this.baseUrl}/latest`);
        }
    }
    