// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun pantallaSeleccion(
    cambiarPantalla:(Int)->Unit,
    elegirPokemon:(Pokemon)->Unit) {

    var seleccion:Especie? by remember { mutableStateOf(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(0.dp,25.dp)) {
            for (especie in Especie.values().filter { it.evolucion == 1 }) {
                var imgSize = 0.dp
                when {
                    (seleccion == especie) -> {imgSize = 150.dp}
                    (seleccion != especie) -> {imgSize = 140.dp}
                }
                    Image(
                        bitmap = useResource("sprites/${especie.name}.png") { loadImageBitmap(it) },
                        contentDescription = "imagen del pokemon ${especie.name}",
                        modifier = Modifier.size(imgSize).clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null) { seleccion = especie },
                    )
            }
        }

        if (seleccion != null) {
            Button(
                onClick = { cambiarPantalla(1); elegirPokemon(Pokemon(seleccion!!,1))},
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = Color(55,97,171),
                    contentColor = Color.White)
            ) {
                Text("Jugar con ${seleccion?.name}")
            }
        }
    }
}

@Composable
fun pantallaCombate(
    cambiarPantalla: (Int) -> Unit,
    sumarPunto: ()-> Unit,
    jugador:Pokemon){

    var enemigo by remember { mutableStateOf(
        Pokemon(Especie.values().toList().filter { it.evolucion == 1 }.random(), (1..3).random())
    ) }

    var narracion by remember { mutableStateOf("¡Un ${enemigo.especie.name} salvaje apareció!") }
    var vidaJugador by remember { mutableStateOf(jugador.HP) }
    var vidaEnemigo by remember { mutableStateOf(enemigo.HP) }
    var estadoJugador by remember { mutableStateOf("")}
    var estadoEnemigo by remember { mutableStateOf("")}
    var gameManager = GameManager(jugador,enemigo)
    var corutina = rememberCoroutineScope()
    var botonesActivos by remember { mutableStateOf(true) }

    fun refrescarInterfaz() {
        vidaJugador = jugador.vidaRestante
        vidaEnemigo = enemigo.vidaRestante
        enemigo = gameManager.enemigo
        estadoJugador = jugador.estado()
        estadoEnemigo = enemigo.estado()
    }

    fun terminarTurno() {
        refrescarInterfaz()
        botonesActivos  = false
        when(enemigo.vidaRestante) {
            0 -> {
                corutina.launch() {
                    delay(1500L)
                    gameManager.finCombate()
                    sumarPunto()
                    refrescarInterfaz()
                    narracion = "¡Un ${enemigo.especie.name} salvaje apareció!"
                    refrescarInterfaz()
                    botonesActivos = true
                }
            }
            else -> {
                corutina.launch() {
                    delay(1500L)
                    narracion = gameManager.turnoEnemigo()
                    gameManager.limpiarUltimoEstado()
                    refrescarInterfaz()
                    botonesActivos = true
                    if (vidaJugador == 0) {
                        botonesActivos = false
                        corutina.launch() {
                            delay(1500L)
                            cambiarPantalla(2)
                        }
                    }

                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(20.dp,0.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("${estadoJugador}")
                Text("${jugador.especie.name} Nv${jugador.nivel}")
                Box(modifier = Modifier.size(140.dp,15.dp).background(color = Color.DarkGray)) {
                    Box(modifier = Modifier.size((vidaJugador*140/jugador.HP).dp,15.dp).background(color = Color.Green))
                }
                Image(
                    bitmap = useResource("sprites/${jugador.especie.name}.png") { loadImageBitmap(it)},
                    contentDescription = "Imagen del pokemon del jugador.",
                    modifier = Modifier.size(140.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            )  {
                Text("${estadoEnemigo}")
                Text("${enemigo.especie.name} Nv${enemigo.nivel}")
                Box(modifier = Modifier.size(140.dp,15.dp).background(color = Color.DarkGray)) {
                    Box(modifier = Modifier.size((vidaEnemigo*140/enemigo.HP).dp,15.dp).background(color = Color.Red))
                }
                Image(
                    bitmap = useResource("spritesVolteadas/${enemigo.especie.name}.png") { loadImageBitmap(it)},
                    contentDescription = "Imagen del pokemon enemigo.",
                    modifier = Modifier.size(140.dp)
                )
            }
        }
        Box(modifier = Modifier.size(800.dp,100.dp).background(color = Color.White).padding(10.dp)) { Text("$narracion") }
        Row {
            Button(
                onClick = {
                    narracion = gameManager.ataqueJugador()
                    terminarTurno()
                },
                enabled = botonesActivos,
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = Color(55,97,171),
                    contentColor = Color.White)) { Text("${jugador.ataqueRapido.name}") }
            Button(onClick = {
                    narracion = gameManager.movimientoEspecialJugador()
                    terminarTurno()
            },
                modifier = Modifier.padding(5.dp,0.dp),
                enabled = (botonesActivos && gameManager.movimientoEspecialJugadorDisponible()),
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = Color(55,97,171),
                    contentColor = Color.White)) {
                var porcentaje = jugador.energia*100f/jugador.movimientoEspecial.coste
                if (porcentaje > 100f) {porcentaje = 100f}
                when (gameManager.movimientoEspecialJugadorDisponible()) {
                    true -> Text("${jugador.movimientoEspecial.name}")
                    else -> Text("${porcentaje.toInt()}%")
                }
            }
            Button(
                onClick = {
                    narracion = gameManager.curarJugador()
                    terminarTurno()
            },
                enabled = botonesActivos,
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = Color(55,97,171),
                    contentColor = Color.White)) { Text("Curar Pokémon") }

            Button(
                onClick = {
                    narracion = jugador.evolucionar()
                    terminarTurno()
                },
                enabled = (botonesActivos && jugador.puedeEvolucionar()),
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = Color.Green,
                    contentColor = Color.White),
                modifier = Modifier.padding(5.dp, 0.dp)) { Text("Evolucionar") }

            Button(
                onClick = {
                    cambiarPantalla(2)
                },
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = Color.Red,
                    contentColor = Color.White)) { Text("Huir del Combate") }
        }
    }
}

