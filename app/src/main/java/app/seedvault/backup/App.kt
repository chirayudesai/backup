package app.seedvault.backup

import android.Manifest.permission.INTERACT_ACROSS_USERS_FULL
import android.app.Application
import android.app.backup.BackupManager.PACKAGE_MANAGER_SENTINEL
import android.app.backup.IBackupManager
import android.content.Context
import android.content.Context.BACKUP_SERVICE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.ServiceManager.getService
import android.os.StrictMode
import android.os.UserHandle
import app.seedvault.backup.crypto.cryptoModule
import app.seedvault.backup.header.headerModule
import app.seedvault.backup.metadata.MetadataManager
import app.seedvault.backup.metadata.metadataModule
import app.seedvault.backup.plugins.saf.documentsProviderModule
import app.seedvault.backup.restore.RestoreViewModel
import app.seedvault.backup.restore.install.installModule
import app.seedvault.backup.settings.AppListRetriever
import app.seedvault.backup.settings.SettingsManager
import app.seedvault.backup.settings.SettingsViewModel
import app.seedvault.backup.storage.storageModule
import app.seedvault.backup.transport.backup.backupModule
import app.seedvault.backup.transport.restore.restoreModule
import app.seedvault.backup.ui.files.FileSelectionViewModel
import app.seedvault.backup.ui.notification.BackupNotificationManager
import app.seedvault.backup.ui.recoverycode.RecoveryCodeViewModel
import app.seedvault.backup.ui.storage.BackupStorageViewModel
import app.seedvault.backup.ui.storage.RestoreStorageViewModel
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

/**
 * @author Steve Soltys
 * @author Torsten Grote
 */
open class App : Application() {

    private val appModule = module {
        single { SettingsManager(this@App) }
        single { BackupNotificationManager(this@App) }
        single { Clock() }
        factory<IBackupManager> { IBackupManager.Stub.asInterface(getService(BACKUP_SERVICE)) }
        factory { AppListRetriever(this@App, get(), get(), get()) }

        viewModel { SettingsViewModel(this@App, get(), get(), get(), get(), get(), get(), get()) }
        viewModel { RecoveryCodeViewModel(this@App, get(), get(), get(), get(), get(), get()) }
        viewModel { BackupStorageViewModel(this@App, get(), get(), get(), get()) }
        viewModel { RestoreStorageViewModel(this@App, get(), get()) }
        viewModel { RestoreViewModel(this@App, get(), get(), get(), get(), get(), get()) }
        viewModel { FileSelectionViewModel(this@App, get()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin()
        if (isDebugBuild()) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyFlashScreen()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
        permitDiskReads {
            migrateTokenFromMetadataToSettingsManager()
        }
    }

    protected open fun startKoin() = startKoin {
        androidLogger(Level.ERROR)
        androidContext(this@App)
        modules(
            listOf(
                cryptoModule,
                headerModule,
                metadataModule,
                documentsProviderModule, // storage plugin
                backupModule,
                restoreModule,
                installModule,
                storageModule,
                appModule
            )
        )
    }

    private val settingsManager: SettingsManager by inject()
    private val metadataManager: MetadataManager by inject()

    /**
     * The responsibility for the current token was moved to the [SettingsManager]
     * in the end of 2020.
     * This method migrates the token for existing installs and can be removed
     * after sufficient time has passed.
     */
    private fun migrateTokenFromMetadataToSettingsManager() {
        @Suppress("DEPRECATION")
        val token = metadataManager.getBackupToken()
        if (token != 0L && settingsManager.getToken() == null) {
            settingsManager.setNewToken(token)
        }
    }

}

const val MAGIC_PACKAGE_MANAGER = PACKAGE_MANAGER_SENTINEL
const val ANCESTRAL_RECORD_KEY = "@ancestral_record@"
const val GLOBAL_METADATA_KEY = "@meta@"

// TODO this doesn't work for LineageOS as they do public debug builds
fun isDebugBuild() = Build.TYPE == "userdebug"

fun <T> permitDiskReads(func: () -> T): T {
    return if (isDebugBuild()) {
        val oldThreadPolicy = StrictMode.getThreadPolicy()
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder(oldThreadPolicy)
                .permitDiskReads()
                .build()
        )
        try {
            func()
        } finally {
            StrictMode.setThreadPolicy(oldThreadPolicy)
        }
    } else {
        func()
    }
}

fun Context.getSystemContext(isUsbStorage: () -> Boolean): Context {
    return if (checkSelfPermission(INTERACT_ACROSS_USERS_FULL) == PERMISSION_GRANTED &&
        isUsbStorage()
    ) createContextAsUser(UserHandle.SYSTEM, 0) else this
}
