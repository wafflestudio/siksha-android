import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.components.compose.TopBar
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.SpeechBubbleIcon
import com.wafflestudio.siksha2.utils.showToast
import com.wafflestudio.siksha2.ui.main.community.CommentReportEvent
import com.wafflestudio.siksha2.ui.main.community.CommentReportViewModel

@Composable
fun CommentReportRoute(
    onNavigateUp: () -> Unit,
    commentReportViewModel: CommentReportViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        commentReportViewModel.commentReportEvent.collect {
            when (it) {
                is CommentReportEvent.ReportCommentSuccess -> {
                    onNavigateUp()
                }

                is CommentReportEvent.ReportCommentFailed -> {
                    context.showToast("신고가 실패했습니다.")
                }
            }
        }
    }

    CommentReportScreen(
        onNavigateUp = onNavigateUp,
        onClickReport = {
            commentReportViewModel.reportComment(it)
        }
    )
}

@Composable
fun CommentReportScreen(
    onNavigateUp: () -> Unit,
    onClickReport: (String) -> Unit
) {
    var reportContent by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SikshaColors.White900)
    ) {
        TopBar(
            title = "신고하기",
            navigationButton = {
                Icon(
                    painter = painterResource(id = R.drawable.larrow),
                    contentDescription = "뒤로가기",
                    tint = Color.White,
                    modifier = Modifier
                        .clickable(onClick = onNavigateUp)
                        .padding(16.dp)
                        .size(24.dp)
                )
            }
        )

        Spacer(modifier = Modifier.height(44.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SpeechBubbleIcon(modifier = Modifier.size(18.dp))
                Text(
                    text = "어떤 이유로 신고하시나요?",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(62.dp))

        Box(
            modifier = Modifier
                .width(320.dp)
                .height(280.dp)
                .align(Alignment.CenterHorizontally)
                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
        ) {
            BasicTextField(
                value = reportContent,
                onValueChange = {
                    if (it.length <= 200) {
                        reportContent = it
                    }
                },
                textStyle = TextStyle(fontSize = 16.sp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )

            Text(
                text = "${reportContent.length}자/200자",
                style = TextStyle(color = Color.Gray, fontSize = 12.sp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(193.dp))

        Button(
            onClick = {
                onClickReport(reportContent)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF9522)),
            modifier = Modifier
                .width(343.dp)
                .height(56.dp)
                .padding(start = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "전송하기",
                color = Color.White,
                style = TextStyle(
                    fontSize = 17.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun CommentReportScreenPreview() {
    SikshaTheme {
        CommentReportScreen(
            onNavigateUp = {},
            onClickReport = {}
        )
    }
}