@Composable
fun pantallaFinal(
    cambiarPantalla: (Int) -> Unit,
    cambiarFondo: () -> Unit,
    resetearPuntuacion: ()->Unit,
    jugador:Pokemon,
    puntuacion:Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(0.dp,25.dp).background(color=Color.White)
    ) {
        Text("¡Fin del Juego!", fontSize = 2f.em, modifier = Modifier.padding(0.dp,5.dp))
        Text("${jugador.especie.name} ha derrotado a $puntuacion enemigos.", fontSize = 1f.em, color = Color(55,97,171))
        Button(
            onClick = { cambiarPantalla(0); cambiarFondo(); resetearPuntuacion()},
            modifier = Modifier.padding(0.dp,5.dp),
            colors = ButtonDefaults.textButtonColors(
                backgroundColor = Color(55, 97, 171),
                contentColor = Color.White
            )
        ) { Text("Jugar de Nuevo") }
    }
}

@Composable
fun App() {
    var pantallaSeleccionada by remember { mutableStateOf(0) }
    val cambiarPantalla:(Int)->Unit = {pantallaSeleccionada = it}

    var fondo by remember  { mutableStateOf((1..7).random()) }
    val cambiarFondo:()->Unit = {fondo = (1..7).minus(fondo).random()}

    var jugador by remember { mutableStateOf(Pokemon(Especie.PLACEHOLDER)) }
    val elegirPokemon:(Pokemon)->Unit = {jugador = it}

    var puntuacion by remember { mutableStateOf(0) }
    val sumarPunto:()->Unit = {puntuacion++}
    val resetearPuntuacion:()->Unit = {puntuacion = 0}

    Image(
        bitmap = useResource("fondos/fondo ($fondo).png") { loadImageBitmap(it) },
        contentDescription = "fondo",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()) {
        Image(
            bitmap = useResource("logoPokemon.png") { loadImageBitmap(it) },
            contentDescription = "logo del juego",
            modifier = Modifier.size(400.dp, 200.dp)
        )

        when (pantallaSeleccionada) {
            0 -> pantallaSeleccion(cambiarPantalla,elegirPokemon)
            1 -> pantallaCombate(cambiarPantalla, sumarPunto, jugador)
            2 -> pantallaFinal(cambiarPantalla, cambiarFondo, resetearPuntuacion, jugador, puntuacion)
        }
    }


}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Pokémon",
        resizable = false,
        state = WindowState(size = DpSize(800.dp, 600.dp))
    ) {
        App()
    }
}
