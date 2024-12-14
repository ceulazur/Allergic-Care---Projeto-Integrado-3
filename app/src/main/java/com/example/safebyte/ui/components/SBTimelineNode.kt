package com.example.safebyte.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.safebyte.R
import com.example.safebyte.ui.theme.SafeByteTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TimelineNodePosition {
    FIRST,
    MIDDLE,
    LAST
}

data class CircleParameters(
    val radius: Dp
)

data class LineParameters(
    val strokeWidth: Dp
)

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoUrl: String
) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = false
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp)
    )
}

@Composable
fun TimelineNode(
    pos: TimelineNodePosition = TimelineNodePosition.MIDDLE,
    circleParameters: CircleParameters,
    lineParameters: LineParameters? = null,
    contentStartOffset: Dp = 16.dp,
    spaceBetweenNodes: Dp = 32.dp,
    content: @Composable BoxScope.(modifier: Modifier) -> Unit
) {
    val circleColor: Color = MaterialTheme.colorScheme.secondary
    Box(
        modifier = Modifier
            .wrapContentSize()
            .drawBehind {
                val circleRadiusInPx = circleParameters.radius.toPx()
                drawCircle(
                    color = circleColor,
                    radius = circleRadiusInPx,
                    center = Offset(circleRadiusInPx, circleRadiusInPx)
                )

                lineParameters?.let {
                    drawLine(
                        color = circleColor,
                        start = Offset(x = circleRadiusInPx, y = circleRadiusInPx * 2),
                        end = Offset(x = circleRadiusInPx, y = this.size.height),
                        strokeWidth = lineParameters.strokeWidth.toPx()
                    )
                }
            }
    ) {
        content(
            Modifier.padding(
                start = contentStartOffset,
                bottom = if (pos != TimelineNodePosition.LAST) spaceBetweenNodes else 0.dp,
            )
        )
    }
}

fun dateToStringFormat(date: Date): String {
    val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return format.format(date)
}

data class MediaContent(
    val date: Date,
    val videoUrl: String?,
    val activitiesList: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondaryTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black
        )
    )
}

@Composable
fun MessageBubble(
    mediaContent: MediaContent,
    modifier: Modifier
) {
    val date = Date()

    Column (
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row {
            Text(
                text = dateToStringFormat(date),
                modifier = Modifier.padding(12.dp),
            )
        }

        Row {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                mediaContent.videoUrl?.let { videoUrl ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(
                                color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp)
                            .clip(MaterialTheme.shapes.medium)
                    ) {
                        VideoPlayer(
                            modifier = Modifier,
                            videoUrl = videoUrl
                        )
                    }
                }

                mediaContent.activitiesList.forEach { activity ->
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.circle_solid),
                            contentDescription = stringResource(R.string.simbolo_lista),
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = activity,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimelineNodePreview() {
    val listState = rememberLazyListState()
    val itemsList = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val activitiesList = listOf(
        "Activity 1",
        "Activity 2",
        "Activity 3",
        "Activity 4",
        "Activity 5",
        "Activity 6",
        "Activity 7",
        "Activity 8",
        "Activity 9",
        "Activity 10",
    )

    val videoUrl =
        "https://firebasestorage.googleapis.com/v0/b/aluno-d3928.appspot.com/o/profileImages%2F10240187-sd_360_640_30fps.mp4?alt=media&token=b956caff-2380-43df-9e9f-283b52219a6d"

    val mediaContent = MediaContent(
        date = Date(),
        videoUrl = videoUrl,
        activitiesList = activitiesList
    )

    SafeByteTheme(
        darkTheme = false
    ) {
        Scaffold(
            topBar = {
                SecondaryTopBar(
                    title = "Home",
                    onBackClick = {}
                )
            }
        ) { paddingValues ->

            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 8.dp),
                modifier = Modifier.padding(paddingValues)
            ) {
                items(
                    count = itemsList.size,
                    key = { itemsList[it] }
                ) { _ ->
                    TimelineNode(
                        pos = TimelineNodePosition.FIRST,
                        circleParameters = CircleParameters(6.dp),
                        lineParameters = LineParameters(3.dp)
                    ) { modifier ->
                        MessageBubble(
                            mediaContent = mediaContent,
                            modifier
                        )
                    }
                }

            }
        }


    }
}