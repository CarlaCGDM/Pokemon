enum class Tipo() {
    FUEGO,
    AGUA,
    PLANTA,
    ELECTRICO,
}

public var fortalezas = mapOf<Tipo,List<Tipo>>(
    Tipo.FUEGO to listOf(Tipo.PLANTA),
    Tipo.AGUA to listOf(Tipo.FUEGO),
    Tipo.ELECTRICO to listOf(Tipo.AGUA),
    Tipo.PLANTA to listOf(Tipo.ELECTRICO),

)