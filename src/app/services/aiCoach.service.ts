import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AiInsightResponse, AiChatResponse } from '../models/ai.model';
 
@Injectable({ providedIn: 'root' })

export class AiCoachService {
  //private baseUrl = 'http://192.168.1.16:8080/api/ai';
  // private baseUrl = 'http://localhost:8080/api/ai';
    private baseUrl = 'http://habit-tracker-env.eba-ykqxpvem.eu-west-1.elasticbeanstalk.com/api/ai';

    constructor(private http: HttpClient) { }

    getInsights(): Observable<AiInsightResponse> {
        return this.http.get<AiInsightResponse>(`${this.baseUrl}/getInsights`);
    }

      refreshInsights(): Observable<AiInsightResponse> {
        return this.http.get<AiInsightResponse>(`${this.baseUrl}/refreshInsights`);
    }


    chat(message: string, conversationId: string | null, conversationHistory: string[]): Observable<AiChatResponse> {
    return this.http.post<AiChatResponse>(`${this.baseUrl}/chat`, {
      message,
      conversationId,
      conversationHistory
    });
  }
 
}