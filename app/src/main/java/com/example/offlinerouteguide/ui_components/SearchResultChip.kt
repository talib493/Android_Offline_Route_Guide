package com.example.offlinerouteguide.ui_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
fun SearchResultChip(
cityName: String,
countryName: String,
name: String,
onPressNavigate:()-> Unit
) {
    var city = ""
    if (!cityName.isBlank()){
        city = cityName+", "
    }
    Column (
        modifier = Modifier.padding(start = 8.dp,end=8.dp).background(color=Color.White)
        .fillMaxWidth()
    ){
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.padding(4.dp).weight(0.5f),
                painter = painterResource(R.drawable.location_on_24px),
                contentDescription = "location"
            )
            Spacer(modifier = Modifier.weight(0.1f))
            Column(modifier = Modifier.weight(3f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${city}${countryName}",
                    style = MaterialTheme.typography.titleSmall
                )
            }
            IconButton(
                onClick = { onPressNavigate() }) {
            Icon(
                modifier = Modifier.padding(0.dp).weight(0.4f),
                painter = painterResource(R.drawable.arrow_insert_24px),
                contentDescription = "location"
            )
        }
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Color.Gray)
    }

}