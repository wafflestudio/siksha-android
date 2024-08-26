package com.wafflestudio.siksha2.compose.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@Composable
fun ConfirmDeleteDialog(
    onDismissRequest: () -> Unit,
    onConfirmDelete: () -> Unit,
    onCancelDelete: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .width(315.dp)
                .height(130.27.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(26.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFFE3E3E3),
                    shape = RoundedCornerShape(26.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "게시글 삭제",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "게시글을 정말 삭제하시겠습니까?",
                modifier = Modifier.padding(16.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // "취소" Button
                TextButton(
                    onClick = onCancelDelete,
                    enabled = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "취소",
                        color = Color(0xFFFF9522),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = onConfirmDelete,
                    enabled = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
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
    var isDialogVisible by remember { mutableStateOf(true) }

    if (isDialogVisible) {
        ConfirmDeleteDialog(
            onDismissRequest = { isDialogVisible = false },
            onConfirmDelete = {
                // Confirm delete action
                println("Confirmed delete")
                isDialogVisible = false
            },
            onCancelDelete = {
                // Cancel action
                println("Cancelled")
                isDialogVisible = false
            }
        )
    }
}
