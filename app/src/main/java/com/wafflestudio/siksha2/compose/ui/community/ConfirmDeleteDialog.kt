package com.wafflestudio.siksha2.compose.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTheme

@Composable
fun ConfirmDeleteDialog(
    title: String,
    description: String,
    onDismissRequest: () -> Unit,
    onConfirmDelete: () -> Unit,
    onCancelDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = modifier
                .background(
                    color = SikshaColors.White900,
                    shape = RoundedCornerShape(26.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = description,
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFE0E0E0))
            Row(
                modifier = Modifier.height(50.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(onClick = onCancelDelete),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "취소",
                        color = SikshaColors.OrangeMain,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Divider(
                    color = Color(0xFFE0E0E0),
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(onClick = onConfirmDelete),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "삭제",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ConfirmDeleteDialogPreview() {
    SikshaTheme {
        ConfirmDeleteDialog(
            title = "게시글 삭제",
            description = "게시글을 정말 삭제하시겠습니까?",
            onDismissRequest = {},
            onConfirmDelete = {},
            onCancelDelete = {}
        )
    }
}
