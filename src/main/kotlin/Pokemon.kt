import kotlin.random.Random

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

    //métodos

    fun atacar(enemigo:Pokemon):Int {
        energia += ataqueRapido.carga
        return ((ataque/defensa.toFloat())*0.5*ataqueRapido.potencia*(Random.nextFloat()+1)).toInt()
    }
    fun recibir(danyo:Int) {
        vidaRestante -= danyo
        if (vidaRestante < 0) {vidaRestante = 0}
    }
    fun curar(cura:Int) {
        vidaRestante+=cura
        if (vidaRestante > HP) {vidaRestante = HP}
    }
    fun gastarEnergia() {
        energia = 0
    }
    fun modificadorAtaque():Float {
        if (estados.isEmpty()) {return 1f}
        return estados.first().potenciador
    }
    fun modificadorDefensa():Float {
        if (estados.isEmpty()) {return 1f}
        return estados.first().escudo
    }
    fun eliminarEstado() {
        if (estados.isNotEmpty()) {estados.removeFirst()}
    }
    fun eliminarEstados() {
        estados = mutableListOf()
    }
    fun estado():String {
        if (estados.isNotEmpty()) {return estados.first().etiqueta}
        else return ""
    }
    fun ganarExperiencia(exp:Int) {
        experiencia+= exp
        if (experiencia >= nivel*10) { subirNivel() }
    }
    fun subirNivel() {
        nivel++
        HP += (especie.HP/50 + (1..3).random())
        ataque += (especie.ataque/50 + (1..3).random())
        defensa += (especie.ataque/50 + (1..3).random())
        vidaRestante = HP
        experiencia = 0
    }
    fun puedeEvolucionar():Boolean {
        if (nivel >=4 && especie.evoluciona != null) {return true}
        return false
    }
    fun evolucionar():String {
        var especieAntigua = especie
        especie = especie.evoluciona!!
        curar(especie.HP)
        return "¡$especieAntigua evoluciona a $especie!"
    }
}