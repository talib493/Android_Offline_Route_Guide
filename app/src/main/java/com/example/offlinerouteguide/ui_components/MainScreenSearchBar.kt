package com.example.offlinerouteguide.ui_components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.offlinerouteguide.R

@Composable
fun MainScreenSearchBar(
    padding: Int,
    onValueUpdate:(String)-> Unit,
    value: String,
    onSearchClick:()-> Unit,
    onCrossClick:()-> Unit,
    placeholderValue: String

){
    val painter = if (value.isNotBlank()){
        painterResource(R.drawable.close_24px)
    }else{
        painterResource(R.drawable.search_24px)
    }
    Card(modifier = Modifier.fillMaxWidth().padding(padding.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,

                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,

                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,

                    focusedLabelColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray
                ),
                value = value,
                onValueChange = {
                    onValueUpdate(it)
                },
                modifier = Modifier.weight(3f),
                leadingIcon = {
                    Icon(painter = painterResource(R.drawable.location_on_24px),
                        contentDescription = "location",
                        tint = Color.Green
                    )
                },
                placeholder =  { Text(text = placeholderValue) }
            )
            IconButton(onClick = {
                if (value.isNotBlank()){
                onCrossClick()
            }else{
                onSearchClick()
            }
            })

            {
                Icon(
                    painter = painter,
                    contentDescription = "Search",
                    modifier = Modifier.weight(0.5f)
                )
            }
        }
    }
}