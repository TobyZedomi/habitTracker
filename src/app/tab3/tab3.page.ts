import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import {
    IonHeader, IonToolbar, IonTitle, IonContent, IonSpinner, IonText,
    IonCard, IonCardContent, IonButton, IonIcon, IonButtons
} from '@ionic/angular/standalone';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { addIcons } from 'ionicons';
import {
    sparklesOutline, sendOutline, refreshOutline, bookOutline,
    playCircleOutline, chatbubbleOutline, logOutOutline
} from 'ionicons/icons';
import { Router } from '@angular/router';
import { AiCoachService } from '../services/aiCoach.service';
import { AuthService } from '../auth/auth.service';
import { AiInsightResponse, AiChatResponse, AiRecommendation, ChatMessage } from '../models/ai.model';

@Component({
    selector: 'app-tab3',
    templateUrl: 'tab3.page.html',
    styleUrls: ['tab3.page.scss'],
    standalone: true,
    imports: [
        CommonModule, FormsModule,
        IonHeader, IonToolbar, IonTitle, IonContent, IonSpinner, IonText,
        IonCard, IonCardContent, IonButton, IonIcon, IonButtons
    ]
})
export class Tab3Page implements OnInit {

    @ViewChild('chatBottom') chatBottom!: ElementRef;

    activeTab = 'insights';
    insight: AiInsightResponse | null = null;
    insightLoading = false;
    insightError = '';
    messages: ChatMessage[] = [];
    userInput = '';
    chatLoading = false;
    conversationId: string | null = null;
    conversationHistory: string[] = [];

    constructor(
        private aiCoachService: AiCoachService,
        private auth: AuthService,
        private router: Router
    ) {
        addIcons({
            sparklesOutline, sendOutline, refreshOutline, bookOutline,
            playCircleOutline, chatbubbleOutline, logOutOutline
        });
    }

    ngOnInit() {
        this.loadInsights();
    }

    
    ionViewWillEnter() {
        this.loadInsights();
    }
    

    loadInsights() {
        this.insightLoading = true;
        this.insightError = '';
        this.aiCoachService.getInsights().subscribe(
            (data: AiInsightResponse) => {
                this.insight = data;
                this.insightLoading = false;
            },
            () => {
                this.insightError = 'Failed to load insights. Please try again.';
                this.insightLoading = false;
            }
        );
    }

    refreshInsights() {
        this.insightLoading = true;
        this.insightError = '';
        this.aiCoachService.refreshInsights().subscribe(
    (data: AiInsightResponse) => {
        this.insight = data;
        this.insightLoading = false;
    },
    () => {
        this.insightError = 'Failed to refresh insights. Please try again.';
        this.insightLoading = false;
    }
);
    }

    sendMessage() {
        const text = this.userInput.trim();
        if (!text || this.chatLoading) return;

        this.messages.push({ role: 'user', content: text, timestamp: Date.now(), recommendations: [] });
        this.conversationHistory.push(text);
        this.userInput = '';
        this.chatLoading = true;

        this.aiCoachService.chat(text, this.conversationId, [...this.conversationHistory]).subscribe(
            (resp: AiChatResponse) => {
                this.conversationId = resp.conversationId;
                this.messages.push({
                    role: 'assistant',
                    content: resp.reply,
                    timestamp: resp.timestamp,
                    recommendations: resp.recommendations || []
                });
                this.conversationHistory.push(resp.reply);
                this.chatLoading = false;
                this.scrollToBottom();
            },
            () => {
                this.messages.push({
                    role: 'assistant',
                    content: 'Something went wrong. Please try again.',
                    timestamp: Date.now(),
                    recommendations: []
                });
                this.chatLoading = false;
                this.scrollToBottom();
            }
        );

        this.scrollToBottom();
    }

    newConversation() {
        this.messages = [];
        this.conversationHistory = [];
        this.conversationId = null;
    }

    switchTab(tab: string) { this.activeTab = tab; }
    openLink(url: string) { window.open(url, '_blank'); }

    getVideos(recommendations: AiRecommendation[]) {
        return recommendations.filter(r => r.type === 'VIDEO' && r.title?.trim());
    }

    getBooks(recommendations: AiRecommendation[]) {
        return recommendations.filter(r => r.type === 'BOOK' && r.title?.trim());
    }

    async logout() {
        await this.auth.logout();
        await this.router.navigateByUrl('/login', { replaceUrl: true });
    }

    private scrollToBottom() {
        setTimeout(() => {
            if (this.chatBottom) {
                this.chatBottom.nativeElement.scrollIntoView({ behavior: 'smooth' });
            }
        }, 100);
    }
}