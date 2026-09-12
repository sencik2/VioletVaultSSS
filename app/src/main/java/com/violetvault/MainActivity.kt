package com.violetvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF10091A)
private val Panel = Color(0xFF1A1028)
private val Panel2 = Color(0xFF241637)
private val Purple = Color(0xFF8B5CF6)
private val Purple2 = Color(0xFFC4B5FD)
private val Muted = Color(0xFFAAA0BB)

data class VaultItem(val name: String, val login: String, val password: String, val site: String)
data class NoteItem(val name: String, val text: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { VioletVaultApp() }
    }
}

@Composable
fun VioletVaultApp() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Bg,
            surface = Panel,
            primary = Purple,
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {
        var unlocked by remember { mutableStateOf(false) }
        if (unlocked) HomeScreen() else LockScreen { unlocked = true }
    }
}

@Composable
private fun LockScreen(unlock: () -> Unit) {
    var pass by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize().background(Bg),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(22.dp),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Panel)
        ) {
            Column(
                modifier = Modifier.padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Lock, null, tint = Purple2, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(12.dp))
                Text("VIOLET VAULT", color = Purple2, fontSize = 27.sp, fontWeight = FontWeight.Bold)
                Text("Твой личный цифровой сейф", color = Muted, modifier = Modifier.padding(top = 7.dp, bottom = 24.dp))

                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = { Text("Мастер-пароль") },
                    singleLine = true,
                    visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { visible = !visible }) {
                            Icon(if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { if (pass.length >= 6) unlock() },
                    modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple)
                ) { Text("Открыть сейф") }

                Text(
                    "Минимум 6 символов",
                    color = Muted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun HomeScreen() {
    var tab by remember { mutableStateOf(0) }
    var search by remember { mutableStateOf("") }
    var passwords by remember {
        mutableStateOf(
            listOf(
                VaultItem("Discord", "sencik", "demo1234", "discord.com"),
                VaultItem("Minecraft", "player", "minecraft123", "minecraft.net")
            )
        )
    }
    var notes by remember {
        mutableStateOf(listOf(NoteItem("Идеи", "Добавить синхронизацию ПК ↔ телефон.")))
    }

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            NavigationBar(containerColor = Panel) {
                NavigationBarItem(tab == 0, { tab = 0 }, icon = { Icon(Icons.Default.Password, null) }, label = { Text("Пароли") })
                NavigationBarItem(tab == 1, { tab = 1 }, icon = { Icon(Icons.Default.Note, null) }, label = { Text("Заметки") })
                NavigationBarItem(tab == 2, { tab = 2 }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Настройки") })
            }
        }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            Text("✦ VIOLET VAULT", color = Purple2, fontSize = 23.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(22.dp, 22.dp, 22.dp, 3.dp))
            Text(when (tab) { 0 -> "Твои пароли"; 1 -> "Твои заметки"; else -> "Настройки" }, color = Muted, modifier = Modifier.padding(horizontal = 22.dp))

            if (tab == 0 || tab == 1) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Поиск") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(16.dp, 14.dp, 16.dp, 4.dp)
                )
            }

            when (tab) {
                0 -> {
                    val filtered = passwords.filter { it.name.contains(search, true) || it.site.contains(search, true) }
                    LazyColumn(Modifier.padding(16.dp)) {
                        items(filtered) { item -> PasswordCard(item) }
                    }
                }
                1 -> {
                    val filtered = notes.filter { it.name.contains(search, true) || it.text.contains(search, true) }
                    LazyColumn(Modifier.padding(16.dp)) {
                        items(filtered) { item -> NoteCard(item) }
                    }
                }
                else -> SettingsPage()
            }
        }
    }
}

@Composable
private fun PasswordCard(item: VaultItem) {
    var show by remember { mutableStateOf(false) }
    Card(colors = CardDefaults.cardColors(containerColor = Panel2), modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Column(Modifier.padding(17.dp)) {
            Text(item.name, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(item.login, color = Muted)
            Text(item.site, color = Purple2, fontSize = 12.sp)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 10.dp)) {
                Text(if (show) item.password else "••••••••", color = Muted, modifier = Modifier.weight(1f))
                IconButton(onClick = { show = !show }) { Icon(if (show) Icons.Default.VisibilityOff else Icons.Default.Visibility, null) }
            }
        }
    }
}

@Composable
private fun NoteCard(item: NoteItem) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel2), modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Column(Modifier.padding(17.dp)) {
            Text(item.name, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(item.text, color = Muted, modifier = Modifier.padding(top = 5.dp))
        }
    }
}

@Composable
private fun SettingsPage() {
    Column(Modifier.padding(22.dp)) {
        Text("Violet Vault v1.0", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text("Первая версия интерфейса готова к сборке. Следующий этап — настоящее шифрованное хранилище, биометрия, генератор паролей и автоматическая блокировка.", color = Muted)
        Spacer(Modifier.height(20.dp))
        Text("Важно: демо-данные в этой версии не предназначены для хранения реальных паролей.", color = Purple2)
    }
}
