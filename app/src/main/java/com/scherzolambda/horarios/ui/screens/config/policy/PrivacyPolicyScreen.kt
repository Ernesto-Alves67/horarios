package com.scherzolambda.horarios.ui.screens.config.policy

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.BasicRichText
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.RichTextThemeProvider
import com.halilibo.richtext.ui.currentRichTextStyle
import com.scherzolambda.horarios.ui.screens.config.CustomTopBar
import com.scherzolambda.horarios.ui.theme.LocalAppColors

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    // 1. Carrega o texto do recurso raw (necessita da função loadRawResource acima)

    val policyText = """
        Coloque aqui o texto da sua política de privacidade em Markdown.
        Você pode usar **negrito**, _itálico_, listas, links, etc.
        
        ## Exemplo de Seção
        
        Este é um exemplo de seção na política de privacidade.
        
        - Item 1
        - Item 2
        - Item 3
        
        [Link para mais informações](https://www.exemplo.com)
    """.trimIndent()
    val textColor = LocalAppColors.current.content.blackText
    BackHandler {
        onBack()
    }
    Scaffold(
        containerColor = LocalAppColors.current.content.grayElements,
        topBar = { CustomTopBar(title = "Politica de privacidade", onBack = onBack) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                // Permite a rolagem para documentos longos
                .verticalScroll(rememberScrollState())
                .padding(all= 20.dp)
        ) {
            RichTextThemeProvider(
                contentColorProvider = { textColor } // passa a cor dinamicamente
            ) {
                BasicRichText(
                    modifier = Modifier.padding(innerPadding)

                ){
                    // dentro do RichText scope você chama o Markdown que já usa a content color
                    Markdown(content = policyText) // (do módulo richtext-commonmark)
                }
            }
        }
    }
}