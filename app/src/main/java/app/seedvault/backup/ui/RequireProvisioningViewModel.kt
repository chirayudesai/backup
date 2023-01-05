package app.seedvault.backup.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import app.seedvault.backup.crypto.KeyManager
import app.seedvault.backup.settings.SettingsManager
import app.seedvault.backup.ui.storage.StorageViewModel

abstract class RequireProvisioningViewModel(
    protected val app: Application,
    protected val settingsManager: SettingsManager,
    protected val keyManager: KeyManager,
) : AndroidViewModel(app) {

    abstract val isRestoreOperation: Boolean

    private val mChooseBackupLocation = MutableLiveEvent<Boolean>()
    internal val chooseBackupLocation: LiveEvent<Boolean> get() = mChooseBackupLocation
    internal fun chooseBackupLocation() = mChooseBackupLocation.setEvent(true)

    internal fun validLocationIsSet() = StorageViewModel.validLocationIsSet(app, settingsManager)

    internal fun recoveryCodeIsSet() = keyManager.hasBackupKey()

    open fun onStorageLocationChanged() {
        // noop can be overwritten by sub-classes
    }

}
