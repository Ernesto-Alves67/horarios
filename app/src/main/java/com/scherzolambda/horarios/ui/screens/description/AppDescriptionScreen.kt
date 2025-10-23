package com.scherzolambda.horarios.ui.screens.description


import androidx.activity.compose.BackHandler
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
fun AppDescriptionScreen(
    onBack: () -> Unit
) {

    val policyText = """

        ## O que é?
        
        - Cansado de descriptografar seus horários? Eu também!! O Horários foi criado para facilitar o acompanhamento e vizualização de aulas. O objetivo é transformar a visualização da grade de horária em uma experiência rápida, fácil e sem complicação.
        
        ## O que ele faz exatamente:
        
        - Desenvolvido com o que há de mais moderno no Android o aplicativo tem como funcionalidade principal, traduzir os códigos de horários das disciplinas e exibi-los de forma eficiente e de fácil entendimento. 
        - As células em `Hoje` e `Semana` são clicaveis e mostram os detalhes da aula senddo eles: Local, Hora, Nome do Professor.
        
        
        ## Código
        - O código é aberto (Open Source) e todos os releases (APKs) podem ser encontrados diretamente no GitHub. Sinta-se à vontade para conferir o código, reportar problemas ou sugerir melhorias!
        
        Código Fonte: https://github.com/Ernesto-Alves67/horarios]


    """.trimIndent()


    val textColor = LocalAppColors.current.content.blackText

    BackHandler {
        onBack()
    }

    Scaffold(
        containerColor = LocalAppColors.current.content.grayElements,
        topBar = { CustomTopBar(title = "Proposta e descrição do app", onBack = onBack) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(all = 20.dp)
        ) {


            RichTextThemeProvider(
                contentColorProvider = { textColor }
            ) {
                BasicRichText(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    Markdown(content = policyText)
                }

            }

        }

    }

}