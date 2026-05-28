package app.dsm.fitai.di

import app.dsm.fitai.domain.repository.AuthRepository
import app.dsm.fitai.domain.repository.UserRepository
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        RepositoryModule::class
    ]
)
interface AppComponent {

    fun userRepository(): UserRepository

    fun authRepository(): AuthRepository
}