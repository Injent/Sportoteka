package ru.master.app.data.messaging

import android.app.Notification
import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.master.app.R
import ru.master.app.data.settings.SettingsRepository
import ru.master.app.network.ServiceApi
import ru.master.app.util.getOrElse

private const val FCM_TOKEN = "fcmToken"

class FirebaseTokenWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val data = SettingsRepository.data.first()

        if (data.accessToken == null) {
            return@withContext Result.failure()
        }

        ServiceApi.registerFcmToken(inputData.getString(FCM_TOKEN)!!)
            .getOrElse {
                return@withContext if (runAttemptCount > 2) {
                    Result.failure()
                } else Result.retry()
            }

        Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return applicationContext.getForegroundInfoForFmc()
    }

    companion object {
        fun start(context: Context, token: String) {
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "fmcSync",
                    ExistingWorkPolicy.KEEP,
                    OneTimeWorkRequestBuilder<FirebaseTokenWorker>()
                        .setConstraints(
                            Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build()
                        )
                        .setInputData(
                            inputData = workDataOf(
                                FCM_TOKEN to token
                            )
                        )
                        .build()
                )
        }
    }
}

private const val SILENT_CHANNEL_ID = "silent"

private fun Context.getForegroundInfoForFmc(): ForegroundInfo {
    val notification = Notification.Builder(this, SILENT_CHANNEL_ID)
        .setSmallIcon(R.drawable.notification_icon)
        .setContentTitle(getString(R.string.syncing))
        .setContentText(getString(R.string.token_registration))
        .build()

    return ForegroundInfo(3, notification)
}