package com.scherzolambda.horarios.ui.screens.config

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scherzolambda.horarios.BuildConfig
import com.scherzolambda.horarios.R
import com.scherzolambda.horarios.ui.theme.AppTheme
import com.scherzolambda.horarios.ui.theme.LocalAppColors
import com.scherzolambda.horarios.ui.theme.ThemeViewModel
import com.scherzolambda.horarios.viewmodel.ConfigViewModel


@Composable
fun ConfigScreen(
    onBack: () -> Unit,
    themeViewModel: ThemeViewModel,
    configViewModel: ConfigViewModel,
    onNavigateToDescription: () -> Unit
) {
    val themeState by themeViewModel.theme.collectAsState()

    val showEmptyDaily by configViewModel.showEmptyDailyCell.collectAsState()
    val showEmptyWeekly by configViewModel.showEmptyWeeklyCell.collectAsState()

    // TODO: SOBRE a aplicação
    // TODO: Termos de uso e política de privacidade

    // TODO: Opção de limpar cache (dados armazenados localmente)
    // TODO: Opção para ativa/desativar abreviação de nomes em Weekly
    // TODO: Opção para ativa/desativar alarme antes de alguma aula
    // TODO: Opção para ativa/desativar alarme antes de alguma aula de um certo perido


    BackHandler {
        onBack()
    }

    Scaffold(
        containerColor = LocalAppColors.current.content.grayElements,
        topBar = { CustomTopBar(title = "Configurações", onBack = onBack) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {


            // --- TEMA ---
            SecaoTitulo("TEMA")
            TemaSwitchGroup(
                selectedTheme = themeState,
                onThemeChange = { themeViewModel.setTheme(it) }
            )

            SecaoTitulo("LAYOUT")
            ItemSwitchLayout(
                titulo = buildAnnotatedString {
                    append("Mostrar células vazias na tela ")
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append("Hoje")
                    pop()
                },
                isChecked = showEmptyDaily,
                onCheckedChange = configViewModel::setShowEmptyDailyCell
            )
            ItemSwitchLayout(
                titulo = buildAnnotatedString {
                    append("Mostrar células vazias na tela ")
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append("Semana")
                    pop()
                },
                isChecked = showEmptyWeekly,
                onCheckedChange = configViewModel::setShowEmptyWeeklyCell
            )
            // --- SOBRE ---
            SecaoTitulo("SOBRE")

            ItemConfiguracao( "O que é o Horários?",
                onClick = onNavigateToDescription)
//            ItemConfiguracao( "Contrato de Usuário")
            ItemConfiguracao( titulo="Versão",
                descricao = BuildConfig.VERSION_NAME)

//            // --- SUPORTE ---
//            SecaoTitulo("SUPORTE")
//            ItemConfiguracao( "Relatar problemas e sugestões")

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    title: String,
    onBack: () -> Unit,
    backgroundColor: Color = LocalAppColors.current.content.background,
    titleColor: Color = LocalAppColors.current.content.blackText,
    rippleColor: Color = LocalAppColors.current.content.grayElements,
    rightContent: @Composable (() -> Unit)? = null // Conteúdo à direita (ícones adicionais)
) {
    TopAppBar(
        navigationIcon = {
            val interactionSource = remember { MutableInteractionSource() }
            IconButton(
                onClick = onBack,
                interactionSource = interactionSource,
                modifier = Modifier.indication(
                    interactionSource = interactionSource,
                    indication = ripple(
                        bounded = false, // ou true
                        color = rippleColor
                    )
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back_arrow),
                    contentDescription = "Voltar",
                    tint = titleColor,
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
        },
        actions = {
            rightContent?.invoke() // Adiciona ações à direita, se houver
        },
        colors = topAppBarColors(
            containerColor = backgroundColor
        ),
    )
}


@Composable
fun SecaoTitulo(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = LocalAppColors.current.content.blackText,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun ItemConfiguracao(
    titulo: String,
    descricao: String? = null,
    onClick: (() -> Unit)?= null) {
    val modifier = remember { Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp) }

    Card(
        modifier = modifier.clickable{
            onClick?.invoke()
        },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = LocalAppColors.current.content.whiteText)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(
                    text = titulo,
                    color = LocalAppColors.current.content.blackText
                )
                descricao?.let {
                    Text(
                        text = it,
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun ItemSwitch3(
    titulo: String,
    descricao: String? = null,
    value: String,
    groupKey: String,
    selectedValue: String,
    onGroupChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = LocalAppColors.current.content.whiteText
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = titulo,
                    color = LocalAppColors.current.content.blackText,
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = selectedValue == value,
                    onCheckedChange = { if (it) onGroupChange(value) },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = LocalAppColors.current.content.primary,
                        checkedTrackColor = LocalAppColors.current.content.primary.copy(alpha = 0.5f),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.LightGray,
                        disabledCheckedThumbColor = Color.DarkGray,
                        disabledUncheckedThumbColor = Color.Gray
                    )
                )
            }

            descricao?.let {
                Text(
                    text = it,
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ItemSwitchLayout(
    titulo: AnnotatedString,
    descricao: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = LocalAppColors.current.content.whiteText
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = titulo,
                    color = LocalAppColors.current.content.blackText,
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    modifier = Modifier.padding(end = 4.dp),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = LocalAppColors.current.content.primary,
                        checkedTrackColor = LocalAppColors.current.content.primary.copy(alpha = 0.5f),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.LightGray,
                        disabledCheckedThumbColor = Color.DarkGray,
                        disabledUncheckedThumbColor = Color.Gray
                    )
                )
            }

            descricao?.let {
                Text(
                    text = it,
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp)
                )
            }
        }
    }
}
@Composable
fun TemaSwitchGroup(
    selectedTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
    ) {
        ItemSwitch3(
            titulo = "Modo escuro",
            value = AppTheme.DARK.name,
            groupKey = "theme",
            selectedValue = selectedTheme.name,
            onGroupChange = { onThemeChange(AppTheme.DARK) }
        )
        ItemSwitch3(
            titulo = "Modo claro",
            value = AppTheme.LIGHT.name,
            groupKey = "theme",
            selectedValue = selectedTheme.name,
            onGroupChange = { onThemeChange(AppTheme.LIGHT) }
        )
        ItemSwitch3(
            titulo = "Seguir tema do sistema",
            value = AppTheme.SYSTEM.name,
            groupKey = "theme",
            selectedValue = selectedTheme.name,
            onGroupChange = { onThemeChange(AppTheme.SYSTEM) }
        )
    }
}

@Composable
fun ItemSwitch2(
    titulo: String,
    descricao: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalAppColors.current.content.whiteText)
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone opcional
            // Icon(icon, contentDescription = titulo, tint = Color.White)

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = titulo,
                color = LocalAppColors.current.content.blackText,
                modifier = Modifier.weight(1f) // ocupa o espaço restante
            )

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }

        descricao?.let {
            Text(
                text = it,
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}


//@Preview
//@Composable
//fun ConfigScreenPreview() {
//    ConfigScreen(onBack = {}, innerPadding = PaddingValues())
//}