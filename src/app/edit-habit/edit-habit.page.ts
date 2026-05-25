import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
    IonHeader, IonToolbar, IonTitle, IonContent, IonButton, IonButtons,
    IonIcon, IonCard, IonCardContent, IonItem, IonInput, IonSelect,
    IonSelectOption, IonTextarea, IonLabel, IonText, IonSpinner, IonChip
} from '@ionic/angular/standalone';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HabitService } from '../services/habit.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AlertController } from '@ionic/angular';
import { HabitResponse, HabitRequest } from '../models/habit.model';
import { ActivityTypeService } from '../services/activityType.service';
import { ActivityType } from '../models/activityType.model';
import { addIcons } from 'ionicons';
import { arrowBackOutline } from 'ionicons/icons';
import { AiCoachService } from '../services/aiCoach.service';

@Component({
    selector: 'app-edit-habit',
    templateUrl: './edit-habit.page.html',
    styleUrls: ['./edit-habit.page.scss'],
    standalone: true,
    imports: [
        CommonModule, ReactiveFormsModule,
        IonHeader, IonToolbar, IonTitle, IonContent, IonButton, IonButtons,
        IonIcon, IonCard, IonCardContent, IonItem, IonInput, IonSelect,
        IonSelectOption, IonTextarea, IonLabel, IonText, IonSpinner, IonChip
    ]
})
export class EditHabitPage implements OnInit {

    habitId!: number;
    habits: HabitResponse | null = null;
    habitForm!: FormGroup;
    activityTypes: ActivityType[] = [];
    loading = false;
    errorMessage = '';

    constructor(
        private habitService: HabitService,
        private router: Router,
        private route: ActivatedRoute,
        private activityTypeService: ActivityTypeService,
        private alertController: AlertController,
        private formBuilder: FormBuilder,
        private aiCoachService: AiCoachService
    ) {
        addIcons({ arrowBackOutline });
    }

    ngOnInit() {
        this.buildForm();
        this.loadHabitIdFromRoute();
        this.loadActivityTypes();
        this.loadHabitDetails();
    }

    loadHabitIdFromRoute() {
        const param = this.route.snapshot.paramMap.get('habitId');
        if (param) this.habitId = Number(param);
    }

    buildForm() {
        this.habitForm = this.formBuilder.group({
            habitId: [null],
            activityTypeId: ['', Validators.required],
            description: ['', Validators.required],
            reminder: ['', Validators.required],
            frequency: ['', Validators.required],
            target: ['', Validators.required]
        });
    }

    loadActivityTypes() {
        this.activityTypeService.getAllActivityTypes().subscribe(
            (types: ActivityType[]) => {
                this.activityTypes = types;
                if (this.habits) this.patchFormValues();
            },
            () => { this.showErrorAlert('Failed to load activity types. Please try again later.'); }
        );
    }

    loadHabitDetails() {
        if (!this.habitId) {
            this.showErrorAlert('Invalid habit ID. Cannot load habit details.');
            return;
        }
        this.habitService.getOneHabitById(this.habitId).subscribe(
            habit => {
                this.habits = habit;
                this.patchFormValues();
            },
            () => { this.showErrorAlert('Failed to load habit details. Please try again later.'); }
        );
    }

    patchFormValues() {
        if (!this.habits) return;
        const matchedType = this.activityTypes.find(t => t.name === this.habits!.activityName);
        this.habitForm.patchValue({
            habitId: this.habits.habitId,
            activityTypeId: matchedType ? matchedType.activityTypeId : '',
            description: this.habits.description || '',
            reminder: this.habits.reminder || '',
            target: this.habits.target || '',
            frequency: this.habits.frequency || ''
        });
    }

    onSubmit() {
        if (this.habitForm.invalid) {
            this.habitForm.markAllAsTouched();
            this.errorMessage = 'Please fill in all required fields.';
            return;
        }

        const formValue = this.habitForm.value;
        const habitData: HabitRequest = {
            habitId: formValue.habitId,
            activityTypeId: Number(formValue.activityTypeId),
            description: formValue.description,
            reminder: formValue.reminder,
            frequency: Number(formValue.frequency),
            target: Number(formValue.target)
        };

        const oldName = this.habits ? this.habits.activityName : 'this habit';
        const newType = this.activityTypes.find(
            t => t.activityTypeId === Number(formValue.activityTypeId)
        )?.name || oldName;

        const changes: string[] = [];
        if (this.habits) {
            if (newType !== oldName) changes.push(`Activity Type: ${oldName} changed to ${newType}`);
            if (formValue.description !== this.habits.description) changes.push(`Description: changed to ${formValue.description}`);
            if (formValue.reminder !== this.habits.reminder) changes.push(`Reminder: changed to ${formValue.reminder}`);
            if (Number(formValue.frequency) !== this.habits.frequency) changes.push(`Frequency: changed to ${formValue.frequency}x per week`);
            if (formValue.target !== this.habits.target) changes.push(`Target: changed to ${formValue.target}`);
        }

        const changeText = changes.length > 0 ? changes.join('\n') : 'No changes were made';

        this.habitService.updateHabit(habitData).subscribe(
            async () => {
                const alert = await this.alertController.create({
                    header: `${oldName} has been updated`,
                    message: changeText,
                    cssClass: 'app-success-alert',
                    buttons: ['OK']
                });
                this.aiCoachService.refreshInsights().subscribe();
                await alert.present();
                await alert.onDidDismiss();
                this.router.navigate(['/tabs/tab2']);
            },
            async (error) => {
    const message = error?.error || 'Failed to update habit. Please try again later.';
    this.showErrorAlert(message);
}
        );
    }

    async showErrorAlert(message: string) {
        const alert = await this.alertController.create({
            message,
            buttons: ['OK']
        });
        await alert.present();
    }

    async logout() {
        await this.router.navigateByUrl('/login', { replaceUrl: true });
    }

    goBackToTab2() {
        this.router.navigate(['/tabs/tab2'], { replaceUrl: true });
    }
}