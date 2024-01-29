package oppslag.integrasjoner.saf

internal fun SafDokumentoversikt.toDokumenter(): List<Dokument> {
    return journalposter.filterNotNull().flatMap {
        it.toDokumenter()
    }
}

internal fun SafJournalpost.toDokumenter(): List<Dokument> {
    val relevantDato = relevanteDatoer.first { it.datotype == SafDatoType.DATO_OPPRETTET }.dato
    val gyldigeJournalPostTyper= listOf("I", "U")
    return dokumenter?.filterNotNull()?.filter {
        it.kanVises() && this.journalposttype in gyldigeJournalPostTyper
    }?.map {
        Dokument(
            journalpostId = this.journalpostId,
            dokumentId = it.dokumentInfoId,
            tittel = it.tittel,
            type = this.journalposttype,
            innsendingId = this.eksternReferanseId,
            dato = relevantDato
        )
    } ?: emptyList()
}

internal fun SafDokumentInfo.kanVises() =
    dokumentvarianter.filterNotNull().any { it.kanVises() }

internal fun SafDokumentvariant.kanVises() =
    filtype == "PDF" && brukerHarTilgang && variantformat == SafVariantformat.ARKIV
