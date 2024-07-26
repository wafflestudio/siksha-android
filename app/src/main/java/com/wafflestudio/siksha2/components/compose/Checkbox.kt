package com.wafflestudio.siksha2.components.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.wafflestudio.siksha2.R

@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier
) {
    Image(
        modifier = modifier
            .then(
                onCheckedChange?.let {
                    Modifier.clickable { it(checked.not()) }
                } ?: Modifier
            ),
        painter = painterResource(if (checked) R.drawable.ic_checkbox_checked else R.drawable.ic_checkbox_unchecked),
        contentDescription = ""
    )
}
