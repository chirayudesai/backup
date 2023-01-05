package app.seedvault.backup.plugins.saf

import app.seedvault.backup.plugins.LegacyStoragePlugin
import app.seedvault.backup.plugins.StoragePlugin
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val documentsProviderModule = module {
    single { DocumentsStorage(androidContext(), get()) }

    single<StoragePlugin> { DocumentsProviderStoragePlugin(androidContext(), get()) }
    @Suppress("Deprecation")
    single<LegacyStoragePlugin> { DocumentsProviderLegacyPlugin(androidContext(), get()) }
}
