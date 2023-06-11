package com.mz.acesaii.model

import androidx.compose.ui.graphics.Color
import com.mz.acesaii.R
import com.mz.acesaii.ui.theme.NeutralColor

enum class GameRecord(
    val icon: Int,
    val contentColor: Color,
    val containerColor: Color
) {
    StartUpDialog(
        icon = R.drawable.acesai,
        contentColor = Color.Black,
        containerColor = NeutralColor
    ),
    WinngingLog(
        icon = R.drawable.acesai,
        contentColor = Color.Black,
        containerColor = NeutralColor
    ),
    LosingLog(
        icon = R.drawable.acesai,
        contentColor = Color.Blue,
        containerColor = NeutralColor
    ),
    CommentLog(
        icon = R.drawable.acesai,
        contentColor = Color.Green,
        containerColor = NeutralColor
    )
}