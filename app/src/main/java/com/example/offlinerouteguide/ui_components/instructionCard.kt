package com.example.offlinerouteguide.ui_components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.offlinerouteguide.R

@Composable
fun InstructionCard(
    instruction:String,
    distance: String
){
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 20.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        modifier = Modifier.fillMaxWidth().padding(20.dp).height(140.dp)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.size(100.dp).weight(1f).padding(10.dp),
                    painter = painterResource(R.drawable.arrow_insert_24px),
                    contentDescription = "instruction icon"
                )
                Text(
                    modifier = Modifier.weight(1.5f).padding(10.dp),
                    text = instruction, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Text(modifier = Modifier.padding(10.dp), text = "Total Distance Left: "+distance, style = MaterialTheme.typography.titleMedium)
        }

    }
}