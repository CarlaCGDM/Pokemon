enum class AtaqueRapido(
    val tipo:Tipo,
    val potencia:Int,
    val carga:Int
    ) {

    SEMILLADORA(Tipo.PLANTA, 5, 13),
    HOJA_AFILADA(Tipo.PLANTA, 10, 4),
    ASCUAS(Tipo.FUEGO, 7,6),
    INCINERAR(Tipo.FUEGO, 15,20),
    BURBUJA(Tipo.AGUA, 7,11),
    SALPICADURA(Tipo.AGUA, 0,12),
    CHISPA(Tipo.ELECTRICO, 4,8),
    IMPACTRUENO(Tipo.ELECTRICO, 3,9),
    //...
}