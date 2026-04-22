package com.daprox.financeos.presentation.core.designsystem.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme

// Which side the icon sits on relative to the text.
enum class IconPosition { START, END }

// Text with an icon to its left or right — the Compose equivalent of drawableStart/drawableEnd.
//
// The [icon] parameter is a composable slot, so the caller is unconstrained:
//   Icon(imageVector = ...)
//   Icon(painter = painterResource(R.drawable.something))
//   A custom Box, badge, or any other composable
//
// [iconSpacing] is the gap between the icon slot and the text edge — equivalent
// to drawablePadding in the View system.
//
// [color] defaults to Color.Unspecified so it inherits LocalContentColor from
// the call site automatically. Pass an explicit color to override.
@Composable
fun TextWithIcon(
    text         : String,
    icon         : @Composable () -> Unit,
    modifier     : Modifier     = Modifier,
    style        : TextStyle    = MaterialTheme.typography.bodyMedium,
    color        : Color        = Color.Unspecified,
    iconPosition : IconPosition = IconPosition.START,
    iconSpacing  : Dp           = 8.dp,
    maxLines     : Int          = Int.MAX_VALUE,
    overflow     : TextOverflow = TextOverflow.Clip,
) {
    Row(
        modifier         = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (iconPosition) {
            IconPosition.START -> {
                icon()
                Spacer(Modifier.width(iconSpacing))
                Text(
                    text     = text,
                    style    = style,
                    color    = color,
                    maxLines = maxLines,
                    overflow = overflow,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }
            IconPosition.END -> {
                Text(
                    text     = text,
                    style    = style,
                    color    = color,
                    maxLines = maxLines,
                    overflow = overflow,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(iconSpacing))
                icon()
            }
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

// START — ImageVector icon on the left (most common case).
@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun TextWithIconStartPreview() {
    FinanceOSTheme {
        TextWithIcon(
            text  = "Fixed Expenses",
            icon  = {
                Icon(
                    imageVector        = Icons.Default.Star,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.primary,
                )
            },
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

// END — icon on the right, e.g. a trailing navigation arrow.
@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun TextWithIconEndPreview() {
    FinanceOSTheme {
        TextWithIcon(
            text         = "See all envelopes",
            icon         = {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.primary,
                )
            },
            color        = MaterialTheme.colorScheme.primary,
            iconPosition = IconPosition.END,
            iconSpacing  = 4.dp,
        )
    }
}

// Long text with ellipsis truncation — icon stays pinned, text clips gracefully.
@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun TextWithIconEllipsisPreview() {
    FinanceOSTheme {
        TextWithIcon(
            text         = "This is a very long category name that should be truncated",
            icon         = {
                Icon(
                    imageVector        = Icons.Default.Star,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            color        = MaterialTheme.colorScheme.onSurface,
            iconPosition = IconPosition.END,
            maxLines     = 1,
            overflow     = TextOverflow.Ellipsis,
        )
    }
}
