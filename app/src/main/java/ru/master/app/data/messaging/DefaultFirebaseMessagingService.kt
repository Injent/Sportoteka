package ru.master.app.data.messaging

import com.google.firebase.messaging.FirebaseMessagingService

class DefaultFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        FirebaseTokenWorker.start(this, token)
    }
}
