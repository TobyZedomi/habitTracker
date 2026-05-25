import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Observer } from 'rxjs';
import { environment } from 'src/environments/environment.prod';
import { ActivityType } from '../models/activityType.model';

@Injectable({ providedIn: 'root' })

export class ActivityTypeService {
       // private baseUrl = 'http://192.168.1.16:8080/api/activity';
         //private baseUrl = 'http://localhost:8080/api/activity';
          private baseUrl = 'http://habit-tracker-env.eba-ykqxpvem.eu-west-1.elasticbeanstalk.com/api/activity';



        constructor(private http: HttpClient) { }

        getAllActivityTypes(): Observable<ActivityType[]> {
            return this.http.get<ActivityType[]>(`${this.baseUrl}/getAllActivityTypes`);
        }

        
        
    }