package app.dsm.fitai.di

import android.app.Application
import app.dsm.fitai.domain.repository.AuthRepository
import app.dsm.fitai.domain.repository.RoutineRepository
import app.dsm.fitai.domain.repository.UserRepository
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        RepositoryModule::class,
        DatabaseModule::class
    ]
)
interface AppComponent {

    fun userRepository(): UserRepository
    fun authRepository(): AuthRepository
    fun routineRepository(): RoutineRepository

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance app: Application
        ): AppComponent
    }
}