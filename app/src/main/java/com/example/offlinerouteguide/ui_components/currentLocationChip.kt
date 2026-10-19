package com.example.offlinerouteguide.ui_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.offlinerouteguide.R

@Composable
fun currentLocationChip(
    name: String,
    onPressNavigate:()-> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .background(color = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 50.dp) // Ensures standard height thickness
                .padding(horizontal = 8.dp, vertical = 8.dp), // Restores internal spacing
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(4.dp),
                painter = painterResource(R.drawable.home_pin_24px),
                contentDescription = "location",
                tint = Color.Blue
            )

            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )

            IconButton(
                onClick = { onPressNavigate() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_insert_24px),
                    contentDescription = "navigate"
                )
            }
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Color.Gray)
    }
}