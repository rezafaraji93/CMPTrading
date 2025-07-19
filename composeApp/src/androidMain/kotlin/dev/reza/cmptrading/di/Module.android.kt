package dev.reza.cmptrading.di

import androidx.room.RoomDatabase
import dev.reza.cmptrading.core.database.getPortfolioDatabaseBuilder
import dev.reza.cmptrading.core.database.portfolio.PortfolioDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> { Android.create() }
        singleOf(::getPortfolioDatabaseBuilder).bind<RoomDatabase.Builder<PortfolioDatabase>>()
    }