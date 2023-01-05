package app.seedvault.backup.header

import org.koin.dsl.module

val headerModule = module {
    single<HeaderReader> { HeaderReaderImpl() }
}
