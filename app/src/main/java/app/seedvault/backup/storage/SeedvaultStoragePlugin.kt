package app.seedvault.backup.storage

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import app.seedvault.backup.crypto.KeyManager
import app.seedvault.backup.getSystemContext
import app.seedvault.backup.plugins.saf.DocumentsStorage
import org.calyxos.backup.storage.plugin.saf.SafStoragePlugin
import javax.crypto.SecretKey

internal class SeedvaultStoragePlugin(
    private val appContext: Context,
    private val storage: DocumentsStorage,
    private val keyManager: KeyManager,
) : SafStoragePlugin(appContext) {
    /**
     * Attention: This context might be from a different user. Use with care.
     */
    override val context: Context
        get() = appContext.getSystemContext {
            storage.storage?.isUsb == true
        }
    override val root: DocumentFile
        get() = storage.rootBackupDir ?: error("No storage set")

    override fun getMasterKey(): SecretKey = keyManager.getMainKey()
    override fun hasMasterKey(): Boolean = keyManager.hasMainKey()
}
