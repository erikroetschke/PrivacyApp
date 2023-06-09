package com.example.privacyapp.di

import android.app.Application
import androidx.room.Room
import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.Database
import com.example.privacyapp.feature_PrivacyDashboard.data.repository.AppRepositoryImpl
import com.example.privacyapp.feature_PrivacyDashboard.data.repository.AppUsageRepositoryImpl
import com.example.privacyapp.feature_PrivacyDashboard.data.repository.LocationRepositoryImpl
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUsageUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.AddApp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.AddLocation
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.DeleteAllApps
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.DeleteApp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.GetApp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.GetApps
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.GetFavoriteApps
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.GetLocations
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.InitApps
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.LocationUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.PrivacyAssessmentUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.GetLocationsWithLocationUsedIsNull
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.GetNumberOfPOI
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases.ComputeUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases.GetAppUsageSinceTimestamp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases.UpdateAppUsageLast24Hours
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
        ).fallbackToDestructiveMigration().build()
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
            addLocation = AddLocation(repository),
            getLocationsWithLocationUsedIsNull = GetLocationsWithLocationUsedIsNull(repository)
        )
    }

    @Provides
    @Singleton
    fun provideAppRepository(db: Database): AppRepository {
        return AppRepositoryImpl(db.appDao)
    }

    @Provides
    @Singleton
    fun provideAppUsageRepository(db: Database): AppUsageRepository {
        return AppUsageRepositoryImpl(db.appUsageDao)
    }

    @Provides
    @Singleton
    fun provideAppUseCases(repository: AppRepository): AppUseCases {
        return AppUseCases(
            getApps = GetApps(repository),
            addApp = AddApp(repository),
            deleteAllApps = DeleteAllApps(repository),
            initApps = InitApps(),
            getApp = GetApp(repository),
            getFavoriteApps = GetFavoriteApps(repository),
            deleteApp = DeleteApp(repository)
        )
    }

    @Provides
    @Singleton
    fun provideAppUsageUseCases(repository: AppUsageRepository, locationRepository: LocationRepository, appRepository: AppRepository): AppUsageUseCases {
        return AppUsageUseCases(
            computeUsage = ComputeUsage(repository, locationRepository, appRepository),
            updateAppUsageLast24Hours = UpdateAppUsageLast24Hours(repository, appRepository),
            getAppUsageSinceTimestamp = GetAppUsageSinceTimestamp(repository)
        )
    }
    @Provides
    @Singleton
    fun providePrivacyAssessmentUseCases(): PrivacyAssessmentUseCases {
        return PrivacyAssessmentUseCases(
            getNumberOfPOI = GetNumberOfPOI()
        )
    }
}