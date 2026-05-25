import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
    GraphDataPoint
} from '../models/graph.model';
 
@Injectable({ providedIn: 'root' })
export class GraphService {
    //private graphUrl = 'http://localhost:8080/api/graphs';
   // private graphUrl = 'http://192.168.1.16:8080/api/graphs';
     private graphUrl = 'http://habit-tracker-env.eba-ykqxpvem.eu-west-1.elasticbeanstalk.com/api/graphs';

 
    constructor(private http: HttpClient) {}
 
   
    getMyDailyCompletions(): Observable<GraphDataPoint[]> {
        return this.http.get<GraphDataPoint[]>(`${this.graphUrl}/daily`);
    }
 
    getMyWeeklyCompletions(): Observable<GraphDataPoint[]> {
        return this.http.get<GraphDataPoint[]>(`${this.graphUrl}/weekly`);
    }
 
   
}