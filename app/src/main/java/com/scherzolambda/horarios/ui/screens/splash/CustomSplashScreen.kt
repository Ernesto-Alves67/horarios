package com.scherzolambda.horarios.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scherzolambda.horarios.R
import com.scherzolambda.horarios.ui.theme.UfcatGreen
import kotlinx.coroutines.delay

@Composable
fun ComposeSplashScreen(
    onFinished: () -> Unit
) {
    val scale = remember { Animatable(0.1f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            0.7f,
            tween(800, easing = EaseInBounce)
        )
        alpha.animateTo(
            1f,
            tween(600)
        )
        delay(300)
        onFinished()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.ufcat_green_2)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(R.drawable.ic_logo_ufcat),
            contentDescription = null,
            modifier = Modifier
                .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                this.alpha = alpha.value
            }
        )
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            color = UfcatGreen,
        )
    }

}

@Preview
@Composable
fun ComposeSplashScreenPreview() {
    ComposeSplashScreen(onFinished = {})
}