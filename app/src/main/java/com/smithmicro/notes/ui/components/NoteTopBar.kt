package com.smithmicro.notes.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.smithmicro.notes.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteTopBar(
    title: String = "",
    navigationIconClick: (() -> Unit)? = null,
    colorIcon: Color = Color.Gray,
    extraIconClick: (() -> Unit)? = null,
    extraIconResId: Int = R.drawable.ic_check,
    colorBackground: Color = Color.White
) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorBackground
        ),
        navigationIcon = {
            navigationIconClick?.let {
                IconButton(
                    onClick = { it() }
                ) {
                    Icon(painter = painterResource(R.drawable.ic_back), contentDescription = "", tint = colorIcon)
                }
            }
        },
        actions = {
            extraIconClick?.let {
                IconButton(onClick = {
                    it()
                }) {
                    Icon(painter = painterResource(extraIconResId), contentDescription = "")
                }
            }
        }
    )
}