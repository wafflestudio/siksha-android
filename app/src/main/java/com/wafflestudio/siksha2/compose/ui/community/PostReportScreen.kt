import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wafflestudio.siksha2.ui.SikshaColors
import com.wafflestudio.siksha2.ui.main.community.PostDetailViewModel

@Composable
fun PostReportScreen(
    postId: Long,
    authToken: String,
    navController: NavController,
    onDismissRequest: () -> Unit,
    postDetailViewModel: PostDetailViewModel = hiltViewModel()
) {
    val reportText = remember { mutableStateOf(TextFieldValue("")) }
    var isSubmitting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SikshaColors.White900)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .background(Color(0xFFFF9522))
        ) {
            Text(
                text = "신고하기",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
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

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(horizontal = 16.dp)
                .background(Color(0xFFF5F5F5))
                .border(1.dp, Color.LightGray, shape = RectangleShape) // Add border if needed
        ) {
            BasicTextField(
                value = reportText.value,
                onValueChange = { reportText.value = it },
                textStyle = TextStyle(fontSize = 16.sp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp) // Add padding inside the text box
            )

            // Character count positioned in the bottom-right corner inside the text box
            Text(
                text = "${reportText.value.text.length}자/500자",
                style = TextStyle(color = Color.Gray, fontSize = 12.sp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(300.dp))

        Button(
            onClick = {
                isSubmitting = true
                postDetailViewModel.reportPost(
                    postId = postId,
                    reason = reportText.value.text
                )
                isSubmitting = false
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF9522)),
            modifier = Modifier
                .width(343.dp)
                .height(56.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 1.dp)
        ) {
            Text(
                text = "전송하기",
                color = Color.White,
                style = TextStyle(
                    fontSize = 17.sp
                )
            )
        }
    }
}
