package com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.OrderType
import com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents.DefaultRadioButton


/**
 * A Composable function representing a section for selecting sorting orders for app notes.
 *
 * @param modifier The modifier to apply to the layout.
 * @param appOrder The current order of the apps.
 * @param onOrderChange The callback function to invoke when the order changes.
 */
@Composable
fun OrderSection(
    modifier: Modifier = Modifier,
    appOrder: AppOrder = AppOrder.Title(OrderType.Ascending),
    onOrderChange: (AppOrder) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            DefaultRadioButton(
                text = "Title",
                selected = appOrder is AppOrder.Title,
                onSelect = { onOrderChange(AppOrder.Title(appOrder.orderType)) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButton(
                text = "Possible location usage",
                selected = appOrder is AppOrder.LocationUsage,
                onSelect = { onOrderChange(AppOrder.LocationUsage(appOrder.orderType)) }
            )

        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            DefaultRadioButton(
                text = "Ascending",
                selected = appOrder.orderType is OrderType.Ascending,
                onSelect = {
                    onOrderChange(appOrder.copy(OrderType.Ascending))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButton(
                text = "Descending",
                selected = appOrder.orderType is OrderType.Descending,
                onSelect = {
                    onOrderChange(appOrder.copy(OrderType.Descending))
                }
            )
        }
    }
}