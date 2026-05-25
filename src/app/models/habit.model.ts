
export interface HabitRequest {
    habitId?: number;
    activityTypeId: number;
    description: string;
    reminder: string;
    target: number;
    frequency: number;
}

export interface HabitResponse {
    habitId: number;
    activityName: string;
    activityStatusText: string;
    description: string;
    reminder: string;
    frequency: number;
    target: string;
    active: boolean;
    currentStreak: number;
    longestStreak: number;
    lastCompletedDate?: string;
    weeklyStreak: number;
}



export interface HabitListResponse {
    habits: HabitResponse[];
}