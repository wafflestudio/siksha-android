import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.ui.DeleteIcon
import com.wafflestudio.siksha2.ui.EditIcon
import com.wafflestudio.siksha2.ui.ReportIcon
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.SikshaTypography



@Composable
fun PostDetailDialog(
    onDismissRequest: () -> Unit,
    onClickEdit: () -> Unit,
    onClickDelete: () -> Unit,
    onClickReport: () -> Unit,
    onClickCancel: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(color = SikshaColors.White900, shape = RoundedCornerShape(26.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "수정하기",
                    fontSize = 16.sp,
                    color = Color(0xFF797979),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClickEdit)
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
            Divider(
                modifier = Modifier.padding(horizontal = 11.dp),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "삭제하기",
                    fontSize = 16.sp,
                    color = Color(0xFF797979),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClickDelete)
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
            Divider(
                modifier = Modifier.padding(horizontal = 11.dp),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                contentAlignment = Alignment.Center
            ) {
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
            Divider(
                modifier = Modifier.padding(horizontal = 11.dp),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "취소",
                    fontSize = 16.sp,
                    color = Color(0xFFFF9522),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClickCancel)
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
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
            onClickCancel = {}
        )
    }
}
