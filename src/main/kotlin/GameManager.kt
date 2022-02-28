class GameManager(var jugador:Pokemon, var enemigo: Pokemon) {

    fun ataqueJugador():String {
        var plusAtaque = jugador.modificadorAtaque()
        var plusDefensa = enemigo.modificadorDefensa()
        var dmg = (jugador.atacar(enemigo)*plusAtaque/plusDefensa).toInt()*5
        enemigo.recibir(dmg)
        return "${jugador.especie} lanza ${jugador.ataqueRapido.name} contra ${enemigo.especie} y le hace $dmg puntos de daño."
    }
    fun ataqueEnemigo():String {
        var plusAtaque = enemigo.modificadorAtaque()
        var plusDefensa = jugador.modificadorDefensa()
        var dmg = (enemigo.atacar(jugador)*plusAtaque/plusDefensa).toInt()*5
        jugador.recibir(dmg)
        return "${enemigo.especie} lanza ${enemigo.ataqueRapido.name} contra ${jugador.especie} y le hace $dmg puntos de daño."
    }
    fun curarJugador():String {
        var suerte = (1..10).random()
        var cura = 0
        when (suerte) {
            1 -> {cura = jugador.HP/2}
            else -> {cura = jugador.HP/10}
        }
        jugador.curar(cura)
        return "${jugador.especie.name} se cura $cura puntos de vida."
    }
    fun curarEnemigo():String {
        var suerte = (1..10).random()
        var cura = 0
        when (suerte) {
            1 -> {cura = enemigo.HP/2}
            else -> {cura = enemigo.HP/10}
        }
        cura += (0..10).random()
        enemigo.curar(cura)
        return "${enemigo.especie.name} se cura $cura puntos de vida."
    }
    fun movimientoEspecialJugador():String {
        jugador.gastarEnergia()
        return jugador.movimientoEspecial.efecto(jugador)
    }
    fun movimientoEspecialEnemigo():String {
        enemigo.gastarEnergia()
        return enemigo.movimientoEspecial.efecto(enemigo)
    }
    fun movimientoEspecialJugadorDisponible():Boolean {
        if (jugador.energia >= jugador.movimientoEspecial.coste && jugador.estados.isEmpty()) {return true}
        return false
    }
    fun movimientoEspecialEnemigoDisponible():Boolean {
        if (enemigo.energia >= enemigo.movimientoEspecial.coste && jugador.estados.isEmpty()) {return true}
        return false
    }
    fun limpiarUltimoEstado() {
        jugador.eliminarEstado()
        enemigo.eliminarEstado()
    }
    fun regenerarJugador() {
        jugador.eliminarEstados()
        jugador.curar(jugador.HP)
    }
    fun resetearEnergia() {
        jugador.gastarEnergia()
    }
    fun regenerarEnemigo() {
        enemigo = Pokemon(
            especie = Especie.values().filter { it.evolucion == jugador.especie.evolucion }.random(),
            nivel = (jugador.nivel..jugador.nivel+3).random()
        )
    }
    fun turnoEnemigo():String {
        when (movimientoEspecialEnemigoDisponible()) {
            true -> {return movimientoEspecialEnemigo()}
            else -> {
                var suerte = (1..5).random()
                var urgencia = enemigo.vidaRestante*10/enemigo.HP
                when {
                    (urgencia < 2) -> { if (suerte < 4) {return curarEnemigo()}}
                    (urgencia < 5) -> { if (suerte < 2) {return curarEnemigo()}}
                    else -> {if (suerte == 1) {return curarEnemigo()}}
                }
                return ataqueEnemigo()
            }
        }
    }
    fun finCombate() {
        darExperiencia()
        regenerarJugador()
        resetearEnergia()
        regenerarEnemigo()
    }
    fun darExperiencia() {
        var experiencia = enemigo.nivel*(7..14).random()
        jugador.ganarExperiencia(experiencia)
    }

}