# Pokémon
Juego terminado de Pokémon con interfaz en Jetpack Compose. El juego consiste en derrotar tantos enemigos como sea posible con el pokémon que el jugador
elige al iniciar una partida. 

![Juego Terminado](https://i.ibb.co/VN9h2sg/combate16.gif)

### Estructura básica:
La estructura básica del juego se compone de una columna principal, cuyo primer elemento es el logo de Pokémon y su segundo elemento es una función composable que se elige
en función de un switch. Mediante funciones lambda se llama a la pantalla de selección de pokémon, la pantalla de combate o la pantalla de puntuación.

```
@Composable
fun pantallaSeleccion(){}

@Composable
fun pantallaCombate(){}

@Composable
fun pantallaFinal() {}

@Composable
fun App() {
   var pantallaSeleccionada by remember { mutableStateOf(0) }
   val cambiarPantalla:(Int)->Unit = {pantallaSeleccionada = it}

   Column() {
       Image(
           bitmap = useResource("logoPokemon.png") { loadImageBitmap(it) },
           "logo del juego",
           modifier = Modifier.size(400.dp,200.dp)
       )
      
       when (pantallaSeleccionada) {
           0 -> pantallaSeleccion()
           1 -> pantallaCombate()
           2 -> pantallaFinal()
       }
   }

```
### La clase pokémon:
Los pokémon se generan con atributos semi-aleatorios en función de la especie y el nivel que se indique en el constructor. Tienen métodos de ataque, movimiento especial,
cura, subir nivel, evolución, etc. y cuentan con ataques asignados de forma aleatoria en base a su tipo (o tipos, en el caso de pokémon multitipo).

``` 
enum class Especie(
   val HP:Int,
   val ataque:Int,
   val defensa:Int,
   val tipo:Tipo,
   val evolucion:Int,
   val evoluciona:Especie?,
) {
   IVYSAUR(155,151,143,Tipo.PLANTA,2,null),
   BULBASAUR(128,118,111,Tipo.PLANTA,1,IVYSAUR),
   CHARMELEON(151,158,126,Tipo.FUEGO,2,null),
   CHARMANDER(118,116,93,Tipo.FUEGO,1,CHARMELEON),
   WARTORTLE(153,126,155,Tipo.AGUA,2,null,),
   SQUIRTLE(127,94,121,Tipo.AGUA,1,WARTORTLE,),
   PIKACHU(111,112,96,Tipo.ELECTRICO,2,null,),
   PICHU(85,77,53,Tipo.ELECTRICO, 1,PIKACHU),
   //…
}
```
```
class Pokemon(var especie: Especie, var nivel: Int = 1) {
    var HP = especie.HP; private set
    var ataque = especie.ataque; private set
    var defensa = especie.defensa; private set
    val ataqueRapido = AtaqueRapido.values().filter {especie.tipo.contains(it.tipo)}.random()
    val movimientoEspecial = MovimientoEspecial.values().filter {especie.tipo.contains(it.tipo)}.random()
    var estados:MutableList<Estado> = mutableListOf()

    init {
        repeat (nivel-1) {
            HP += (especie.HP/50 + (1..3).random())
            ataque += (especie.ataque/50 + (1..3).random())
            defensa += (especie.ataque/50 + (1..3).random())
        }
    }

    var vidaRestante = HP; private set
    var energia = 0; private set
    var experiencia = 0; private set
```

### Los movimientos especiales:
Mediante los movimientos especiales, los pokémon pueden adquirir estados que modifican sus atributos base para hacerse invulnerables, potenciar su ataque, curarse a lo largo del tiempo
y otras posibilidades. Cada estado se aplica durante un turno y desaparece de la cola de estados al acabar, dejando paso al siguiente que esté en espera. Un movimiento especial
genera automáticamente una serie de estados.

´´´
enum class MovimientoEspecial(
    var tipo:Tipo,
    var coste:Int
) {
    LLAMARADA(Tipo.FUEGO,30) {
        override fun efecto(pokemon: Pokemon):String {
            var turnos = (2..5).random()
            for (turno in 0..turnos) {
                pokemon.estados += Estado(potenciador = 0f, etiqueta = "CARGANDO")
            }
            pokemon.estados += Estado(potenciador = turnos*1.25f, etiqueta = "POTENCIADO")
            return "${pokemon.especie.name} se prepara para lanzar una gran llamarada."
        }
    },

    ENTERRAR (Tipo.PLANTA, 30) {
        override fun efecto(pokemon:Pokemon):String {
            var turnos = (4..8).random()
            var incremento = 0.25f
            for (turno in 0..turnos) {
                incremento*=2
                pokemon.estados += Estado(escudo = 1+incremento, etiqueta = "ENTERRADO")
            }
            return "${pokemon.especie.name} comienza a enterrarse bajo tierra."
        }
    },

    SUMERGIR(Tipo.AGUA,40) {
        override fun efecto(pokemon: Pokemon):String {
            var turnos = (2..5).random()
            for (turno in 0..turnos) {
                pokemon.estados += Estado(escudo=1000f, etiqueta = "SUMERGIDO")
            }
            return "${pokemon.especie.name} se sumerge bajo el agua."
        }
    },

    SOBRECARGA(Tipo.ELECTRICO, 40) {
        override fun efecto(pokemon: Pokemon):String {
            var turnos = (4..8).random()
            var incremento = 0.5f
            for (turno in 0..turnos) {
                pokemon.estados += Estado(potenciador = 1+incremento, escudo = 0.5f, etiqueta = "SOBRECARGADO")
                incremento*=2
            }
            return "${pokemon.especie.name} comienza a sobrecargar sus ataques."
        }
    },

    ;

    open fun efecto(pokemon:Pokemon):String {
        return "Movimiento especial del pokémon."
    }
}
´´´