package com.example.offlinerouteguide.ui_components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.offlinerouteguide.dataStructures.PhotonFeature

@Composable
fun SearchResultScrollable(
    list: List<PhotonFeature>,
    onNavigate:(PhotonFeature)-> Unit,
    modifier: Modifier = Modifier,
    currentLocItem : @Composable () -> Unit
){
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        item {
            currentLocItem()
        }
        items(list){item->
            SearchResultChip(
                name = item.properties.name?:"",
                cityName = item.properties.city?:"",
                countryName = item.properties.country?:"",
                onPressNavigate = {
                    onNavigate(item)
                }
            )


        }

    }
}