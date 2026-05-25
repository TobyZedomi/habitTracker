import { Component, OnInit, ViewChild } from '@angular/core';
import {
    IonContent, IonHeader, IonToolbar, IonTitle, IonButtons, IonButton,
    IonSegment, IonSegmentButton, IonLabel, IonIcon, IonCard, IonCardHeader,
    IonCardContent, IonCardTitle, IonCardSubtitle, IonChip, IonSpinner, IonText
} from '@ionic/angular/standalone';
import { ActivatedRoute, Router } from '@angular/router';
import { HabitService } from '../services/habit.service';
import { HabitResponse } from '../models/habit.model';
import { ActivityLogService } from '../services/activityLog.service';
import { ActivityLogCardResponse } from '../models/activityLog.model';
import { AlertController } from '@ionic/angular';
import { CommonModule } from '@angular/common';
import { addIcons } from 'ionicons';
import {
    pulseOutline, documentTextOutline, timeOutline, flagOutline,
    repeatOutline, checkmarkCircleOutline, flameOutline, trophyOutline,
    calendarOutline, addCircleOutline, createOutline, trashOutline,
    fitnessOutline, listOutline, readerOutline, logOutOutline
} from 'ionicons/icons';
import { ActivityLogCard } from '../activity-log-card/activity-log-card';
import { AiCoachService } from '../services/aiCoach.service';
 
@Component({
    selector: 'app-tab2',
    templateUrl: 'tab2.page.html',
    styleUrls: ['tab2.page.scss'],
    standalone: true,
    imports: [
        CommonModule,
        IonContent, IonHeader, IonToolbar, IonTitle, IonButtons, IonButton,
        IonSegment, IonSegmentButton, IonLabel, IonIcon,
        IonCard, IonCardHeader, IonCardContent, IonCardTitle, IonCardSubtitle,
        IonChip, IonSpinner, IonText,
        ActivityLogCard
    ]
})
export class Tab2Page implements OnInit {
 
    @ViewChild(IonContent) content!: IonContent;
 
    activeSegment: string = 'log';
 
    weeklyCards: ActivityLogCardResponse[] = [];
    cardsLoading: boolean = false;
    cardsError: string = '';
 
    habits: HabitResponse[] = [];
    habitsLoading: boolean = false;
    habitsError: string = '';
 
    newHabitId: number | null = null;
    highlightedHabitId: number | null = null;
    focusHabitId: number | null = null;
 
    constructor(
        private habitService: HabitService,
        private activityLogService: ActivityLogService,
        private router: Router,
        private route: ActivatedRoute,
        private alertController: AlertController,
        private aiCoachService: AiCoachService
    ) {
        addIcons({
            pulseOutline, documentTextOutline, timeOutline, flagOutline,
            repeatOutline, checkmarkCircleOutline, flameOutline, trophyOutline,
            calendarOutline, addCircleOutline, createOutline, trashOutline,
            fitnessOutline, listOutline, readerOutline, logOutOutline
        });
    }
 
    ngOnInit() {
        this.loadWeeklyCards();
        this.loadHabits();
    }
 
    ionViewWillEnter() {
        this.route.queryParams.subscribe(params => {
            if (params['newHabitId']) {
                this.newHabitId = +params['newHabitId'];
                this.activeSegment = 'habits';
            }
            if (params['focusHabitId']) {
                this.focusHabitId = +params['focusHabitId'];
                this.activeSegment = 'log';
            }
        });
        this.loadWeeklyCards();
        this.loadHabits();
    }
 
    loadWeeklyCards() {
        this.cardsLoading = true;
        this.cardsError = '';
        this.activityLogService.getWeeklyActivityCards().subscribe(
    cards => {
        this.weeklyCards = cards;
        this.cardsLoading = false;
        if (this.focusHabitId) {
            const id = this.focusHabitId;
            this.focusHabitId = null;
            setTimeout(() => this.scrollToCard(id), 350);
        }
    },
    () => {
        this.cardsError = 'Failed to load activity cards. Please try again.';
        this.cardsLoading = false;
    }
);
    }
 
    scrollToCard(habitId: number) {
        const el = document.getElementById('log-card-' + habitId);
        if (el) {
            el.scrollIntoView({ behavior: 'smooth', block: 'center' });
            el.classList.add('card-highlight');
            setTimeout(() => el.classList.remove('card-highlight'), 2500);
        }
    }
 
    loadHabits() {
        this.habitsLoading = true;
        this.habitsError = '';
        this.habitService.getAllHabitsForCurrentUser().subscribe(
    habits => {
        this.habits = habits;
        this.habitsLoading = false;
        if (this.newHabitId) {
            const id = this.newHabitId;
            this.newHabitId = null;
            this.highlightedHabitId = id;
            setTimeout(() => this.scrollToHabit(id), 300);
            setTimeout(() => { this.highlightedHabitId = null; }, 3000);
        }
    },
    () => {
        this.habitsError = 'Failed to load habits. Please try again.';
        this.habitsLoading = false;
    }
);
    }
 
    scrollToHabit(habitId: number) {
        const el = document.getElementById('habit-card-' + habitId);
        if (el) {
            el.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    }
 
    onActivityLogged() {
        this.loadWeeklyCards();
    }
 
    goToLogActivityForHabit(habitId: number) {
        this.activeSegment = 'log';
        setTimeout(() => this.scrollToCard(habitId), 200);
    }
 
    goToCreateHabit() {
        this.router.navigate(['/tabs/create-habit']);
    }
 
    goToHabitDetail(habitId: number) {
        this.router.navigate(['/tabs/habit-detail', habitId]);
    }
 
    goToEditHabit(habitId: number) {
        this.router.navigate(['/tabs/edit-habit', habitId]);
    }
 
    async confirmDeleteHabit(habitId: number, activityName: string) {
    const confirm = await this.alertController.create({
        header: 'Delete Habit',
        message: `Are you sure you want to delete ${activityName}? This cannot be undone.`,
        buttons: [
            { text: 'Cancel', role: 'cancel' },
            { text: 'Continue', role: 'destructive' }
        ]
    });
    await confirm.present();
    const { role } = await confirm.onDidDismiss();

    if (role === 'destructive') {
        const deleteConfirm = await this.alertController.create({
            header: 'Delete Habit',
            message: `Press OK to permanently delete ${activityName}.`,
            buttons: [
                { text: 'Cancel', role: 'cancel' },
                { text: 'OK', role: 'confirm' }
            ]
        });
        await deleteConfirm.present();
        const { role: finalRole } = await deleteConfirm.onDidDismiss();

        if (finalRole === 'confirm') {
            this.habitService.deleteHabit(habitId).subscribe(
    async () => {
        const alert = await this.alertController.create({
            header: 'Deleted',
            message: `${activityName} has been deleted.`,
            cssClass: 'app-success-alert',
            buttons: ['OK']
        });
        await alert.present();
        await alert.onDidDismiss();
        this.aiCoachService.refreshInsights().subscribe();
        this.loadWeeklyCards();
        this.loadHabits();
    },
    async () => {
        const alert = await this.alertController.create({
            header: 'Error',
            message: 'Failed to delete habit. Please try again.',
            buttons: ['OK']
        });
        await alert.present();
    }
);
        }
    }
}
 
    async logout() {
        await this.router.navigateByUrl('/login', { replaceUrl: true });
    }
}