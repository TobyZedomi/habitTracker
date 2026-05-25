import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Component, OnInit } from '@angular/core';
import {
    IonHeader, IonToolbar, IonTitle, IonContent, IonButton, IonButtons,
    IonIcon, IonCard, IonCardContent, IonChip, IonLabel, IonSpinner, IonText
} from '@ionic/angular/standalone';
import { HabitService } from '../services/habit.service';
import { ActivityLogService } from '../services/activityLog.service';
import { ActivatedRoute, Router } from '@angular/router';
import { HabitResponse } from '../models/habit.model';
import { ActivityLogDetailResponseRaw, ActivityLogRequest } from '../models/activityLog.model';
import { AlertController } from '@ionic/angular';
import { AiCoachService } from '../services/aiCoach.service';
import { addIcons } from 'ionicons';
import {
    arrowBackOutline, createOutline, addCircleOutline, flameOutline,
    trophyOutline, calendarOutline, timeOutline, flagOutline,
    notificationsOutline, pulseOutline, checkmarkCircleOutline,
    documentTextOutline, mapOutline, trashOutline, chevronDownOutline,
    chevronUpOutline
} from 'ionicons/icons';

@Component({
    selector: 'app-habit-detail',
    templateUrl: './habit-detail.page.html',
    styleUrls: ['./habit-detail.page.scss'],
    standalone: true,
    imports: [
        CommonModule, FormsModule,
        IonHeader, IonToolbar, IonTitle, IonContent, IonButton, IonButtons,
        IonIcon, IonCard, IonCardContent, IonChip, IonLabel, IonSpinner, IonText
    ]
})
export class HabitDetailPage implements OnInit {

    habitId!: number;
    habits: HabitResponse | null = null;
    logs: ActivityLogDetailResponseRaw[] = [];
    loading = false;
    errorMessage = '';
    expandedLogIds = new Set<number>();

    editingLogId: number | null = null;
editDuration: number | null = null;
editDistance: number | null = null;
editCalories: number | null = null;
editNotes = '';

    constructor(
        private habitService: HabitService,
        private activityLogService: ActivityLogService,
        private router: Router,
        private route: ActivatedRoute,
        private alertController: AlertController,
        private aiCoachService: AiCoachService
    ) {
        addIcons({
            arrowBackOutline, createOutline, addCircleOutline, flameOutline,
            trophyOutline, calendarOutline, timeOutline, flagOutline,
            notificationsOutline, pulseOutline, checkmarkCircleOutline,
            documentTextOutline, mapOutline, trashOutline, chevronDownOutline,
            chevronUpOutline
        });
    }

    ngOnInit() {
        this.loadHabitIdFromRoute();
        this.loadHabitDetails();
        this.loadHabitLogs();
    }
    

    ionViewWillEnter() {
        this.loadHabitDetails();
        this.loadHabitLogs();
    }
        

    loadHabitIdFromRoute() {
        const param = this.route.snapshot.paramMap.get('habitId');
        if (param) this.habitId = +param;
    }

    loadHabitDetails() {
        this.habitService.getOneHabitById(this.habitId).subscribe(
            habit => { this.habits = habit; },
            () => {}
        );
    }

    loadHabitLogs() {
        this.activityLogService.getLogsForHabit(this.habitId).subscribe(
            logs => { this.logs = logs; },
            () => {}
        );
    }

    confirmDeleteLog(habitTrackerLogId: number) {
        this.alertController.create({
            header: 'Confirm Delete',
            message: 'Are you sure you want to delete this log entry?',
            buttons: [
                { text: 'Cancel', role: 'cancel' },
                { text: 'Delete', handler: () => this.deleteLog(habitTrackerLogId) }
            ]
        }).then(alert => alert.present());
    }

    startEdit(log: ActivityLogDetailResponseRaw) {
    this.editingLogId = log.habit_tracker_log_id;
    this.editDuration = log.duration_minutes || null;
    this.editDistance = log.distance_km || null;
    this.editCalories = log.calories_burned || null;
    this.editNotes = log.notes || '';
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
            this.aiCoachService.refreshInsights().subscribe();
            this.cancelEdit();
            this.loadHabitLogs();
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

    deleteLog(habitTrackerLogId: number) {
        this.activityLogService.deleteHabitLog(habitTrackerLogId).subscribe(
            () => { this.aiCoachService.refreshInsights().subscribe(); this.loadHabitLogs(); },
            () => { this.errorMessage = 'Failed to delete habit log. Please try again later.'; }
        );
    }

    toggleLog(id: number) {
        if (this.expandedLogIds.has(id)) {
            this.expandedLogIds.delete(id);
        } else {
            this.expandedLogIds.add(id);
        }
    }

    goToLogActivity() {
        this.router.navigate(['/tabs/tab2'], { queryParams: { focusHabitId: this.habitId } });
    }

    goToEditHabit() {
        this.router.navigate(['/tabs/edit-habit', this.habitId]);
    }

    goBackToHabits() {
        this.router.navigate(['/tabs/tab2']);
    }

    async logout() {
        await this.router.navigateByUrl('/login', { replaceUrl: true });
    }
}