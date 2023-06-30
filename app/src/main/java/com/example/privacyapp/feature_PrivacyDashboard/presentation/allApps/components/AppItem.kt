package com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.components

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.samples.SampleAppProvider
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.google.accompanist.drawablepainter.rememberDrawablePainter

//@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AppItem(
    @PreviewParameter(SampleAppProvider::class) app: App,
    maxLocationUsage: Int,
    modifier: Modifier = Modifier
    ){
    var locationUsedBoxWidthInPercentage = 0f
    if (maxLocationUsage != 0) {
        locationUsedBoxWidthInPercentage = (100/maxLocationUsage.toFloat()) * app.numberOfEstimatedRequests
    }


    Card(modifier = modifier
        .clip(RoundedCornerShape(10.dp))
        .background(color = Color.White)
    ) {
        Row(modifier = Modifier
            .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(Modifier.height(45.dp).width(45.dp)) {
                Image(painter = rememberDrawablePainter(getAppIcon(app.packageName)), contentDescription = null)
            }

            Spacer(modifier = Modifier.width(10.dp))
            Column() {
                Text(text = app.appName, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(10.dp))
                Box() {
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .height(10.dp)
                        .fillMaxWidth()
                        .background(color = Color.DarkGray)
                    )
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .height(10.dp)
                        .fillMaxWidth(locationUsedBoxWidthInPercentage/100)
                        .background(color = Color.Green)
                    )
                }
            }
        }
    }
}

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