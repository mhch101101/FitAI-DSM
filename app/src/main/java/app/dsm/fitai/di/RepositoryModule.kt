package app.dsm.fitai.di

import app.dsm.fitai.data.repository.AuthRepositoryImpl
import app.dsm.fitai.data.repository.RoutineRepositoryImpl
import app.dsm.fitai.data.repository.UserRepositoryImpl
import app.dsm.fitai.domain.repository.AuthRepository
import app.dsm.fitai.domain.repository.RoutineRepository
import app.dsm.fitai.domain.repository.UserRepository
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindRoutineRepository(
        impl: RoutineRepositoryImpl
    ): RoutineRepository

}