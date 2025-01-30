package com.smithmicro.notes.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smithmicro.notes.R

@Composable
fun NotesEmptyWarningBox(hasNotNotes: Boolean) {
    if (hasNotNotes) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_empty_notes),
                colorFilter = ColorFilter.tint(Color.LightGray),
                contentDescription = "-",
                modifier = Modifier
                    .size(148.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = stringResource(R.string.no_notes_to_display),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}