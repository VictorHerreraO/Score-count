package com.soyvictorherrera.scorecount.di

import com.soyvictorherrera.scorecount.data.repository.MatchRepositoryImpl
import com.soyvictorherrera.scorecount.data.repository.ScoreRepositoryImpl
import com.soyvictorherrera.scorecount.data.repository.SettingsRepositoryImpl
import com.soyvictorherrera.scorecount.domain.repository.MatchRepository
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindScoreRepository(impl: ScoreRepositoryImpl): ScoreRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindMatchRepository(impl: MatchRepositoryImpl): MatchRepository
}
