package app.seedvault.backup.transport.backup

import android.os.ParcelFileDescriptor
import app.seedvault.backup.transport.TransportTest
import io.mockk.mockk
import java.io.OutputStream

internal abstract class BackupTest : TransportTest() {

    protected val inputFactory = mockk<InputFactory>()
    protected val data = mockk<ParcelFileDescriptor>()
    protected val outputStream = mockk<OutputStream>()
    protected val encryptedOutputStream = mockk<OutputStream>()

    protected val quota = DEFAULT_QUOTA_FULL_BACKUP

}
