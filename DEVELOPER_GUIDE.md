# Guia Rápido de Desenvolvimento - App Horários

## 🚀 Quick Start

### Estrutura do Projeto
```
app/src/main/java/com/scherzolambda/horarios/
├── data_transformation/        # Processamento de dados
│   ├── FileProcessor.kt       # Parse de HTML
│   ├── DataStoreHelper.kt     # Preferências
│   ├── HorarioSemanal.kt      # Modelo de dados
│   └── enums/                 # Enumerações (dias, horários)
├── di/                        # Injeção de dependência (Hilt)
│   └── AppModule.kt
├── ui/
│   ├── navigation/            # Sistema de navegação
│   │   └── NavHostMain.kt
│   ├── screens/               # Telas do app
│   │   ├── DailyScreen.kt    # Aulas de hoje
│   │   ├── WeeklyScreen.kt   # Grade semanal
│   │   ├── StatusScreen.kt   # Status e carregamento
│   │   └── web/SigaaWebView.kt
│   └── theme/                 # Cores, tipografia
├── viewmodel/                 # Lógica de negócio
│   └── DisciplinaViewModel.kt
├── MainActivity.kt
└── HorariosApplication.kt
```

## 📝 Adicionando uma Nova Tela

### 1. Criar a Screen Composable
```kotlin
@Composable
fun MinhaNovaScreen(
    paddingValues: PaddingValues,
    viewModel: DisciplinaViewModel
) {
    val disciplinas by viewModel.disciplinas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column {
            // Seu conteúdo aqui
        }
    }
}
```

### 2. Adicionar rota na navegação
```kotlin
// Em NavHostMain.kt

// 1. Adicionar no sealed class Screen
object MinhaScreen : Screen("minha", "Minha", R.drawable.ic_minha)

// 2. Adicionar na lista de screens
val screens = listOf(Screen.Daily, Screen.Weekly, Screen.MinhaScreen, ...)

// 3. Adicionar no NavHost
composable(Screen.MinhaScreen.route) { 
    MinhaNovaScreen(innerPadding, disciplinaViewModel) 
}
```

## 🔄 Trabalhando com Estados

### Observar Estados do ViewModel
```kotlin
// Sempre use collectAsState() para observar StateFlows
val disciplinas by viewModel.disciplinas.collectAsState()
val isLoading by viewModel.isLoading.collectAsState()
val weeklySchedule by viewModel.weeklySchedule.collectAsState()
```

### Estados Locais da Tela
```kotlin
// Use remember para estado que não precisa sobreviver à reconfiguração
var selectedItem by remember { mutableStateOf<Item?>(null) }

// Use rememberSaveable para estado que deve sobreviver
var searchQuery by rememberSaveable { mutableStateOf("") }
```

### Estados Derivados
```kotlin
// Use remember com dependências para derivar estados
val filteredList = remember(list, filter) {
    list.filter { it.matches(filter) }
}
```

## 💾 Carregando e Salvando Dados

### Carregar do Cache Local
```kotlin
// No ViewModel (já implementado)
fun carregarDisciplinasLocal() {
    viewModelScope.launch {
        _isLoading.value = true
        withContext(Dispatchers.IO) {
            val disciplinas = lerDisciplinasLocal(context)
            _disciplinas.value = disciplinas
        }
        _isLoading.value = false
    }
}
```

### Carregar de Arquivo HTML
```kotlin
// Usar o launcher já configurado na StatusScreen
val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument(),
    onResult = { uri -> htmlUri = uri }
)

Button(onClick = { launcher.launch(arrayOf("text/html")) }) {
    Text("Selecionar HTML")
}

// Processar quando arquivo é selecionado
LaunchedEffect(htmlUri) {
    htmlUri?.let { uri ->
        // Processar arquivo...
        viewModel.carregarDeArquivoHtml(filePath)
    }
}
```

### Salvar Dados
```kotlin
// No ViewModel
viewModel.salvarDisciplinasLocal(novasDisciplinas)
```

## 🎨 Padrões de UI

