package com.mz.acesaii.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mz.acesaii.model.GameRecord
import com.mz.acesaii.model.Poker
import com.mz.acesaii.ui.theme.Elevation
import com.mz.acesaii.util.toInstant
import io.realm.kotlin.Realm
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import io.realm.kotlin.ext.realmListOf
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


@Composable
fun PokerEntryHolder(poker: Poker, onClick: (String) -> Unit,
) { var componentHeight by remember { mutableStateOf(0.dp) }
    val localDensity = LocalDensity.current

    Row(modifier = Modifier.clickable(
        indication = null,
        interactionSource = remember {
            MutableInteractionSource()
        }
    ){
        onClick(poker._id.toHexString()) })
    {
        Spacer(modifier = Modifier.width(20.dp))
        Surface(
            modifier = Modifier
                .width(2.dp)
                .height(componentHeight + 14.dp),
            tonalElevation = Elevation.Level1
        ) {}
        Spacer(modifier = Modifier.width(20.dp))
        Surface(modifier = Modifier
            .clip(shape = Shapes().medium)
            .onGloballyPositioned {
                componentHeight = with(localDensity) { it.size.height.toDp() }

            },
            tonalElevation = Elevation.Level1
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                PokerEntryHeader(
                    pokerGameRecord = poker.gameRecord,
                    time = poker.date.toInstant())

            }
        }
    }
}

@Composable
fun PokerEntryHeader(pokerGameRecord: String, time: Instant){
    val gameStatus by remember{ mutableStateOf(GameRecord.valueOf(pokerGameRecord)) }
    val formatter = remember {
        DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
            .withZone(ZoneId.systemDefault())
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GameRecord.valueOf(pokerGameRecord).containerColor)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier.size(18.dp),
                painter = painterResource(id = gameStatus.icon),
                contentDescription = "Game Status Icon",
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = gameStatus.name,
                color = gameStatus.contentColor,
                style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize)
            )
        }
        Text(
            text = formatter.format(time),
            color = gameStatus.contentColor,
            style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize)
        )
    }
}
