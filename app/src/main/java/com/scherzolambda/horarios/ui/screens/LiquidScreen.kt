package com.scherzolambda.horarios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Slider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage // atenção à versão do Coil (coil3 vs coil2)
import com.scherzolambda.horarios.ui.theme.White
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.rememberLiquidState

@Composable
fun LiquidScreen(
    modifier: Modifier = Modifier,
) {
    // estado compartilhado entre as camadas
    val liquidState: LiquidState = rememberLiquidState()

    // UI state: controles do painel
    var useLiquid by remember { mutableStateOf(true) }
    var frostDp by remember { mutableFloatStateOf(8f) }         // Frost em dp
    var refractionFloat by remember { mutableFloatStateOf(0.25f) }  // 0..1
    var curveFloat by remember { mutableFloatStateOf(0.2f) }        // 0..1
    var dispersionFloat by remember { mutableFloatStateOf(0.0f) }  // 0..1
    var edgeFloat by remember { mutableFloatStateOf(0.08f) }       // 0..1
    var saturationFloat by remember { mutableFloatStateOf(1.0f) }  // 0..3
    var tintIndex by remember { mutableStateOf(0) }           // índice de cor preset

    val tintOptions = listOf(
         // sem tint (use null para deixar o efeito natural)
        Color(0x00000000),
        Color(0xFF7C4DFF),
        Color(0xFF00BFA5),
        Color(0xFFFF7043),
        Color(0x99FFFFFF)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        // 1) CAMADA DE FUNDO — composable que será amostrado pelo efeito
        AsyncImage(
            model = "https://picsum.photos/1200/500",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                // marque o elemento que realmente desenha os pixels de background
                .liquefiable(liquidState)
        )

        // 2) PAINEL DE CONTROLES — aplicamos .liquid somente quando useLiquid = true
        // Importante: usar container semi-transparente (alpha baixo) para o "glass" aparecer.
        val panelColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.16f) // <--- reduzir alpha aqui!
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .width(340.dp)
                .clip(RoundedCornerShape(1.dp))
                // aplicamos .liquid condicionalmente; também deixamos as propriedades do efeito
                .let { base ->
                    if (useLiquid) {
                        base.liquid(liquidState) {
                            frost = frostDp.dp
                            refraction = refractionFloat
                            curve = curveFloat
                            dispersion = dispersionFloat
                            edge = edgeFloat
                            saturation = saturationFloat
                            // use null para nenhuma tinta, evita sobrepor com cor sólida
                            tint = tintOptions[tintIndex]
                        }
                    } else base
                },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)
                .background(Color.Transparent),
                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Liquid Controls", style = MaterialTheme.typography.titleMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Usar", modifier = Modifier.padding(end = 6.dp))
                        Switch(checked = useLiquid, onCheckedChange = { useLiquid = it })
                    }
                }

                Divider()

                // Frost (dp)
                LabeledSlider("Frost (dp)", frostDp, 0f..40f, 40) { frostDp = it }

                // Refraction
                LabeledSlider("Refraction", refractionFloat, 0f..1f, 100) { refractionFloat = it }

                // Curve
                LabeledSlider("Curve", curveFloat, 0f..1f, 100) { curveFloat = it }

                // Dispersion
                LabeledSlider("Dispersion", dispersionFloat, 0f..1f, 100) { dispersionFloat = it }

                // Edge
                LabeledSlider("Edge", edgeFloat, 0f..0.5f, 50) { edgeFloat = it }

                // Saturation
                LabeledSlider("Saturation", saturationFloat, 0f..3f, 60) { saturationFloat = it }

                // Tint selector (simples)
                TintSelector(tintIndex, listOf(Color.Unspecified, Color(0xFF7C4DFF), Color(0xFF00BFA5), Color(0xFFFF7043), Color(0x99FFFFFF))) {
                    tintIndex = it
                }

                Divider()


                Row(horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                        .liquid(liquidState){
                            frost = frostDp.dp
                            refraction = refractionFloat
                            curve = curveFloat
                            dispersion = dispersionFloat
                            edge = edgeFloat
                            saturation = saturationFloat
                            tint = tintOptions[tintIndex]
                        }
                        .clip(RoundedCornerShape(curveFloat))) {
                    Button(onClick = {
                        frostDp = 8f; refractionFloat = 0.25f; curveFloat = 0.2f; dispersionFloat = 0f; edgeFloat = 0.08f; saturationFloat = 1f; tintIndex = 0
                    }) {
                        Text("Reset")
                    }
                }


            }
        }
    }
}

@Composable
private fun LabeledSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 4.dp))
            Text(String.format("%.2f", value), style = MaterialTheme.typography.bodySmall)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TintSelector(
    tintIndex: Int,
    tintOptions: List<Color>,
    onSelect: (Int) -> Unit,
) {
    var opened by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text("Tinta:", modifier = Modifier.padding(end = 8.dp))
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (tintIndex == 0) Color.Transparent else tintOptions.getOrNull(tintIndex) ?: Color.Transparent)
                .clickable { opened = true }
        )

        DropdownMenu(expanded = opened, onDismissRequest = { opened = false }) {
            listOf("Nenhum", "Roxo", "Teal", "Laranja", "Branco translúcido").forEachIndexed { idx, label ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onSelect(idx)
                        opened = false
                    }
                )
            }
        }
    }
}