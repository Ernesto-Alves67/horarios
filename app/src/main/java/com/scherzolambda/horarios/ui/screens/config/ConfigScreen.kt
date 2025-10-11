package com.scherzolambda.horarios.ui.screens.config

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.scherzolambda.horarios.ui.theme.AppTheme
import com.scherzolambda.horarios.ui.theme.LocalAppColors
import com.scherzolambda.horarios.ui.theme.ThemeViewModel


@Composable
fun ConfigScreen(
    onBack: () -> Unit,
    innerPadding: PaddingValues,
    themeViewModel: ThemeViewModel,
    configViewModel: ConfigViewModel
) {
    val themeState by themeViewModel.theme.collectAsState()

    val showEmptyDaily by configViewModel.showEmptyDailyCell.collectAsState()
    val showEmptyWeekly by configViewModel.showEmptyWeeklyCell.collectAsState()

    // TODO: SOBRE a aplicação
    // TODO: Termos de uso e política de privacidade

    // TODO: Opção de limpar cache (dados armazenados localmente)
    // TODO: OPção para personalizar tela Daily (exibir ou ocultar componentes vazios)
    // TODO: OPção para personalizar tela Weekly (exibir ou ocultar componentes vazios)
    // TODO: Opção para ativa/desativar abreviação de nomes em Weekly
    // TODO: Opção para ativa/desativar alarme antes de alguma aula
    // TODO: Opção para ativa/desativar alarme antes de alguma aula de um certo perido


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.content.grayElements)
            .verticalScroll(rememberScrollState())
            .padding(innerPadding)
    ) {
        CustomTopBar(title = "Configurações", onBack = onBack)

        // --- TEMA ---
        SecaoTitulo("TEMA")
        TemaSwitchGroup(
            selectedTheme = themeState,
            onThemeChange = { themeViewModel.setTheme(it) }
        )


        SecaoTitulo("Layout")
//        Spacer(modifier = Modifier.height(24.dp))
        ItemSwitchLayout(
            titulo = "Exibir componentes vazios na tela 'Hoje'",
            isChecked = showEmptyDaily,
            onCheckedChange = configViewModel::setShowEmptyDailyCell
        )
        ItemSwitchLayout(
            titulo = "Exibir componentes vazios na tela 'Semana'",
            isChecked = showEmptyWeekly,
            onCheckedChange = configViewModel::setShowEmptyWeeklyCell
        )
        // --- SOBRE ---
        SecaoTitulo("SOBRE")

        ItemConfiguracao( "Política de Privacidade")
        ItemConfiguracao( "Contrato de Usuário")
        ItemConfiguracao( "Versão")


        Spacer(modifier = Modifier.height(24.dp))

        // --- SUPORTE ---
        SecaoTitulo("SUPORTE")
        ItemConfiguracao( "Relatar problemas e sugestões")

    }
}


@Composable
fun SecaoTitulo(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelLarge,
        color = LocalAppColors.current.content.blackText,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun ItemConfiguracao(titulo: String, descricao: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalAppColors.current.content.whiteText),
//            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        Icon(icon, contentDescription = titulo, tint = Color.White)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(titulo, color = LocalAppColors.current.content.blackText)
            descricao?.let {
                Text(it, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}


@Composable
fun ItemSwitch(
    icon: ImageVector,
    titulo: String,
    descricao: String? = null,
    value: String,
    groupKey: String,
    selectedValue: String,
    onGroupChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalAppColors.current.content.whiteText)
//            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = titulo,
                color = LocalAppColors.current.content.blackText,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = selectedValue == value,
                onCheckedChange = { if (it) onGroupChange(value) },
                modifier = Modifier.padding(end = 16.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = LocalAppColors.current.content.primary,       // bolinha quando ligado
                checkedTrackColor = LocalAppColors.current.content.primary.copy(alpha = 0.5f), // trilho ligado
                uncheckedThumbColor = Color.Gray,                        // bolinha quando desligado
                uncheckedTrackColor = Color.LightGray,                   // trilho desligado
                disabledCheckedThumbColor = Color.DarkGray,              // bolinha se desabilitado ligado
                disabledUncheckedThumbColor = Color.Gray                 // bolinha se desabilitado desligado
            )
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
    titulo: String,
    descricao: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
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
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
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
fun CustomTopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalAppColors.current.content.background)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = LocalAppColors.current.content.blackText
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = LocalAppColors.current.content.blackText,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ItemSwitch2(
    icon: ImageVector,
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