package com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.google.accompanist.drawablepainter.rememberDrawablePainter

/**
 * A composable that represents an individual app item in the list, displaying its icon, name, and usage visualization.
 *
 * @param app The App object representing the app to display.
 * @param cumulativeUsage The total cumulative usage across all apps for scaling purposes.
 * @param modifier The modifier to apply to the layout.
 */
@Composable
fun AppItem(
    app: App,
    cumulativeUsage: Int,
    modifier: Modifier = Modifier
    ){
    // Calculate the width of the location usage box based on the percentage of total usage
    var locationUsedBoxWidthInPercentage = 0f
    if (cumulativeUsage != 0) {
        locationUsedBoxWidthInPercentage = (100/cumulativeUsage.toFloat()) * app.numberOfEstimatedRequests
    }


    Card(modifier = modifier
        .clip(RoundedCornerShape(10.dp))
        //.background(color = Color.White)
    ) {
        Row(modifier = Modifier
            .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Display the app icon
            Box(
                Modifier
                    .height(45.dp)
                    .width(45.dp)) {
                Image(painter = rememberDrawablePainter(getAppIcon(app.packageName)), contentDescription = null)
            }

            Spacer(modifier = Modifier.width(10.dp))
            Column() {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = app.appName, style = MaterialTheme.typography.bodyLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if(app.ACCESS_BACKGROUND_LOCATION){
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Warning",
                                tint = Color.Yellow, // Set the desired color
                                modifier = Modifier.size(24.dp) // Adjust size as needed
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        if(app.preinstalled){
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .border(1.dp, Color.Yellow, shape = RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "S",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Yellow
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(text = String.format("%.2f", locationUsedBoxWidthInPercentage) + "%", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                // Display the usage visualization bar
                Box() {
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .height(10.dp)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.tertiary)
                    )
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .height(10.dp)
                        .fillMaxWidth(locationUsedBoxWidthInPercentage / 100)
                        .background(color = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

/**
 * Retrieves the app icon based on the provided package name.
 *
 * @param packageName The package name of the app.
 * @return The Drawable representing the app icon.
 */
private fun getAppIcon(packageName: String): Drawable? {
    val packageManager: PackageManager =
        ApplicationProvider.application.applicationContext.packageManager

    try
    {
        return packageManager.getApplicationIcon(packageName)
    }
    catch (e: PackageManager.NameNotFoundException)
    {
        e.printStackTrace()
    }
    return null
}