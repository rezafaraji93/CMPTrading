package dev.reza.cmptrading.di

import androidx.room.RoomDatabase
import dev.reza.cmptrading.core.database.getPortfolioDatabaseBuilder
import dev.reza.cmptrading.core.database.portfolio.PortfolioDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> { Darwin.create() }
        singleOf(::getPortfolioDatabaseBuilder).bind<RoomDatabase.Builder<PortfolioDatabase>>()
    }