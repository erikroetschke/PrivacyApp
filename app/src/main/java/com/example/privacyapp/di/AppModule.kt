package com.example.privacyapp.di

import android.app.Application
import androidx.room.Room
import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.AppDatabase
import com.example.privacyapp.feature_PrivacyDashboard.data.repository.AppRepositoryImpl
import com.example.privacyapp.feature_PrivacyDashboard.data.repository.AppUsageRepositoryImpl
import com.example.privacyapp.feature_PrivacyDashboard.data.repository.PreferencesManagerImpl
import com.example.privacyapp.feature_PrivacyDashboard.data.repository.LocationRepositoryImpl
import com.example.privacyapp.feature_PrivacyDashboard.data.repository.POIRepositoryImpl
import com.example.privacyapp.feature_PrivacyDashboard.data.repository.PrivacyAssessmentRepositoryImpl
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PreferencesManager
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.POIRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
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
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.GetAppsSuspend
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.DeleteLocationsOlderThanTimestamp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.GetLocationsWithLocationUsedIsNull
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.GetUsedLocationsLastSinceTimestamp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.AddPrivacyAssessment
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.DeletePrivacyAssessment
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.DoAssessment
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.ExtractPOIsLast24h
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.GetAssessment1dByMetricSinceTimestamp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.GetPOISinceTimestampAsFlow
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.UpdatePOIs
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases.ComputeUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases.DeleteAppUsageOlderThanTimestamp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases.GetAppUsageSinceTimestamp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases.UpdateAppUsageLast24Hours
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
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
    fun provideDatabase(app: Application): AppDatabase {
        //get Application to provide it in other classes, as this method will be called even before onCreate() at the MainActivity
        ApplicationProvider.initialize(app)
        //provide db
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )/*.fallbackToDestructiveMigration()*/.build()
    }

    @Provides
    @Singleton
    fun provideLocationRepository(db: AppDatabase): LocationRepository {
        return LocationRepositoryImpl(db.locationDao)
    }

    @Provides
    @Singleton
    fun provideLocationUseCases(repository: LocationRepository): LocationUseCases {
        return LocationUseCases(
            getLocations = GetLocations(repository),
            addLocation = AddLocation(repository),
            getLocationsWithLocationUsedIsNull = GetLocationsWithLocationUsedIsNull(repository),
            getUsedLocationsLastSinceTimestamp = GetUsedLocationsLastSinceTimestamp(repository),
            deleteLocationsOlderThanTimestamp = DeleteLocationsOlderThanTimestamp(repository)
        )
    }

    @Provides
    @Singleton
    fun provideAppRepository(db: AppDatabase): AppRepository {
        return AppRepositoryImpl(db.appDao)
    }

    @Provides
    @Singleton
    fun provideAppUsageRepository(db: AppDatabase): AppUsageRepository {
        return AppUsageRepositoryImpl(db.appUsageDao)
    }
    @Provides
    @Singleton
    fun providePrivacyAssessmentRepository(db: AppDatabase): PrivacyAssessmentRepository {
        return PrivacyAssessmentRepositoryImpl(db.privacyAssessment1dDao)
    }

    @Provides
    @Singleton
    fun providePOIRepository(db: AppDatabase): POIRepository {
        return POIRepositoryImpl(db.pOIDao)
    }

    @Provides
    @Singleton
    fun provideSharedPrefs(app: Application): PreferencesManager {
        return PreferencesManagerImpl(app)
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
            deleteApp = DeleteApp(repository),
            getAppsSuspend = GetAppsSuspend(repository)
        )
    }

    @Provides
    @Singleton
    fun provideAppUsageUseCases(repository: AppUsageRepository, locationRepository: LocationRepository, appRepository: AppRepository): AppUsageUseCases {
        return AppUsageUseCases(
            computeUsage = ComputeUsage(repository, locationRepository, appRepository),
            updateAppUsageLast24Hours = UpdateAppUsageLast24Hours(repository, appRepository),
            getAppUsageSinceTimestamp = GetAppUsageSinceTimestamp(repository),
            deleteAppUsageOlderThanTimestamp = DeleteAppUsageOlderThanTimestamp(repository)
        )
    }
    @Provides
    @Singleton
    fun providePrivacyAssessmentUseCases(repository: PrivacyAssessmentRepository, locationRepository: LocationRepository, poiRepository: POIRepository): PrivacyAssessmentUseCases {
        return PrivacyAssessmentUseCases(
            addPrivacyAssessment = AddPrivacyAssessment(repository),
            deletePrivacyAssessment = DeletePrivacyAssessment(repository),
            getAssessment1dByMetricSinceTimestamp = GetAssessment1dByMetricSinceTimestamp(repository),
            doAssessment = DoAssessment(repository, poiRepository),
            extractPOIsLast24h = ExtractPOIsLast24h(locationRepository, poiRepository),
            getPOISinceTimestampAsFlow = GetPOISinceTimestampAsFlow(poiRepository),
            updatePOIs = UpdatePOIs(poiRepository, locationRepository)
        )
    }
}