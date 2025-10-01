package com.soyvictorherrera.scorecount.di

import com.soyvictorherrera.scorecount.data.datasource.LocalScoreDataSource
import com.soyvictorherrera.scorecount.data.repository.ScoreRepositoryImpl
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideLocalScoreDataSource(): LocalScoreDataSource {
        return LocalScoreDataSource()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindScoreRepository(scoreRepositoryImpl: ScoreRepositoryImpl): ScoreRepository
}
