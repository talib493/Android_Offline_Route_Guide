package com.example.offlinerouteguide.ui_components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.offlinerouteguide.models.MainScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoBottomSheet(
    name: String,
    viewModel: MainScreenViewModel = viewModel(),
    onDirectionsClick:()->Unit

){
    ModalBottomSheet(
        containerColor = Color.White,
        contentColor = Color.Black,
        onDismissRequest = {
            viewModel.infoSheet=false
        },
        modifier = Modifier.fillMaxWidth(),
    ){
        Column(modifier = Modifier.fillMaxWidth().padding(15.dp)) {
            Text(text = name , style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(onClick = {
                onDirectionsClick()
            }) {
                    Text(text = "Directions")
            }

        }
    }

}