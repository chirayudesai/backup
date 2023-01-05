package app.seedvault.backup.restore

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.core.net.toUri
import app.seedvault.backup.ui.notification.BackupNotificationManager
import org.koin.core.context.GlobalContext.get

internal const val ACTION_RESTORE_ERROR_UNINSTALL = "app.seedvault.backup.action.UNINSTALL"
internal const val EXTRA_PACKAGE_NAME = "app.seedvault.backup.extra.PACKAGE_NAME"
internal const val REQUEST_CODE_UNINSTALL = 4576841

class RestoreErrorBroadcastReceiver : BroadcastReceiver() {

    // using KoinComponent would crash robolectric tests :(
    private val notificationManager: BackupNotificationManager by lazy { get().get() }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_RESTORE_ERROR_UNINSTALL) return

        notificationManager.onRestoreErrorSeen()

        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)!!

        @Suppress("DEPRECATION") // the alternative doesn't work for us
        val i = Intent(Intent.ACTION_UNINSTALL_PACKAGE).apply {
            data = "package:$packageName".toUri()
            flags = FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(i)
    }

}
