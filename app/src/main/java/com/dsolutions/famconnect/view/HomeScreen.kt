package com.dsolutions.famconnect.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dsolutions.famconnect.R
import com.dsolutions.famconnect.view.common.HeaderRow
import com.dsolutions.famconnect.view.common.Screen

@Composable
fun HomeScreen(
    onNavigateTo: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HeaderRow(
            drawableRes = R.drawable.ic_logo_unlabeled,
            contentDescription = "Logo",
            title = "FAMconnect"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MainMenuButton(
                text = "Family Calendar",
                drawableRes = R.drawable.ic_calendar,
                //icon = Icons.Default.CalendarMonth,
                onClick = { onNavigateTo(Screen.Calendar.route) },
                modifier = Modifier.weight(1f),
                contentDescription = "Family Calendar"
            )

            MainMenuButton(
                text = "Tasks",
                drawableRes = R.drawable.ic_tasks,
                //icon = Icons.Default.List,
                onClick = { onNavigateTo(Screen.Tasks.route) },
                modifier = Modifier.weight(1f),
                contentDescription = "Tasks"
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MainMenuButton(
                text = "Meal Plan",
                drawableRes = R.drawable.ic_meal,
                //icon = Icons.Default.Restaurant,
                onClick = { onNavigateTo(Screen.Meals.route) },
                modifier = Modifier.weight(1f),
                contentDescription = "Meal Plan"
            )

            MainMenuButton(
                text = "Shopping List",
                drawableRes = R.drawable.ic_shopping,
                //icon = Icons.Default.ShoppingCart,
                onClick = { onNavigateTo(Screen.Shopping.route) },
                modifier = Modifier.weight(1f),
                contentDescription = "Shopping List"
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MainMenuButton(
                text = "Time Plan",
                drawableRes = R.drawable.ic_clock,
                //icon = Icons.Default.Schedule,
                onClick = { onNavigateTo(Screen.TimePlan.route) },
                modifier = Modifier.weight(1f),
                contentDescription = "Time Plan"
            )

            MainMenuButton(
                text = "Vacation",
                drawableRes = R.drawable.ic_vacation,
                //icon = Icons.Default.BeachAccess,
                onClick = { onNavigateTo(Screen.Vacation.route) },
                modifier = Modifier.weight(1f),
                contentDescription = "Vacation"
            )
        }
    }
}