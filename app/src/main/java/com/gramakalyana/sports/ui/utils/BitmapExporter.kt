package com.gramakalyana.sports.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import com.gramakalyana.sports.data.model.Match
import com.gramakalyana.sports.data.model.Score
import com.gramakalyana.sports.data.repository.SportsRepository
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BitmapExporter {

    suspend fun createScorecardBitmap(context: Context, match: Match): Bitmap {
        val width = 1080
        val height = 1450 // Slightly taller to accommodate MOM
        
        val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        // Fetch MOM name if exists
        var momName = ""
        if (match.manOfTheMatchId != null) {
            val repository = SportsRepository()
            val players = repository.getAllPlayers().firstOrNull()
            momName = players?.find { it.id == match.manOfTheMatchId }?.name ?: ""
        }

        bitmap.applyCanvas {
            val bgPaint = Paint().apply { color = Color.WHITE }
            val primaryPaint = Paint().apply {
                color = "#1A237E".toColorInt()
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            val textPaint = Paint().apply {
                color = Color.BLACK
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            val secondaryPaint = Paint().apply {
                color = "#E91E63".toColorInt()
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }

            drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

            val borderPaint = Paint().apply {
                color = "#1A237E".toColorInt()
                style = Paint.Style.STROKE
                strokeWidth = 20f
            }
            drawRect(40f, 40f, width - 40f, height - 40f, borderPaint)

            var currentY = 150f

            primaryPaint.textSize = 60f
            primaryPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            drawText("GRAMA KALYANA SPORTS", width / 2f, currentY, primaryPaint)

            currentY += 60f
            textPaint.textSize = 35f
            textPaint.color = Color.GRAY
            drawText("Official Match Scorecard", width / 2f, currentY, textPaint)

            currentY += 150f

            val score = match.score ?: Score()
            val (scoreA, scoreB) = when (score.type) {
                "KABADDI" -> score.teamAScore.toString() to score.teamBScore.toString()
                "VOLLEYBALL" -> score.teamASets.toString() to score.teamBSets.toString()
                "CRICKET" -> "${score.teamARuns}/${score.teamAWickets}" to "${score.teamBRuns}/${score.teamBWickets}"
                "SOCCER" -> score.teamAGoals.toString() to score.teamBGoals.toString()
                else -> "0" to "0"
            }

            secondaryPaint.textSize = 40f
            drawText("TEAM A", width * 0.25f, currentY, secondaryPaint)
            secondaryPaint.textSize = 100f
            drawText(scoreA, width * 0.25f, currentY + 130f, secondaryPaint)

            primaryPaint.textSize = 50f
            drawText("VS", width / 2f, currentY + 80f, primaryPaint)

            secondaryPaint.textSize = 40f
            drawText("TEAM B", width * 0.75f, currentY, secondaryPaint)
            secondaryPaint.textSize = 100f
            drawText(scoreB, width * 0.75f, currentY + 130f, secondaryPaint)

            currentY += 250f

            val dashPaint = Paint().apply {
                color = Color.LTGRAY
                style = Paint.Style.STROKE
                strokeWidth = 3f
                pathEffect = DashPathEffect(floatArrayOf(20f, 10f), 0f)
            }
            drawLine(100f, currentY, width - 100f, currentY, dashPaint)

            currentY += 100f

            textPaint.color = Color.BLACK
            textPaint.textSize = 40f
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            drawText("Status: ${match.status.name}", width / 2f, currentY, textPaint)

            currentY += 60f
            textPaint.typeface = Typeface.DEFAULT
            textPaint.textSize = 35f
            drawText("Match ID: ${match.id.take(8)}", width / 2f, currentY, textPaint)

            currentY += 60f
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            drawText("Date: ${sdf.format(Date(match.startTime))}", width / 2f, currentY, textPaint)

            currentY += 120f

            val winnerText = if (match.status.name == "COMPLETED") {
                val sA = score.teamAScore // Simplification for winner logic in bitmap
                val sB = score.teamBScore
                if (sA > sB) "WINNER: TEAM A" else if (sB > sA) "WINNER: TEAM B" else "RESULT: DRAW"
            } else {
                "MATCH IN PROGRESS"
            }

            primaryPaint.textSize = 60f
            primaryPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            drawText(winnerText, width / 2f, currentY, primaryPaint)

            if (momName.isNotEmpty()) {
                currentY += 100f
                secondaryPaint.textSize = 45f
                drawText("Man of the Match: $momName", width / 2f, currentY, secondaryPaint)
            }

            textPaint.color = Color.GRAY
            textPaint.textSize = 30f
            drawText("Powered by Grama Kalyana App", width / 2f, height - 100f, textPaint)
        }

        return bitmap
    }
}
