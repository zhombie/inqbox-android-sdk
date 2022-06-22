package kz.qbox.sdk.socket.listener

import kz.qbox.sdk.socket.model.TaskMessage

interface TaskListener {
    fun onTaskMessage(taskMessage: TaskMessage)
}