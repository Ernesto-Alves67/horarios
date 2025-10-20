package com.scherzolambda.horarios.ui.screens.config.usercontract

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.BasicRichText
import com.halilibo.richtext.ui.RichTextThemeProvider
import com.scherzolambda.horarios.ui.screens.config.CustomTopBar
import com.scherzolambda.horarios.ui.theme.LocalAppColors


//// Função de carregamento do arquivo (reutilize a que criamos antes)
//@Composable
//fun loadRawResource(resourceId: Int): String {
//    val context = LocalContext.current
//    return remember(resourceId) {
//        context.resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
//    }
//}

@Composable
fun UserAgreementScreen(
    onBack: () -> Unit
) {
    // Carrega o texto do Contrato/Termos
    val agreementText = """
        # Termos de Serviço

        Bem-vindo ao nosso aplicativo! Ao utilizar este aplicativo, você concorda com os seguintes termos e condições:

        ## Uso do Aplicativo

        1. Você concorda em usar o aplicativo apenas para fins legais e de acordo com todas as leis aplicáveis.
        2. Você não deve usar o aplicativo para transmitir qualquer conteúdo que seja ilegal, prejudicial, ameaçador, abusivo, assediante, difamatório, vulgar, obsceno, calunioso, invasivo da privacidade de outrem, odioso ou racialmente, etnicamente ou de outra forma questionável.

        ## Propriedade Intelectual

        Todo o conteúdo do aplicativo, incluindo textos, gráficos, logotipos, ícones de botões, imagens e software, é propriedade do desenvolvedor do aplicativo ou de seus fornecedores de conteúdo e é protegido por leis de direitos autorais internacionais.

        ## Limitação de Responsabilidade

        O desenvolvedor do aplicativo não será responsável por quaisquer danos diretos, indiretos, incidentais, especiais ou consequenciais decorrentes do uso ou da incapacidade de usar o aplicativo.

        ## Alterações nos Termos

        Reservamo-nos o direito de modificar estes termos a qualquer momento. Quaisquer alterações serão publicadas nesta página e entrarão em vigor imediatamente após a publicação.

        ## Contato

        Se você tiver alguma dúvida sobre estes termos, entre em contato conosco através do e-mail: 
        """.trimIndent()

    BackHandler {
        onBack()
    }
    val textColor = LocalAppColors.current.content.blackText
    Scaffold(
        containerColor = LocalAppColors.current.content.grayElements,
        topBar = { CustomTopBar(title = "Politica de privacidade", onBack = onBack) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            RichTextThemeProvider(
                contentColorProvider = { textColor } // passa a cor dinamicamente
            ) {
                BasicRichText(
                    modifier = Modifier.padding(20.dp)
                ){
                    // dentro do RichText scope você chama o Markdown que já usa a content color
                    Markdown(content = agreementText) // (do módulo richtext-commonmark)
                }
            }
        }
    }
}