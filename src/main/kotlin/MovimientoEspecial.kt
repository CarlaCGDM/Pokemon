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