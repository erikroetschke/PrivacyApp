package com.example.privacyapp.feature_PrivacyDashboard.presentation.settings.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents.InfoDialog

@Composable
fun SwitchSetting(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    infoText:String, onInfoClick: () -> Unit, infoDialogVisible: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = Color.Gray
            )
        )
        IconButton(
            onClick = {
                onInfoClick()
            }
        ) {
            Icon(imageVector = Icons.Default.Info, contentDescription = "Info")
        }

        if(infoDialogVisible){
            InfoDialog(infoText = infoText, onDismiss = {onInfoClick()})
        }
    }
}