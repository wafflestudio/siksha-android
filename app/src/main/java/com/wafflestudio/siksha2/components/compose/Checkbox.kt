package com.wafflestudio.siksha2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wafflestudio.siksha2.ui.SikshaColors

@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier
) {
    Box( // TODO: 디자인 확정 후 수정
        modifier = modifier
            .background(
                color = if (checked) MaterialTheme.colors.primary else SikshaColors.Gray400,
                shape = RoundedCornerShape(3.dp)
            )
            .then(
                onCheckedChange?.let {
                    Modifier.clickable { it(checked.not()) }
                } ?: Modifier
            )
    )
}
