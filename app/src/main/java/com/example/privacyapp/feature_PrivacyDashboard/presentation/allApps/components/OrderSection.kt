package com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppPermissionFilter
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
    appFilter: AppPermissionFilter = AppPermissionFilter(false, false, false, false),
    onOrderChange: (AppOrder) -> Unit,
    onFilterChange: (AppPermissionFilter) -> Unit
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
        Spacer(modifier = Modifier.height(16.dp))
        Divider(Modifier.fillMaxWidth())
        Row(Modifier.fillMaxWidth()) {
            DefaultRadioButton(
                text = "No Location",
                selected = appFilter.none,
                onSelect = {
                    if(appFilter.none){
                        onFilterChange(appFilter.copy(none = false))
                    }else {
                        onFilterChange(appFilter.copy(none = true, coarseLocation = false, fineLocation = false, backgroundLocation = false))
                    }
                }
            )
            DefaultRadioButton(
                text = "Coarse Location",
                selected = appFilter.coarseLocation,
                onSelect = {
                    if(appFilter.coarseLocation){
                        onFilterChange(appFilter.copy(coarseLocation = false, fineLocation = false, backgroundLocation = false))
                    }else {
                        onFilterChange(appFilter.copy(none = false, coarseLocation = true, fineLocation = false, backgroundLocation = false))
                    }
                }
            )
        }
        Row(Modifier.fillMaxWidth()) {
            DefaultRadioButton(
                text = "Fine location",
                selected = appFilter.fineLocation,
                onSelect = {
                    if(appFilter.fineLocation){
                        onFilterChange(appFilter.copy(fineLocation = false, backgroundLocation = false))
                    }else {
                        onFilterChange(appFilter.copy(none = false, coarseLocation = true, fineLocation = true, backgroundLocation = false))
                    }
                }
            )
            DefaultRadioButton(
                text = "Background Location",
                selected = appFilter.backgroundLocation,
                onSelect = {
                    if(appFilter.backgroundLocation){
                        onFilterChange(appFilter.copy(backgroundLocation = false))
                    }else {
                        onFilterChange(appFilter.copy(none = false, coarseLocation = true, fineLocation = true, backgroundLocation = true))
                    }
                }
            )
        }
    }
}