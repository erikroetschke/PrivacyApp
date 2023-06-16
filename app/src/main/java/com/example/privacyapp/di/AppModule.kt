package com.example.privacyapp.di

import android.app.Application
import androidx.room.Room
import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.AppDao
import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.Database
import com.example.privacyapp.feature_PrivacyDashboard.data.repository.AppRepositoryImpl
import com.example.privacyapp.feature_PrivacyDashboard.data.repository.LocationRepositoryImpl
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AddApp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AddLocation
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.DeleteAllApps
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.GetAppIcons
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.GetApps
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.GetLocations
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.InitApps
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.LocationUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): Database {
        return Room.databaseBuilder(
            app,
            Database::class.java,
            Database.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideLocationRepository(db: Database): LocationRepository {
        return LocationRepositoryImpl(db.locationDao)
    }

    @Provides
    @Singleton
    fun provideLocationUseCases(repository: LocationRepository): LocationUseCases {
        return LocationUseCases(
            getLocations = GetLocations(repository),
            addLocation = AddLocation(repository)
        )
    }

    @Provides
    @Singleton
    fun provideAppRepository(db: Database): AppRepository {
        return AppRepositoryImpl(db.appDao)
    }

    @Provides
    @Singleton
    fun provideAppUseCases(repository: AppRepository): AppUseCases {
        return AppUseCases(
            getApps = GetApps(repository),
            addApp = AddApp(repository),
            deleteAllApps = DeleteAllApps(repository),
            initApps = InitApps()
        )
    }
}