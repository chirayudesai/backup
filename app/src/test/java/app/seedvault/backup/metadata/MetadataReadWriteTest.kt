package app.seedvault.backup.metadata

import app.seedvault.backup.crypto.CipherFactoryImpl
import app.seedvault.backup.crypto.CryptoImpl
import app.seedvault.backup.crypto.KEY_SIZE_BYTES
import app.seedvault.backup.crypto.KeyManagerTestImpl
import app.seedvault.backup.getRandomBase64
import app.seedvault.backup.getRandomString
import app.seedvault.backup.header.HeaderReaderImpl
import app.seedvault.backup.header.VERSION
import app.seedvault.backup.metadata.PackageState.APK_AND_DATA
import app.seedvault.backup.metadata.PackageState.WAS_STOPPED
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

@TestInstance(PER_CLASS)
internal class MetadataReadWriteTest {

    private val secretKey = SecretKeySpec(
        "This is a legacy backup key 1234".toByteArray(), 0, KEY_SIZE_BYTES, "AES"
    )
    private val keyManager = KeyManagerTestImpl(secretKey)
    private val cipherFactory = CipherFactoryImpl(keyManager)
    private val headerReader = HeaderReaderImpl()
    private val cryptoImpl = CryptoImpl(keyManager, cipherFactory, headerReader)

    private val writer = MetadataWriterImpl(cryptoImpl)
    private val reader = MetadataReaderImpl(cryptoImpl)

    private val packages = HashMap<String, PackageMetadata>().apply {
        put(getRandomString(), PackageMetadata(Random.nextLong(), APK_AND_DATA, BackupType.FULL))
        put(getRandomString(), PackageMetadata(Random.nextLong(), WAS_STOPPED, BackupType.KV))
    }

    @Test
    fun `written metadata matches read metadata`() {
        val metadata = getMetadata(packages)
        val outputStream = ByteArrayOutputStream()

        writer.write(metadata, outputStream)

        val inputStream = ByteArrayInputStream(outputStream.toByteArray())

        assertEquals(metadata, reader.readMetadata(inputStream, metadata.token))
    }

    private fun getMetadata(
        packageMetadata: HashMap<String, PackageMetadata> = HashMap(),
    ): BackupMetadata {
        return BackupMetadata(
            version = VERSION,
            token = Random.nextLong(),
            salt = getRandomBase64(32),
            time = Random.nextLong(),
            androidVersion = Random.nextInt(),
            androidIncremental = getRandomString(),
            deviceName = getRandomString(),
            packageMetadataMap = packageMetadata
        )
    }

}
