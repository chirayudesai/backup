package app.seedvault.backup.metadata

import app.seedvault.backup.crypto.Crypto
import app.seedvault.backup.getRandomBase64
import app.seedvault.backup.getRandomString
import app.seedvault.backup.metadata.PackageState.APK_AND_DATA
import app.seedvault.backup.metadata.PackageState.NOT_ALLOWED
import app.seedvault.backup.metadata.PackageState.NO_DATA
import app.seedvault.backup.metadata.PackageState.QUOTA_EXCEEDED
import app.seedvault.backup.metadata.PackageState.WAS_STOPPED
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import kotlin.random.Random

@TestInstance(PER_CLASS)
internal class MetadataWriterDecoderTest {

    private val crypto = mockk<Crypto>()

    private val encoder = MetadataWriterImpl(crypto)
    private val decoder = MetadataReaderImpl(crypto)

    @Test
    fun `encoded metadata matches decoded metadata (no packages)`() {
        val metadata = getMetadata()
        assertEquals(
            metadata,
            decoder.decode(encoder.encode(metadata), metadata.version, metadata.token)
        )
    }

    @Test
    fun `encoded metadata matches decoded metadata (with package, no apk info)`() {
        val time = Random.nextLong()
        val packages = HashMap<String, PackageMetadata>().apply {
            put(getRandomString(), PackageMetadata(time, APK_AND_DATA, BackupType.FULL))
            put(getRandomString(), PackageMetadata(time, WAS_STOPPED, BackupType.KV))
        }
        val metadata = getMetadata(packages)
        assertEquals(
            metadata,
            decoder.decode(encoder.encode(metadata), metadata.version, metadata.token)
        )
    }

    @Test
    fun `encoded metadata matches decoded metadata (full package)`() {
        val packages = HashMap<String, PackageMetadata>().apply {
            put(
                getRandomString(), PackageMetadata(
                    time = Random.nextLong(),
                    state = APK_AND_DATA,
                    backupType = BackupType.FULL,
                    version = Random.nextLong(),
                    installer = getRandomString(),
                    splits = listOf(
                        ApkSplit(getRandomString(), getRandomString()),
                        ApkSplit(getRandomString(), getRandomString()),
                        ApkSplit(getRandomString(), getRandomString())
                    ),
                    sha256 = getRandomString(),
                    signatures = listOf(getRandomString(), getRandomString())
                )
            )
        }
        val metadata = getMetadata(packages)
        assertEquals(
            metadata,
            decoder.decode(encoder.encode(metadata), metadata.version, metadata.token)
        )
    }

    @Test
    fun `encoded metadata matches decoded metadata (three full packages)`() {
        val packages = HashMap<String, PackageMetadata>().apply {
            put(
                getRandomString(), PackageMetadata(
                    time = Random.nextLong(),
                    state = QUOTA_EXCEEDED,
                    backupType = BackupType.FULL,
                    system = Random.nextBoolean(),
                    version = Random.nextLong(),
                    installer = getRandomString(),
                    sha256 = getRandomString(),
                    signatures = listOf(getRandomString())
                )
            )
            put(
                getRandomString(), PackageMetadata(
                    time = Random.nextLong(),
                    state = NO_DATA,
                    backupType = BackupType.KV,
                    system = Random.nextBoolean(),
                    version = Random.nextLong(),
                    installer = getRandomString(),
                    sha256 = getRandomString(),
                    signatures = listOf(getRandomString(), getRandomString())
                )
            )
            put(
                getRandomString(), PackageMetadata(
                    time = 0L,
                    state = NOT_ALLOWED,
                    system = Random.nextBoolean(),
                    version = Random.nextLong(),
                    installer = getRandomString(),
                    sha256 = getRandomString(),
                    signatures = listOf(getRandomString(), getRandomString())
                )
            )
        }
        val metadata = getMetadata(packages)
        assertEquals(
            metadata,
            decoder.decode(encoder.encode(metadata), metadata.version, metadata.token)
        )
    }

    private fun getMetadata(
        packageMetadata: HashMap<String, PackageMetadata> = HashMap(),
    ): BackupMetadata {
        return BackupMetadata(
            version = Random.nextBytes(1)[0],
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
