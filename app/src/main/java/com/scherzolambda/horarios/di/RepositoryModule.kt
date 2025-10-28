package com.scherzolambda.horarios.di

import com.scherzolambda.horarios.data_transformation.api.repositories.AuthRepository
import com.scherzolambda.horarios.data_transformation.api.repositories.IAuthRepository
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
    abstract fun bindAuthRepository(authRepository: AuthRepository): IAuthRepository
}

