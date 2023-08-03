package com.example.privacyapp.feature_PrivacyDashboard.domain.model.samples

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App

class SampleAppProvider: PreviewParameterProvider<App> {
    override val values: Sequence<App> = sequenceOf(
        App("Test1", "test1", true, true, true, 5, true, true),
        App("Test2", "test2",true, true, false, 5, false, true)
        )
}