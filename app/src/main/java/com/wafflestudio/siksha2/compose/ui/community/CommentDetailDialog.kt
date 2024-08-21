package com.wafflestudio.siksha2.compose.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wafflestudio.siksha2.ui.SikshaColors

@Composable
fun CommentDetailDialog(
    onDismissRequest: () -> Unit,
    onClickReply: () -> Unit,
    onClickReport: () -> Unit,
    onClickCancel: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(315.dp)
                .height(165.dp)
                .background(Color.White, shape = RoundedCornerShape(26.dp))
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(26.dp)
                )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(26.dp)),
                shape = RoundedCornerShape(26.dp),
                color = Color.White
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "댓글달기",
                        fontSize = 16.sp,
                        color = Color(0xFF797979),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onClickReply)
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                    Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                    Text(
                        text = "신고하기",
                        fontSize = 16.sp,
                        color = Color(0xFF797979),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onClickReport)
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White, RoundedCornerShape(26.dp)),
                shape = RoundedCornerShape(26.dp),
                color = Color.White
            ) {
                Text(
                    text = "취소",
                    fontSize = 16.sp,
                    color = Color(0xFFFF9522),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            onClickCancel()
                        })
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommentDetailDialogPreview() {
    CommentDetailDialog(
        onDismissRequest = {},
        onClickReply = { },
        onClickReport = {},
        onClickCancel = {}
    )
}