### Loading State
```kotlin
if (isLoading) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
} else {
    // Conteúdo normal
}
```

### Lista com LazyColumn
```kotlin
LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    items(items.size, key = { index -> items[index].id }) { index ->
        ItemCard(items[index])
    }
}
```

### Dialog
```kotlin
var showDialog by remember { mutableStateOf(false) }

if (showDialog) {
    AlertDialog(
        onDismissRequest = { showDialog = false },
        confirmButton = {
            Button(onClick = { showDialog = false }) {
                Text("OK")
            }
        },
        title = { Text("Título") },
        text = { Text("Conteúdo") }
    )
}
```

## ⚡ Performance - Boas Práticas

### ✅ DO's

#### 1. Cache Estados Derivados
```kotlin
// ✅ BOM: Cached com StateFlow
val weeklySchedule: StateFlow<List<HorarioSemanal>> = _disciplinas
    .map { montarHorariosSemanaisDeDisciplinas(it) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
```

#### 2. Use remember para Cálculos Pesados
```kotlin
// ✅ BOM: Recalcula só quando dependências mudam
val filteredItems = remember(items, query) {
    items.filter { it.name.contains(query) }
}
```

#### 3. I/O em Background
```kotlin
// ✅ BOM: Não bloqueia UI
viewModelScope.launch {
    withContext(Dispatchers.IO) {
        val data = readFromFile()
    }
}
```

#### 4. Keys em LazyLists
```kotlin
// ✅ BOM: Ajuda com recomposições
LazyColumn {
    items(items.size, key = { items[it].id }) { index ->
        ItemRow(items[index])
    }
}
```

### ❌ DON'Ts

#### 1. Não Recalcule Sempre
```kotlin
// ❌ RUIM: Recalcula a cada recomposição
@Composable
fun MyScreen(viewModel: ViewModel) {
    val items = viewModel.getItems() // Chamado toda vez!
}

// ✅ BOM: Observa StateFlow
@Composable
fun MyScreen(viewModel: ViewModel) {
    val items by viewModel.items.collectAsState()
}
```

#### 2. Não Bloqueie a Main Thread
```kotlin
// ❌ RUIM: Bloqueia UI
fun loadData() {
    val data = readFile() // Thread principal!
    updateUI(data)
}

// ✅ BOM: Background thread
fun loadData() {
    viewModelScope.launch {
        val data = withContext(Dispatchers.IO) {
            readFile()
        }
        updateUI(data)
    }
}
```

#### 3. Não Crie Novos Objetos Toda Vez
```kotlin
// ❌ RUIM: Cria novo objeto a cada recomposição
@Composable
fun MyComposable() {
    val padding = PaddingValues(16.dp) // Nova instância!
}

// ✅ BOM: Reutiliza
@Composable
fun MyComposable() {
    val padding = remember { PaddingValues(16.dp) }
}
```

## 🧪 Testando Seu Código

### Unit Test para ViewModel
```kotlin
@Test
fun `carregarDisciplinas deve atualizar estado`() = runTest {
    // Given
    val viewModel = DisciplinaViewModel(context, fileProcessor)
    
    // When
    viewModel.carregarDisciplinasLocal()
    advanceUntilIdle() // Aguarda coroutines
    
    // Then
    assertFalse(viewModel.isLoading.value)
    assertTrue(viewModel.disciplinas.value.isNotEmpty())
}
```

### UI Test
```kotlin
@Test
fun testDailyScreenShowsLoading() {
    composeTestRule.setContent {
        DailyScreen(PaddingValues(), viewModel)
    }
    
    // Verifica se loading indicator é exibido
    composeTestRule.onNode(hasTestTag("loading"))
        .assertIsDisplayed()
}
```

## 🔧 Debugging

### Log de Estados
```kotlin
// Use LaunchedEffect para observar mudanças
LaunchedEffect(disciplinas) {
    Log.d("MyScreen", "Disciplinas atualizadas: ${disciplinas.size}")
}
```

