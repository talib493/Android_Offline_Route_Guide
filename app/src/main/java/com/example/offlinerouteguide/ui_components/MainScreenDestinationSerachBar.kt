package com.example.offlinerouteguide.ui_components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.offlinerouteguide.R

@Composable
fun MainScreenDirectionSearchBar(
    padding: Int,
    onStartValueUpdate:(String)-> Unit,
    startValue: String,
    onHideClick:()-> Unit,
    onSearchClick:()-> Unit,
    startPlaceholderValue: String,
    destPlaceholderValue: String,
    onDestValueUpdate:(String)-> Unit,
    destValue: String,
    startState : (Boolean)-> Unit,
    destState : (Boolean) -> Unit
){
    Card(modifier = Modifier.fillMaxWidth().padding(padding.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    value = startValue,
                    onValueChange = {
                        onStartValueUpdate(it)
                    },
                    modifier = Modifier.weight(3f).onFocusChanged{focusState ->
                        startState(focusState.isFocused)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.home_pin_24px),
                            contentDescription = "location",
                            tint = Color.Green
                        )
                    },
                    placeholder = { Text(text = startPlaceholderValue, color = Color.DarkGray) }
                )
                IconButton(onClick = {
                    onHideClick()
                })
                { Icon(
                        painter = painterResource(R.drawable.visibility_off_24px),
                        contentDescription = "Search",
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    value = destValue,
                    onValueChange = {
                        onDestValueUpdate(it)
                    },
                    modifier = Modifier.weight(3f).onFocusChanged{focusState ->
                        destState(focusState.isFocused)
            },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.location_on_24px),
                            contentDescription = "location",
                            tint = Color.Red
                        )
                    },
                    placeholder = { Text(text = destPlaceholderValue, color = Color.DarkGray) }
                )
                IconButton(onClick = {
                    onSearchClick()
                })

                {
                    Icon(
                        painter = painterResource(R.drawable.search_24px),
                        contentDescription = "Search",
                    )
                }
            }
        }
    }

}