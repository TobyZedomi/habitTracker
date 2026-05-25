export interface ActivityLogRequest {
    habitId: number;
    dateOfActivity: string; 
    durationMinutes?: number | null;
    distanceKm?: number | null;
    caloriesBurned?: number | null;
    note?: string;
}

export interface ActivityLogResponse {
    habitTrackerLogId: number;
    habitId: number;
    activityName: string;
    date: string; 
    duration?: number;
    distanceKm?: number;
    caloriesBurned?: number;
    notes?: string;
    createdAt: string;
}

export interface ActivityLogDetailResponseRaw {
    habit_tracker_log_id: number;
    habit_id: number;
    date_of_activity: string;
    duration_minutes?: number | null;
    distance_km?: number | null;
    calories_burned?: number | null;
    notes?: string;
    created_at: string;
}

export interface ActivityLogCardResponse {
    habitId: number;
    activityName: string;
    activityStatusText: string;
    frequency: number;
    weeklyCompletedCount: number;
    weeklyGoalCompleted: boolean;
    progressLabel: string;
}

export interface ActivityLogListResponse {
    logs: ActivityLogResponse[];
}