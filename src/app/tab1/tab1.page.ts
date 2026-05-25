import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
    IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonButtons,
    IonText, IonSpinner, IonGrid, IonRow, IonCol, IonCard, IonCardContent,
    IonSegment, IonSegmentButton, IonLabel, IonIcon
} from '@ionic/angular/standalone';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService, UserResponse } from '../auth/auth.service';
import { DashboardResponse } from '../models/dashboard.model';
import { ActivityLogResponse, ActivityLogRequest } from '../models/activityLog.model';
import { ActivityLogService } from '../services/activityLog.service';
import { DashboardService } from '../services/dashboard.service';
import { AlertController, ToastController } from '@ionic/angular';
import { addIcons } from 'ionicons';
import {
    logOutOutline, listOutline, checkmarkCircleOutline, calendarOutline,
    timeOutline, mapOutline, flameOutline, pulseOutline, trophyOutline,
    documentTextOutline, trashOutline, cloudDownloadOutline, heartOutline,
    barChartOutline, statsChartOutline, chevronBackOutline, chevronForwardOutline,
    personOutline
} from 'ionicons/icons';
import { GraphService } from '../services/graph.service';
import { GraphDataPoint } from '../models/graph.model';
import {AiCoachService} from '../services/aiCoach.service';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
    selector: 'app-tab1',
    templateUrl: './tab1.page.html',
    styleUrls: ['./tab1.page.scss'],
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonButtons,
        IonSpinner, IonText, IonGrid, IonRow, IonCol, IonCard, IonCardContent,
        IonSegment, IonSegmentButton, IonLabel, IonIcon
    ]
})
export class Tab1Page implements OnInit, AfterViewInit {

    @ViewChild('dailyChartCanvas') dailyChartCanvas!: ElementRef<HTMLCanvasElement>;
    @ViewChild('weeklyChartCanvas') weeklyChartCanvas!: ElementRef<HTMLCanvasElement>;

    user: UserResponse | null = null;
    dashboardSummary: DashboardResponse | null = null;
    recentActivities: ActivityLogResponse[] = [];
    allActivities: ActivityLogResponse[] = [];
    selectedView: string = 'recent';
    loading: boolean = false;
    errorMessage: string = '';
    importLoading: boolean = false;

    dailyPoints: GraphDataPoint[] = [];
    weeklyPoints: GraphDataPoint[] = [];
    graphsLoading: boolean = false;

    activeSlide: number = 0;
    readonly slides = ['Overview', 'Daily', 'Weekly'];

    selectedPeriod: number = 0;
    readonly periods = [
        { label: '7d', days: 7 },
        { label: '30d', days: 30 },
        { label: '90d', days: 90 },
        { label: 'All', days: 0 }
    ];

    private dailyChart: Chart | null = null;
    private weeklyChart: Chart | null = null;
    private touchStartX: number = 0;

    currentUsername: string = '';

    constructor(
        private auth: AuthService,
        private router: Router,
        private dashboardService: DashboardService,
        private activityLogService: ActivityLogService,
        private alertController: AlertController,
        private toastController: ToastController,
        private graphService: GraphService ,
        private aiCoachService: AiCoachService
    ) {
        addIcons({
            logOutOutline, listOutline, checkmarkCircleOutline, calendarOutline,
            timeOutline, mapOutline, flameOutline, pulseOutline, trophyOutline,
            documentTextOutline, trashOutline, cloudDownloadOutline, heartOutline,
            barChartOutline, statsChartOutline, chevronBackOutline, chevronForwardOutline,
            personOutline
        });
    }

    editingLogId: number | null = null;
editDuration: number | null = null;
editDistance: number | null = null;
editCalories: number | null = null;
editNotes = '';

    async ngOnInit() {
        this.user = await this.auth.getCurrentUser();
        if (!this.user) {
            this.user = await this.auth.loadCurrentUser();
        }
        if (this.user) {
            this.currentUsername = this.user.username;
        }
        this.selectedView = 'recent';
        this.loadDashboardSummary();
        this.loadRecentActivities();
        this.loadGraphs();
    }

    ngAfterViewInit() {}

    ionViewWillEnter() {
        this.loadDashboardSummary();
        this.loadRecentActivities();
    }

    onCarouselTouchStart(event: TouchEvent) {
        this.touchStartX = event.touches[0].clientX;
    }

    onCarouselTouchEnd(event: TouchEvent) {
        const delta = this.touchStartX - event.changedTouches[0].clientX;
        if (Math.abs(delta) > 50) {
            if (delta > 0) {
                this.nextSlide();
            } else {
                this.prevSlide();
            }
        }
    }

