import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
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
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.SikshaTypography

@Composable
fun PostDetailDialog(
    onDismissRequest: () -> Unit,
    onClickEdit: () -> Unit,
    onClickDelete: () -> Unit,
    onClickReport: () -> Unit,
    onClickCopyUrl: () -> Unit,
    onClickCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    PostDetailDialogItem(text = "신고하기", onClick = onClickReport)
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = modifier.padding(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .width(343.dp)
                    .background(Color.White, RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                color = Color.White
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 0.dp)
                ) {
                    Text(
                        text = "게시글 메뉴",
                        color = Color(0xFF575757),
                        modifier = Modifier.padding(top = 10.dp, bottom = 5.dp),
                        style = SikshaTypography.body1,
                        fontSize = 10.sp
                    )

                    Divider(color = Color(0xFFDFDFDF), thickness = 1.dp)
                    PostDetailDialogItem(text = "수정하기", onClick = onClickEdit)
                    Divider(color = Color(0xFFDFDFDF), thickness = 1.dp)
                    PostDetailDialogItem(text = "삭제하기", onClick = onClickDelete)
                    Divider(color = Color(0xFFDFDFDF), thickness = 1.dp)
                    PostDetailDialogItem(text = "신고하기", onClick = onClickReport)
                    Divider(color = Color(0xFFDFDFDF), thickness = 1.dp)
                    PostDetailDialogItem(text = "URL 복사하기", onClick = onClickCopyUrl)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .width(343.dp)
                    .height(50.dp)
                    .background(Color.White, RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                color = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClickCancel),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "취소",
                        color = Color(0xFF0066FF),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        style = SikshaTypography.body1
                    )
                }
            }
        }
    }
}

@Composable
fun PostDetailDialogItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFF0066FF),
            fontSize = 16.sp,
            style = SikshaTypography.body1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PostDetailDialogPreview() {
    SikshaTheme {
        PostDetailDialog(
            onDismissRequest = {},
            onClickEdit = {},
            onClickDelete = {},
            onClickReport = {},
            onClickCopyUrl = {},
            onClickCancel = {}
        )
    }
}
