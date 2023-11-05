package com.sample.demo
import com.sample.demo.database.DoctorEntity
import com.sample.demo.database.DoctorsRepository
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


class ParsingManager {

    val baseUrl = "https://www.docfinder.at"
    val doctorsList: MutableList<DoctorData> = mutableListOf()

    lateinit var repository: DoctorsRepository

    fun setRepo(repo: DoctorsRepository) {
        repository = repo
    }

//    val fows = listOf("zahnarzt", "praktischer-arzt", "hautarzt", "frauenarzt", "orthopaede", "augenarzt", "plastischer-chirurg", "apotheke")
    val fows = listOf("zahnarzt",)

    private fun persistDocs() {
        println("Writing ${doctorsList.size} docs to database")
        doctorsList.forEach { docData ->
            println("Writing ${docData.name}")
            val ent = docData.toEntity()
            repository.save(ent)
        }
    }

    fun downloadData() {
        doctorsList.clear()

        val zips = listOf("1060",)
        fows.forEach { fow ->
            println("Getting all FOW: $fow")

            zips.forEach {
                println("Searching PLZ: $it")

                downloadData(fow, it)
            }
        }
        persistDocs()
    }

    private fun downloadData(fow: String, zipCode: String) {

        val pageUrls = mutableListOf("/suche/$fow/$zipCode")
        val doctorUrls: MutableList<String> = mutableListOf()

        /*var mod = 0*/

        var path = "$baseUrl${pageUrls[0]}"
        var doc = Jsoup.connect(path).get()
        var next = getNextUrl(doc)

        while (!next.isNullOrBlank()) {
            // SCAN PAGE
            // ADD EVERY DOCTOR URL TO [doctorUrls]

            println("Processing: $next")

            val searchRes = doc.select(".search-results").select("a[aria-label]")

            searchRes.forEach { doctorTag ->
                val link = doctorTag.attr("href")
                doctorUrls.add(link)
            }

            // Path to next page
            path = "$baseUrl$next"

            // Load and parse HTML from next page
            doc = Jsoup.connect(path).get()

            //println("DONE")
            next = getNextUrl(doc)
        }

        val searchRes = doc.select(".search-results").select("a[aria-label]")

        searchRes.forEach { doctorTag ->
            val link = doctorTag.attr("href")
            doctorUrls.add(link)
        }

        /*mod = doctorsList.size*/
        doctorUrls.forEachIndexed { i, it ->
            doctorsList.add(parseDoctorData("$baseUrl$it", zipCode))
        }
    }

    fun getNextUrl(doc: Document): String? {
        val next: String? = doc
            .getElementsByClass("btn icon-right")
            .first()?.attr("href")
        return next
    }

    /**
     * Returns a text contained in HTTP-GET response
     */
    fun getHtml(path: String): String {
        val url = URL(path)
        val connection = url.openConnection()
        BufferedReader(InputStreamReader(connection.getInputStream())).use { inp ->
            return inp.readText()
        }
    }

    fun parseDoctorData(path: String, zip: String): DoctorData {
        val page = Jsoup.connect(path).get()
        println("scanning $path")
        val data = DoctorData(
            name = page.select("h1").text(),
            fow = page.select("div.professions").select("span").first()?.text()?:"",
            phone = page.select(".phone-number").select("a").text(),
            insurances = convertToList(page.select(".insurances.section.hidden-lg-up").select("li").text()),
            languages = convertToList(
                page
                    .select(".section.languages.profiles-collapse-btn.profiles-collapse.profiles-collapse-mobile.open.profiles-collapse-legal-event.hidden-lg-up")
                    .select(".expanded.content.profiles-time-wrapper")
                    .select("ul")
                    .select("li")
                    .text()
            ),
            zip = zip
        )
//        println("done")
        return data
    }

    fun convertToList(convertee: String): List<String> {
        val lll = convertee.split(" ")
        return lll
    }

    data class DoctorData (
        val name: String,
        val fow: String, //Field of work
        val phone: String,
        val insurances: List<String>,
        val languages: List<String>,
        val zip: String
    )

    fun DoctorData.toEntity():DoctorEntity =
        DoctorEntity(
            name = this.name,
            fow = this.fow,
            phone = this.phone,
            insurances = this.insurances.joinToString(),
            languages = this.languages.joinToString(),
            zip = this.zip
        )
}
