package com.scherzolambda.horarios.ui.screens.config.policy

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.BasicRichText
import com.halilibo.richtext.ui.RichTextThemeProvider
import com.scherzolambda.horarios.ui.screens.config.CustomTopBar
import com.scherzolambda.horarios.ui.theme.LocalAppColors

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    // 1. Carrega o texto do recurso raw (necessita da função loadRawResource acima)

    val policyText = """
        ## Política de Privacidade
        ---
        O Horários valoriza sua privacidade. Esta política explica como coletamos, usamos e protegemos suas informações.
        Os dados coletados são utilizados exclusivamente para melhorar sua experiência no aplicativo.
        A seguir os dados que coletamos:
        
        - **Dados Pessoais**: Nome, email, etc.
        - **Dados de Dispositivo**: Tipo de dispositivo, sistema operacional.
        - **Dados de Uso**: Informações sobre como você utiliza o aplicativo.
        - **Cookies**: Pequenos arquivos armazenados no seu dispositivo.
        
        ## Exclusão de Dados
        Você pode solicitar a exclusão dos seus dados pessoais a qualquer momento.
     
        Entre em contato conosco para exercer esses direitos.
        - [Email](mailto:ernesto487dev@gmail.com)
        - [whatsapp](https://wa.me/64999684391)
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
                .background(LocalAppColors.current.content.blackSecondary)
                .verticalScroll(rememberScrollState())
                .padding(all = 20.dp)
        ) {
            RichTextThemeProvider(
                contentColorProvider = { textColor } // passa a cor dinamicamente
            ) {
                BasicRichText(
                    modifier = Modifier.padding(innerPadding),


                ){
                    // dentro do RichText scope você chama o Markdown que já usa a content color
                    Markdown(content = policyText) // (do módulo richtext-commonmark)
                }
            }
        }
    }
}