### Compose Layout Inspector
1. Execute o app em modo debug
2. Tools → Layout Inspector
3. Visualize hierarquia de Composables
4. Veja recomposições em tempo real

## 📦 Dependências Importantes

```gradle
// Jetpack Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")

// Navigation
implementation("androidx.navigation:navigation-compose")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android")

// Hilt
implementation("com.google.dagger:hilt-android")
kapt("com.google.dagger:hilt-compiler")

// DataStore
implementation("androidx.datastore:datastore-preferences")

// HTML Parsing
implementation("org.jsoup:jsoup")
```

## 🎯 Fluxo de Trabalho Típico

### Adicionando uma Nova Funcionalidade

1. **Defina o Modelo de Dados** (se necessário)
   ```kotlin
   data class MinhaEntidade(val id: String, val nome: String)
   ```

2. **Adicione ao ViewModel**
   ```kotlin
   private val _minhasEntidades = MutableStateFlow<List<MinhaEntidade>>(emptyList())
   val minhasEntidades: StateFlow<List<MinhaEntidade>> = _minhasEntidades
   
   fun carregarEntidades() {
       viewModelScope.launch {
           _isLoading.value = true
           withContext(Dispatchers.IO) {
               val entidades = repository.getEntidades()
               _minhasEntidades.value = entidades
           }
           _isLoading.value = false
       }
   }
   ```

3. **Crie a UI**
   ```kotlin
   @Composable
   fun MinhaTelaScreen(viewModel: DisciplinaViewModel) {
       val entidades by viewModel.minhasEntidades.collectAsState()
       val isLoading by viewModel.isLoading.collectAsState()
       
       if (isLoading) {
           LoadingIndicator()
       } else {
           LazyColumn {
               items(entidades.size) { index ->
                   EntidadeCard(entidades[index])
               }
           }
       }
   }
   ```

4. **Adicione à Navegação**
   - Defina rota em `NavHostMain.kt`
   - Adicione item na NavigationBar
   - Configure o composable no NavHost

5. **Teste**
   - Teste unitário do ViewModel
   - Teste de UI se necessário
   - Teste manual no dispositivo/emulador

## 🐛 Problemas Comuns e Soluções

### Recomposições Infinitas
```kotlin
// ❌ PROBLEMA: Cria nova lista toda vez
@Composable
fun MyScreen() {
    val items = listOf(1, 2, 3) // Nova lista!
}

// ✅ SOLUÇÃO: Use remember
@Composable
fun MyScreen() {
    val items = remember { listOf(1, 2, 3) }
}
```

### StateFlow não Atualiza UI
```kotlin
// ❌ PROBLEMA: Não coleta o estado
@Composable
fun MyScreen(viewModel: ViewModel) {
    val items = viewModel.items.value // Snapshot estático!
}

// ✅ SOLUÇÃO: Use collectAsState
@Composable
fun MyScreen(viewModel: ViewModel) {
    val items by viewModel.items.collectAsState()
}
```

### ViewModel Recriado
```kotlin
// ❌ PROBLEMA: Novo ViewModel a cada recomposição
@Composable
fun MyScreen() {
    val viewModel = DisciplinaViewModel(...)
}

// ✅ SOLUÇÃO: Use hiltViewModel()
@Composable
fun MyScreen(viewModel: DisciplinaViewModel = hiltViewModel()) {
    // viewModel persiste
}
```

## 📚 Recursos Adicionais

- [Documentação Completa](./ARCHITECTURE.md)
- [Análise e Melhorias](./IMPROVEMENTS_PT.md)
- [Diagramas Visuais](./DIAGRAMS.md)
- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)

## 🤝 Contribuindo

1. Fork o repositório
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Add: MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

### Convenções de Commit
```
Add: Nova funcionalidade
Fix: Correção de bug
Update: Atualização de funcionalidade existente
Refactor: Refatoração de código
Docs: Mudanças na documentação
Style: Mudanças de formatação
Test: Adição ou correção de testes
```

---

**Dúvidas?** Consulte a documentação completa ou abra uma issue no GitHub!
