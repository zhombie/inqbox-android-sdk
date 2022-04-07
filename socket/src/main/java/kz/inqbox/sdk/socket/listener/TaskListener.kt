package kz.inqbox.sdk.socket.listener

import kz.inqbox.sdk.socket.model.TaskMessage

interface TaskListener {
    fun onTaskMessage(taskMessage: TaskMessage)
}