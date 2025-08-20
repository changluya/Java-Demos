// src/api/chatApi.js
import { streamService } from '@/utils/request'

/**
 * Send chat message with streaming response
 * @param {string} sessionId - The chat session ID
 * @param {string} message - The message content
 * @param {Function} onDownloadProgress - Progress callback function
 * @returns {Promise} Axios promise with stream response
 */
export function chatStream(sessionId, message, mode, onDownloadProgress) {
    return streamService({
        url: '/demo03/chat',
        method: 'post',
        data: { sessionId, message, mode },
        responseType: 'stream',
        onDownloadProgress: onDownloadProgress,
        timeout: 300000 // 5分钟
    })
}