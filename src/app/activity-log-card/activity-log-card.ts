import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import {
    IonCard, IonCardContent, IonButton, IonIcon,
    IonCheckbox, IonSpinner
} from '@ionic/angular/standalone';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AlertController } from '@ionic/angular';
import { addIcons } from 'ionicons';
import {
    checkmarkCircleOutline, addCircleOutline, chevronDownOutline,
    chevronUpOutline, fitnessOutline, timeOutline, mapOutline,
    flameOutline, documentTextOutline, trophyOutline
} from 'ionicons/icons';
import { ActivityLogCardResponse, ActivityLogRequest } from '../models/activityLog.model';
import { ActivityLogService } from '../services/activityLog.service';
import { AiCoachService } from '../services/aiCoach.service';

@Component({
    selector: 'app-activity-log-card',
    templateUrl: './activity-log-card.html',
    styleUrls: ['./activity-log-card.scss'],
    standalone: true,
    imports: [
        CommonModule, FormsModule,
        IonCard, IonCardContent, IonButton, IonIcon,
        IonCheckbox, IonSpinner
    ]
})
export class ActivityLogCard implements OnInit {

    @Input() card!: ActivityLogCardResponse;
    @Output() activityLogged = new EventEmitter<void>();

    isChecked: boolean = false;
    showDetails: boolean = false;
    showExtraLogControls: boolean = false;
    saving: boolean = false;

    durationMinutes: number | null = null;
    distanceKm: number | null = null;
    caloriesBurned: number | null = null;
    notes: string = '';

    today: string = new Date().toISOString().split('T')[0];

    constructor(
        private activityLogService: ActivityLogService,
        private alertController: AlertController,
        private aiCoachService: AiCoachService
    ) {
        addIcons({
            checkmarkCircleOutline, addCircleOutline, chevronDownOutline,
            chevronUpOutline, fitnessOutline, timeOutline, mapOutline,
            flameOutline, documentTextOutline, trophyOutline
        });
    }

    ngOnInit() {}

    get progressPercent(): number {
        if (this.card.frequency <= 0) return 0;
        const pct = (this.card.weeklyCompletedCount / this.card.frequency) * 100;
        return Math.min(pct, 100);
    }

    toggleDetails() {
        this.showDetails = !this.showDetails;
    }

    showExtraLog() {
        this.showExtraLogControls = true;
        this.resetForm();
    }

    cancelExtraLog() {
        this.showExtraLogControls = false;
        this.resetForm();
    }

    resetForm() {
        this.isChecked = false;
        this.showDetails = false;
        this.durationMinutes = null;
        this.distanceKm = null;
        this.caloriesBurned = null;
        this.notes = '';
    }

    async logActivity() {
        if (!this.isChecked) {
            const alert = await this.alertController.create({
                header: 'Confirmation Required',
                message: 'Please tick the checkbox to confirm you completed this activity before saving.',
                buttons: ['OK']
            });
            await alert.present();
            return;
        }

        this.saving = true;

        const request: ActivityLogRequest = {
            habitId: this.card.habitId,
            dateOfActivity: this.today,
            durationMinutes: this.durationMinutes || null,
            distanceKm: this.distanceKm || null,
            caloriesBurned: this.caloriesBurned || null,
            note: this.notes || undefined
        };

        this.activityLogService.logHabit(request).subscribe(
    async () => {
        this.saving = false;
        const alert = await this.alertController.create({
            header: 'Activity Logged',
            message: `${this.card.activityName} logged successfully.`,
            cssClass: 'app-success-alert',
            buttons: ['OK']
        });
        await alert.present();
        await alert.onDidDismiss();
        this.showExtraLogControls = false;
        this.resetForm();
        this.activityLogged.emit();
    },
    async () => {
        this.saving = false;
        const alert = await this.alertController.create({
            header: 'Error',
            message: 'Failed to log activity. Please try again.',
            buttons: ['OK']
        });
        await alert.present();
    }
);
    }
}