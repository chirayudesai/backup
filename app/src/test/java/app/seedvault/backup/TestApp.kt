package app.seedvault.backup

import app.seedvault.backup.crypto.CipherFactory
import app.seedvault.backup.crypto.CipherFactoryImpl
import app.seedvault.backup.crypto.Crypto
import app.seedvault.backup.crypto.CryptoImpl
import app.seedvault.backup.crypto.KeyManager
import app.seedvault.backup.crypto.KeyManagerTestImpl
import app.seedvault.backup.header.headerModule
import app.seedvault.backup.metadata.metadataModule
import app.seedvault.backup.plugins.saf.documentsProviderModule
import app.seedvault.backup.restore.install.installModule
import app.seedvault.backup.transport.backup.backupModule
import app.seedvault.backup.transport.restore.restoreModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class TestApp : App() {

    private val testCryptoModule = module {
        factory<CipherFactory> { CipherFactoryImpl(get()) }
        single<KeyManager> { KeyManagerTestImpl() }
        single<Crypto> { CryptoImpl(get(), get(), get()) }
    }
    private val appModule = module {
        single { Clock() }
    }

    override fun startKoin() = startKoin {
        androidContext(this@TestApp)
        modules(
            listOf(
                testCryptoModule,
                headerModule,
                metadataModule,
                documentsProviderModule, // storage plugin
                backupModule,
                restoreModule,
                installModule,
                appModule
            )
        )
    }
}