    goToSlide(index: number) {
        this.activeSlide = index;
        if (index === 1 && this.dailyPoints.length > 0) {
            setTimeout(() => this.renderDailyChart(), 80);
        }
        if (index === 2 && this.weeklyPoints.length > 0) {
            setTimeout(() => this.renderWeeklyChart(), 80);
        }
    }

    prevSlide() {
        if (this.activeSlide > 0) {
            this.goToSlide(this.activeSlide - 1);
        }
    }

    nextSlide() {
        if (this.activeSlide < this.slides.length - 1) {
            this.goToSlide(this.activeSlide + 1);
        }
    }

    expandedActivityIds: Set<number> = new Set();

toggleActivity(id: number) {
    if (this.expandedActivityIds.has(id)) {
        this.expandedActivityIds.delete(id);
    } else {
        this.expandedActivityIds.add(id);
    }
}

    loadDashboardSummary() {
        this.dashboardService.getDashboardSummary(this.selectedPeriod).subscribe((data) => {
            this.dashboardSummary = data;
        });
    }

    selectPeriod(days: number) {
        this.selectedPeriod = days;
        this.loadDashboardSummary();
    }

    loadRecentActivities() {
        this.dashboardService.getDashboardRecentActivities().subscribe((data) => {
            this.recentActivities = data;
        });
    }

    loadAllActivities() {
        this.activityLogService.getAllLoggedHabitsForCurrentUser().subscribe((data) => {
            this.allActivities = data;
        });
    }

    loadGraphs() {
        this.graphsLoading = true;
        this.graphService.getMyDailyCompletions().subscribe(
            data => {
                this.dailyPoints = data;
                if (this.activeSlide === 1 && data.length > 0) {
                    setTimeout(() => this.renderDailyChart(), 80);
                }
            },
            () => {}
        );
        this.graphService.getMyWeeklyCompletions().subscribe(
            data => {
                this.weeklyPoints = data;
                this.graphsLoading = false;
                if (this.activeSlide === 2 && data.length > 0) {
                    setTimeout(() => this.renderWeeklyChart(), 80);
                }
            },
            () => { this.graphsLoading = false; }
        );
    }

