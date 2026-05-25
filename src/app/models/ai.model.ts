export interface AiRecommendation {
    type: string;
    title: string;
    author: string;
    channel: string;
    description: string;
    url: string;
    s3Key: string;
}
 
export interface AiInsightResponse {
    insightText: string;
    recommendations: AiRecommendation[];
    generatedAt: number;
    s3Key: string;
}
 
export interface AiChatResponse {
    reply: string;
    recommendations: AiRecommendation[];
    conversationId: string;
    timestamp: number;
}
 
export interface ChatMessage {
    role: string;
    content: string;
    timestamp: number;
    recommendations: AiRecommendation[];
}