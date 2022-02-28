enum class Especie( //stats base de cada especie de pokemon
    val HP:Int,
    val ataque:Int,
    val defensa:Int,
    val tipo:List<Tipo>,
    val evolucion:Int,
    val evoluciona:Especie?,
) {
    IVYSAUR(155,151,143, listOf(Tipo.PLANTA), 2,null),
    BULBASAUR(128,118,111, listOf(Tipo.PLANTA), 1, IVYSAUR),
    CHARMELEON(151,158,126,listOf(Tipo.FUEGO),2, null),
    CHARMANDER(118,116,93,listOf(Tipo.FUEGO), 1,CHARMELEON),
    WARTORTLE(153,126,155, listOf(Tipo.AGUA), 2,null,),
    SQUIRTLE(127,94,121, listOf(Tipo.AGUA), 1, WARTORTLE,),
    PIKACHU(111,112,96, listOf(Tipo.ELECTRICO), 2,null,),
    PICHU(85,77,53, listOf(Tipo.ELECTRICO), 1, PIKACHU),
    CHINCHOU(181,106,97, listOf(Tipo.ELECTRICO,Tipo.AGUA),1,null),
    PLACEHOLDER(0,0,0,listOf(Tipo.ELECTRICO),4,null),
    //añadir el resto...
}