    private renderDailyChart() {
        if (!this.dailyChartCanvas) return;
        if (this.dailyChart) { this.dailyChart.destroy(); this.dailyChart = null; }
        const canvas = this.dailyChartCanvas.nativeElement;
        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        const areaGradient = ctx.createLinearGradient(0, 0, 0, 200);
        areaGradient.addColorStop(0, 'rgba(20, 184, 166, 0.35)');
        areaGradient.addColorStop(1, 'rgba(20, 184, 166, 0.0)');

        const maxVal = Math.max(...this.dailyPoints.map(p => p.value), 1);

        this.dailyChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: this.dailyPoints.map(p => p.label.substring(5)),
                datasets: [{
                    label: 'Completions',
                    data: this.dailyPoints.map(p => p.value),
                    borderColor: '#14b8a6',
                    borderWidth: 2.5,
                    backgroundColor: areaGradient,
                    tension: 0.42,
                    fill: true,
                    pointRadius: 3,
                    pointBackgroundColor: '#ffffff',
                    pointBorderColor: '#14b8a6',
                    pointBorderWidth: 2,
                    pointHoverRadius: 7,
                    pointHoverBackgroundColor: '#14b8a6',
                    pointHoverBorderColor: '#ffffff',
                    pointHoverBorderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                animation: { duration: 600, easing: 'easeOutQuart' },
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        backgroundColor: '#1d2230',
                        titleColor: '#f8fafc',
                        bodyColor: '#98a2b3',
                        borderColor: 'rgba(20,184,166,0.3)',
                        borderWidth: 1,
                        cornerRadius: 10,
                        padding: 10,
                        callbacks: {
                            label: (context) => ` ${context.parsed.y} completion${context.parsed.y !== 1 ? 's' : ''}`
                        }
                    }
                },
                scales: {
                    x: {
                        ticks: { color: '#6b7280', font: { size: 10 }, maxTicksLimit: 10 },
                        grid: { color: 'rgba(255,255,255,0.03)', drawTicks: false },
                        border: { color: 'rgba(255,255,255,0.05)' }
                    },
                    y: {
                        beginAtZero: true,
                        max: maxVal + 1,
                        ticks: { stepSize: 1, color: '#6b7280', font: { size: 11 } },
                        grid: { color: 'rgba(255,255,255,0.05)' },
                        border: { color: 'transparent' }
                    }
                }
            }
        });
    }

    private renderWeeklyChart() {
        if (!this.weeklyChartCanvas) return;
        if (this.weeklyChart) { this.weeklyChart.destroy(); this.weeklyChart = null; }
        const canvas = this.weeklyChartCanvas.nativeElement;
        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        const areaGradient = ctx.createLinearGradient(0, 0, 0, 200);
        areaGradient.addColorStop(0, 'rgba(99, 102, 241, 0.28)');
        areaGradient.addColorStop(1, 'rgba(99, 102, 241, 0.0)');

        const maxVal = Math.max(...this.weeklyPoints.map(p => p.value), 1);

        this.weeklyChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: this.weeklyPoints.map(p => p.label),
                datasets: [{
                    label: 'Weekly',
                    data: this.weeklyPoints.map(p => p.value),
                    borderColor: '#6366f1',
                    borderWidth: 2.5,
                    backgroundColor: areaGradient,
                    tension: 0.42,
                    fill: true,
                    pointBackgroundColor: '#ffffff',
                    pointBorderColor: '#6366f1',
                    pointBorderWidth: 2,
                    pointRadius: 5,
                    pointHoverRadius: 8,
                    pointHoverBackgroundColor: '#6366f1',
                    pointHoverBorderColor: '#ffffff',
                    pointHoverBorderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                animation: { duration: 700, easing: 'easeOutQuart' },
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        backgroundColor: '#1d2230',
                        titleColor: '#f8fafc',
                        bodyColor: '#98a2b3',
                        borderColor: 'rgba(99, 102, 241, 0.3)',
                        borderWidth: 1,
                        cornerRadius: 10,
                        padding: 10,
                        callbacks: {
                            label: (context) => ` ${context.parsed.y} completion${context.parsed.y !== 1 ? 's' : ''}`
                        }
                    }
                },
                scales: {
                    x: {
                        ticks: { color: '#6b7280', font: { size: 10 } },
                        grid: { color: 'rgba(255,255,255,0.03)', drawTicks: false },
                        border: { color: 'rgba(255,255,255,0.05)' }
                    },
                    y: {
                        beginAtZero: true,
                        max: maxVal + 1,
                        ticks: { stepSize: 1, color: '#6b7280', font: { size: 11 } },
                        grid: { color: 'rgba(255,255,255,0.05)' },
                        border: { color: 'transparent' }
                    }
                }
            }
        });
    }

    onSegmentChange(event: any) {
        const val = event.detail.value;
        this.selectedView = val;
        if (val === 'recent') {
            this.loadRecentActivities();
        }
    }

    startEdit(activity: ActivityLogResponse) {
    this.editingLogId = activity.habitTrackerLogId;
    this.editDuration = activity.duration || null;
    this.editDistance = activity.distanceKm || null;
    this.editCalories = activity.caloriesBurned || null;
    this.editNotes = activity.notes || '';
}

cancelEdit() {
    this.editingLogId = null;
    this.editDuration = null;
    this.editDistance = null;
    this.editCalories = null;
    this.editNotes = '';
}

saveEdit(logId: number) {
    const logData = {
        habitTrackerLogId: logId,
        durationMinutes: this.editDuration || 0,
        distanceKm: this.editDistance || 0,
        caloriesBurned: this.editCalories || 0,
        notes: this.editNotes || ''
    };

    this.activityLogService.updateHabitLog(logId, logData).subscribe(
        async () => {
            const alert = await this.alertController.create({
                header: 'Updated',
                message: 'Activity log has been updated.',
                cssClass: 'app-success-alert',
                buttons: ['OK']
            });
            await alert.present();
            await alert.onDidDismiss();
            this.cancelEdit();
            this.loadDashboardSummary();
            this.loadRecentActivities();
            this.aiCoachService.refreshInsights().subscribe();
        },
        async (error) => {
    const message = error?.error || 'Failed to update activity log. Please try again.';
    const alert = await this.alertController.create({
        message: message,
        buttons: ['OK']
    });
    await alert.present();
}
    );
}

  async confirmDeleteLog(habitTrackerLogId: number) {
    const confirm = await this.alertController.create({
        header: 'Delete Activity Log',
        message: 'Are you sure you want to delete this activity log?',
        buttons: [
            { text: 'Cancel', role: 'cancel' },
            { text: 'Continue', role: 'destructive' }
        ]
    });
    await confirm.present();
    const { role } = await confirm.onDidDismiss();

    if (role === 'destructive') {
        this.activityLogService.deleteHabitLog(habitTrackerLogId).subscribe(
            () => {
                this.aiCoachService.refreshInsights().subscribe();
                this.loadDashboardSummary();
                this.loadRecentActivities();
            },
            () => {
                this.errorMessage = 'Failed to delete activity log. Please try again.';
            }
        );
    }
}

    async logout() {
        await this.auth.logout();
        await this.router.navigateByUrl('/login', { replaceUrl: true });
    }
}