import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.main.community.PostReportEvent
import com.wafflestudio.siksha2.ui.main.community.PostReportViewModel
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.compose.ui.community.CommunityProfilePicture
import com.wafflestudio.siksha2.models.User
import com.wafflestudio.siksha2.utils.showToast
import com.wafflestudio.siksha2.ui.SikshaTypography

@Composable
fun PostReportRoute(
    onNavigateUp: () -> Unit,
    postReportViewModel: PostReportViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val user by postReportViewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        postReportViewModel.postReportEvent.collect {
            when (it) {
                is PostReportEvent.ReportPostSuccess -> {
                    onNavigateUp()
                }

                is PostReportEvent.ReportPostFailed -> {
                    context.showToast("신고가 실패했습니다.")
                }
            }
        }
    }

    PostReportScreen(
        onNavigateUp = onNavigateUp,
        onClickReport = {
            postReportViewModel.reportPost(it)
        },
        user = user,
    )
}

@Composable
fun PostReportScreen(
    onNavigateUp: () -> Unit,
    onClickReport: (String) -> Unit,
    user: User,
) {
    var reportContent by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SikshaColors.White900)
    ) {
        Box(
            modifier = Modifier
                .height(88.dp)
                .fillMaxWidth()
                .background(SikshaColors.OrangeMain)
        ) {
            IconButton(
                onClick = onNavigateUp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.larrow),
                    contentDescription = "뒤로가기",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "신고하기",
                modifier = Modifier.align(Alignment.Center),
                color = SikshaColors.White900,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                style = SikshaTypography.subtitle1
            )
        }

        Spacer(modifier = Modifier.height(44.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "어떤 이유로 신고하시나요?",
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.Black
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 28.dp)
        ) {
            CommunityProfilePicture(
                model = user.profileUrl,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = user.nickname,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

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
                    if (it.length <= 500) {
                        reportContent = it
                    }
                },
                textStyle = TextStyle(fontSize = 16.sp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )

            Text(
                text = "${reportContent.length}자/500자",
                style = TextStyle(color = Color.Gray, fontSize = 12.sp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(200.dp))

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
fun PostReportScreenPreview() {
    PostReportScreen(
        onNavigateUp = {},
        onClickReport = {},
        user = User(0L, "닉네임", null)
    )
}
