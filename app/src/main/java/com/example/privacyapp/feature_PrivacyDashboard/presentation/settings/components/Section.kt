package com.example.privacyapp.feature_PrivacyDashboard.presentation.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Composable function that creates a settings section with a title and content.
 *
 * @param title The title of the section.
 * @param content The content of the section, provided as a composable lambda.
 */
@Composable
fun Section(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Divider(modifier = Modifier.padding(top = 4.dp))
        content()
    }
}