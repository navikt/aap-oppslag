package oppslag.integrasjoner.pdl

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PdlResponsFunctionsTest {

    private fun testperson(fødselsdato: LocalDate) = PdlPerson(
        adressebeskyttelse = emptyList(),
        navn = listOf(PdlNavn(fornavn = "Test", etternavn = "Testesen", mellomnavn = null)),
        foedselsdato = listOf(PdlFoedsel(foedselsdato = fødselsdato.toString())),
        fnr = "12345678910",
        code = null
    )

    @Test
    fun `bruker er over 18 år`() {
        val person = testperson(LocalDate.now().minusYears(25))
        val søker = person.toSøker()
        assertFalse(søker.erUnderAttenÅr)
    }

    @Test
    fun `bruker som fyller 18 år i dag er ikke under 18 år`() {
        val person = testperson(LocalDate.now().minusYears(18))
        val søker = person.toSøker()
        assertFalse(søker.erUnderAttenÅr)
    }

    @Test
    fun `bruker som fyller 18 år i morgen er under 18 år`() {
        val person = testperson(LocalDate.now().minusYears(18).plusDays(1))
        val søker = person.toSøker()
        assertTrue(søker.erUnderAttenÅr)
    }

    @Test
    fun `bruker er under 18 år`() {
        val person = testperson(LocalDate.now().minusYears(17).minusMonths(5))
        val søker = person.toSøker()
        assertTrue(søker.erUnderAttenÅr)
    }

    @Test
    fun `bruker uten noen fødselsdatoer skal håndteres som om de er over 18 år`() {
        val person = PdlPerson(
            adressebeskyttelse = emptyList(),
            navn = listOf(PdlNavn(fornavn = "Test", etternavn = "Testesen", mellomnavn = null)),
            foedselsdato = emptyList(),
            fnr = "12345678910",
            code = null
        )
        val søker = person.toSøker()
        assertFalse(søker.erUnderAttenÅr)
    }

    @Test
    fun `bruker hvor fødselsdato er null skal håndteres som om de er over 18 år v2`() {
        val person = PdlPerson(
            adressebeskyttelse = emptyList(),
            navn = listOf(PdlNavn(fornavn = "Test", etternavn = "Testesen", mellomnavn = null)),
            foedselsdato = listOf(PdlFoedsel(null)),
            fnr = "12345678910",
            code = null
        )
        val søker = person.toSøker()
        assertFalse(søker.erUnderAttenÅr)
    }

}
