package com.sample.demo
import com.sample.demo.database.DoctorEntity
import com.sample.demo.database.DoctorsRepository
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


class ParsingManager {

    private val baseUrl = "https://www.docfinder.at"
    private val doctorsList: MutableList<DoctorData> = mutableListOf()
    private val parser = JsonParser()

    lateinit var repository: DoctorsRepository

    private val zips = parser.parse().toSet()

    fun setRepo(repo: DoctorsRepository) {
        repository = repo
    }

    private val fows = listOf("zahnarzt", "praktischer-arzt", "hautarzt", "frauenarzt", "orthopaede", "augenarzt", "plastischer-chirurg", "apotheke")

    private fun persistDocs() {
        appendLog("Writing ${doctorsList.size} docs to database")
        doctorsList.forEach { docData ->
            val matchingEntries = repository.findAllByName(docData.name)
            val ent = docData.toEntity()
            if (!matchingEntries.contains(ent)) {
                appendLog("Writing ${docData.name}")
                repository.save(ent)
            }
        }
        val all = repository.findAll()
        appendLog("DB Contains ${all.toList().size} entries")
    }

    fun downloadData() {
//        check()
//        return
        doctorsList.clear()

        zips.forEach { zip ->
            appendLog("Getting all ZIP: $zip")
            fows.forEach {
                appendLog("Searching FOW: $it")

                downloadData(it, zip)
            }
            persistDocs()
            doctorsList.clear()
        }
        appendLog("SCAN COMPLETE!")
    }

    fun getByZip(zip: String?): List<DoctorEntity> {
        return if (zip != null) {
            repository.findAllByZip(zip).toList()
        } else {
            repository.findAll().toList()
        }
    }

    fun check() {
        repository.deleteAll()
        val all = repository.findAll()
        return
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

            appendLog("Processing: $next")

            val searchRes = doc.select(".search-results").select("a[aria-label]")

            searchRes.forEach { doctorTag ->
                val link = doctorTag.attr("href")
                doctorUrls.add(link)
            }

            // Path to next page
            path = "$baseUrl$next"

            // Load and parse HTML from next page
            doc = Jsoup.connect(path).get()

            //appendLog("DONE")
            next = getNextUrl(doc)
        }

        val searchRes = doc.select(".search-results").select("a[aria-label]")

        searchRes.forEach { doctorTag ->
            val link = doctorTag.attr("href")
            doctorUrls.add(link)
        }

        /*mod = doctorsList.size*/
        doctorUrls.forEach {
            doctorsList.add(parseDoctorData("$baseUrl$it", zipCode))
        }
    }

    private fun getNextUrl(doc: Document): String? {
        return doc
            .getElementsByClass("btn icon-right")
            .first()?.attr("href")
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
        appendLog("scanning $path")
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
