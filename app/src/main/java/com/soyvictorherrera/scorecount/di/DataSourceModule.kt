package com.soyvictorherrera.scorecount.di

import com.soyvictorherrera.scorecount.data.datasource.LocalMatchDataSource
import com.soyvictorherrera.scorecount.data.datasource.MatchDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun bindMatchDataSource(impl: LocalMatchDataSource): MatchDataSource

}
