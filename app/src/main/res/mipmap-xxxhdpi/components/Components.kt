package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.ui.theme.NothingBorderGray
import com.example.ui.theme.NothingCardGray
import com.example.ui.theme.NothingRed

@Composable
fun NothingSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .border(
                BorderStroke(1.dp, NothingBorderGray),
                RoundedCornerShape(27.dp)
            )
            .background(NothingCardGray, RoundedCornerShape(27.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search icon",
            tint = NothingRed,
            modifier = Modifier.size(20.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            if (query.isEmpty()) {
                Text(
                    text = "Search contacts...",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.secondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = TextStyle(
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 15.sp
                ),
                cursorBrush = SolidColor(NothingRed),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    keyboardController?.hide()
                }),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (query.isNotEmpty()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear search",
                    tint = Color.White
                )
            }
        }
    }
}

val dotMatrixPatterns = mapOf(
    'A' to listOf(
        " ### ",
        "#   #",
        "#   #",
        "#####",
        "#   #",
        "#   #",
        "#   #"
    ),
    'B' to listOf(
        "#### ",
        "#   #",
        "#### ",
        "#   #",
        "#   #",
        "#   #",
        "#### "
    ),
    'C' to listOf(
        " ####",
        "#    ",
        "#    ",
        "#    ",
        "#    ",
        "#    ",
        " ####"
    ),
    'D' to listOf(
        "###  ",
        "#  # ",
        "#   #",
        "#   #",
        "#   #",
        "#  # ",
        "###  "
    ),
    'E' to listOf(
        "#####",
        "#    ",
        "#    ",
        "#### ",
        "#    ",
        "#    ",
        "#####"
    ),
    'F' to listOf(
        "#####",
        "#    ",
        "#### ",
        "#    ",
        "#    ",
        "#    ",
        "#    "
    ),
    'G' to listOf(
        " ####",
        "#    ",
        "#    ",
        "#  ##",
        "#   #",
        "#   #",
        " ####"
    ),
    'H' to listOf(
        "#   #",
        "#   #",
        "#   #",
        "#####",
        "#   #",
        "#   #",
        "#   #"
    ),
    'I' to listOf(
        "#####",
        "  #  ",
        "  #  ",
        "  #  ",
        "  #  ",
        "  #  ",
        "#####"
    ),
    'J' to listOf(
        "  ###",
        "    #",
        "    #",
        "    #",
        "#   #",
        "#   #",
        " ### "
    ),
    'K' to listOf(
        "#   #",
        "#  # ",
        "# #  ",
        "##   ",
        "# #  ",
        "#  # ",
        "#   #"
    ),
    'L' to listOf(
        "#    ",
        "#    ",
        "#    ",
        "#    ",
        "#    ",
        "#    ",
        "#####"
    ),
    'M' to listOf(
        "#   #",
        "## ##",
        "# # #",
        "#   #",
        "#   #",
        "#   #",
        "#   #"
    ),
    'N' to listOf(
        "#   #",
        "##  #",
        "# # #",
        "#  ##",
        "#   #",
        "#   #",
        "#   #"
    ),
    'O' to listOf(
        " ### ",
        "#   #",
        "#   #",
        "#   #",
        "#   #",
        "#   #",
        " ### "
    ),
    'P' to listOf(
        "#### ",
        "#   #",
        "#   #",
        "#### ",
        "#    ",
        "#    ",
        "#    "
    ),
    'Q' to listOf(
        " ### ",
        "#   #",
        "#   #",
        "#   #",
        "# # #",
        "#  # ",
        " ## #"
    ),
    'R' to listOf(
        "#### ",
        "#   #",
        "#   #",
        "#### ",
        "# #  ",
        "#  # ",
        "#   #"
    ),
    'S' to listOf(
        " ####",
        "#    ",
        " ### ",
        "    #",
        "    #",
        "    #",
        "#### "
    ),
    'T' to listOf(
        "#####",
        "  #  ",
        "  #  ",
        "  #  ",
        "  #  ",
        "  #  ",
        "  #  "
    ),
    'U' to listOf(
        "#   #",
        "#   #",
        "#   #",
        "#   #",
        "#   #",
        "#   #",
        " ### "
    ),
    'V' to listOf(
        "#   #",
        "#   #",
        "#   #",
        " # # ",
        " # # ",
        "  #  ",
        "  #  "
    ),
    'W' to listOf(
        "#   #",
        "#   #",
        "#   #",
        "#   #",
        "# # #",
        "## ##",
        "#   #"
    ),
    'X' to listOf(
        "#   #",
        " # # ",
        "  #  ",
        "  #  ",
        " # # ",
        " # # ",
        "#   #"
    ),
    'Y' to listOf(
        "#   #",
        " # # ",
        "  #  ",
        "  #  ",
        "  #  ",
        "  #  ",
        "  #  "
    ),
    'Z' to listOf(
        "#####",
        "    #",
        "   # ",
        "  #  ",
        " #   ",
        "#    ",
        "#####"
    ),
    '?' to listOf(
        " ### ",
        "#   #",
        "   # ",
        "  #  ",
        "  #  ",
        "     ",
        "  #  "
    )
)

@Composable
fun NothingDotMatrixCharacter(
    char: Char,
    color: Color = Color.White,
    dotSize: Dp = 1.3.dp,
    spacing: Dp = 1.2.dp,
    modifier: Modifier = Modifier
) {
    val upperChar = char.uppercaseChar()
    val pattern = dotMatrixPatterns[upperChar] ?: dotMatrixPatterns['?'] ?: listOf(
        " ### ",
        "#   #",
        "   # ",
        "  #  ",
        "  #  ",
        "     ",
        "  #  "
    )

    androidx.compose.foundation.layout.Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(spacing),
        modifier = modifier
    ) {
        pattern.forEach { rowString ->
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(spacing)
            ) {
                rowString.forEach { c ->
                    val isFilled = c != ' '
                    Box(
                        modifier = Modifier
                            .size(dotSize)
                            .clip(CircleShape)
                            .background(if (isFilled) color else Color.Transparent)
                    )
                }
            }
        }
    }
}

@Composable
fun ContactAvatar(
    imageUrl: String?,
    name: String,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(BorderStroke(1.dp, NothingBorderGray), CircleShape)
            .background(NothingCardGray),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = "$name Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    AvatarFallback(name)
                },
                error = {
                    AvatarFallback(name)
                }
            )
        } else {
            AvatarFallback(name)
        }
    }
}

@Composable
private fun AvatarFallback(name: String) {
    val initial = if (name.isNotEmpty()) name.first() else '?'
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingCardGray),
        contentAlignment = Alignment.Center
    ) {
        NothingDotMatrixCharacter(
            char = initial,
            color = Color.White,
            dotSize = 1.8.dp,
            spacing = 1.5.dp
        )
    }
}

@Composable
fun NothingGlassCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .border(
                BorderStroke(0.8.dp, NothingBorderGray),
                RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = NothingCardGray
        ),
        onClick = onClick
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}
