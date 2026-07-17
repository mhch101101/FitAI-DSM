package app.dsm.fitai.di

import android.app.Application
import android.content.Context
import app.dsm.fitai.data.local.preferences.UserPreferencesRepository
import app.dsm.fitai.data.remote.api.RoutineApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object AppModule {

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        app: Application
    ): UserPreferencesRepository {
        return UserPreferencesRepository(app.applicationContext)
    }

    @Provides
    fun provideContext(app: Application): Context {
        return app.applicationContext
    }

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://afwlmtoygploiueelkus.supabase.co/")
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    @Provides
    fun provideRoutineApi(
        retrofit: Retrofit
    ): RoutineApi {
        return retrofit.create(
            RoutineApi::class.java
        )
    }

}