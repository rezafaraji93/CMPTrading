package dev.reza.cmptrading.di

import androidx.room.Room
import androidx.room.RoomDatabase
import dev.reza.cmptrading.coins.data.remote.impl.KtorCoinsRemoteDataSource
import dev.reza.cmptrading.coins.domain.api.CoinsRemoteDataSource
import dev.reza.cmptrading.coins.domain.usecase.GetCoinDetailUseCase
import dev.reza.cmptrading.coins.domain.usecase.GetCoinPriceHistoryUseCase
import dev.reza.cmptrading.coins.domain.usecase.GetCoinsListUseCase
import dev.reza.cmptrading.coins.presentation.CoinsListViewModel
import dev.reza.cmptrading.core.database.portfolio.PortfolioDatabase
import dev.reza.cmptrading.core.database.portfolio.getPortfolioDatabase
import dev.reza.cmptrading.core.network.HttpClientFactory
import dev.reza.cmptrading.portfolio.data.PortfolioRepositoryImpl
import dev.reza.cmptrading.portfolio.domain.PortfolioRepository
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) =
    startKoin {
        config?.invoke(this)
        modules(
            platformModule,
            sharedModule
        )
    }


expect val platformModule: Module

val sharedModule = module {
    // core
    single<HttpClient> { HttpClientFactory.create(get()) }

    // portfolio
    single {
        getPortfolioDatabase(
            get<RoomDatabase.Builder<PortfolioDatabase>>()
        )
    }
    singleOf(::PortfolioRepositoryImpl).bind<PortfolioRepository>()
    viewModel { CoinsListViewModel(get(), get()) }
    singleOf(::GetCoinsListUseCase)
    singleOf(::KtorCoinsRemoteDataSource).bind<CoinsRemoteDataSource>()
    singleOf(::GetCoinDetailUseCase)
    singleOf(::GetCoinPriceHistoryUseCase)
}