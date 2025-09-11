package org.openedx.discovery.presentation.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.openedx.core.ui.theme.OpenEdXTheme
import org.openedx.core.ui.theme.appColors
import org.openedx.core.ui.theme.appShapes
import org.openedx.core.ui.theme.appTypography
import org.openedx.discovery.R
import org.openedx.discovery.domain.model.Course
import org.openedx.foundation.extension.toImageLink
import org.openedx.foundation.presentation.WindowSize
import org.openedx.foundation.presentation.rememberWindowSize
import org.openedx.foundation.presentation.windowSizeValue
import org.openedx.core.R as сoreR

@Composable
fun ImageHeader(
    modifier: Modifier,
    apiHostUrl: String,
    courseImage: String?,
    courseName: String,
) {
    val configuration = LocalConfiguration.current
    val windowSize = rememberWindowSize()
    val contentScale =
        if (!windowSize.isTablet && configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ContentScale.Fit
        } else {
            ContentScale.Crop
        }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(courseImage?.toImageLink(apiHostUrl))
                .error(сoreR.drawable.core_no_image_course)
                .placeholder(сoreR.drawable.core_no_image_course)
                .build(),
            contentDescription = stringResource(
                id = сoreR.string.core_accessibility_header_image_for,
                courseName
            ),
            contentScale = contentScale,
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.appShapes.cardShape)
        )
    }
}

@Composable
fun DiscoveryCourseItem(
    apiHostUrl: String,
    course: Course,
    windowSize: WindowSize,
    onClick: (String) -> Unit,
) {
    // Workaround: Tibetan script line wrapping issue in Jetpack Compose.
    // Adding a trailing newline forces proper height calculation and prevents clipping.
    val adjustedCourseTitle = course.name + "\n"

    val durationText = if (course.duration.isBlank()) {
        stringResource(id = R.string.discovery_course_duration_unspecified)
    } else {
        stringResource(id = R.string.discovery_course_duration_specified, course.duration)
    }

    val lineHeight = 22.dp

    Surface(
        modifier = Modifier
            .testTag("btn_course_card")
            .fillMaxWidth()
            .height(230.dp)
            .clickable { onClick(course.courseId) },
        shape = MaterialTheme.appShapes.cardShape,
        shadowElevation = 4.dp,
        color = MaterialTheme.appColors.surface
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(course.media.courseImage?.uri?.toImageLink(apiHostUrl) ?: "")
                    .error(сoreR.drawable.core_no_image_course)
                    .placeholder(сoreR.drawable.core_no_image_course)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp)
                    .clip(MaterialTheme.appShapes.courseImageShape)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    modifier = Modifier.testTag("txt_course_org"),
                    text = course.org,
                    color = MaterialTheme.appColors.textFieldHint,
                    style = MaterialTheme.appTypography.labelMedium,
                    maxLines = 1,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier
                        .testTag("txt_course_title")
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .height(lineHeight * 3),
                    text = adjustedCourseTitle,
                    color = MaterialTheme.appColors.textPrimary,
                    style = MaterialTheme.appTypography.titleSmall,
                    maxLines = 3,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .testTag("txt_course_duration"),
                    text = durationText,
                    color = MaterialTheme.appColors.textFieldHint,
                    style = MaterialTheme.appTypography.labelMedium,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun WarningLabel(
    painter: Painter,
    text: String
) {
    val borderColor = if (!isSystemInDarkTheme()) {
        MaterialTheme.appColors.cardViewBorder
    } else {
        MaterialTheme.appColors.surface
    }
    Box(
        Modifier
            .fillMaxWidth()
            .shadow(
                0.dp,
                MaterialTheme.appShapes.material3.medium
            )
            .background(
                MaterialTheme.appColors.surface,
                MaterialTheme.appShapes.material3.medium
            )
            .border(
                1.dp,
                borderColor,
                MaterialTheme.appShapes.material3.medium
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = MaterialTheme.appColors.warning
            )
            Spacer(Modifier.width(12.dp))
            Text(
                modifier = Modifier.testTag("txt_enroll_internet_error"),
                text = text,
                color = MaterialTheme.appColors.textPrimaryVariant,
                style = MaterialTheme.appTypography.titleSmall
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WarningLabelPreview() {
    OpenEdXTheme {
        WarningLabel(
            painter = painterResource(id = сoreR.drawable.core_ic_offline),
            text = stringResource(id = R.string.discovery_no_internet_label)
        )
    }
}
