import java.time.LocalDate

plugins {
    id("net.kyori.indra.licenser.spotless")
}

indraSpotlessLicenser {
    licenseHeaderFile(rootProject.file("HEADER.txt"))
    property("YEAR", LocalDate.now().year.toString())
    newLine(true)
}
