package app.seedvault.backup.transport.restore

import android.os.ParcelFileDescriptor
import app.seedvault.backup.getRandomByteArray
import app.seedvault.backup.header.HeaderReader
import app.seedvault.backup.header.VERSION
import app.seedvault.backup.transport.TransportTest
import io.mockk.mockk
import java.io.InputStream

internal abstract class RestoreTest : TransportTest() {

    protected val outputFactory = mockk<OutputFactory>()
    protected val headerReader = mockk<HeaderReader>()
    protected val fileDescriptor = mockk<ParcelFileDescriptor>()

    protected val data = getRandomByteArray()
    protected val inputStream = mockk<InputStream>()
    protected val decryptedInputStream = mockk<InputStream>()

    protected val unsupportedVersion = (VERSION + 1).toByte()

}
