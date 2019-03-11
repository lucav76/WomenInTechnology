import groovy.json.JsonSlurper
import org.jsoup.Jsoup

@Singleton
@SuppressWarnings("MethodMayBeStatic")
class WikiCrawler {
    // Pure Groovy with regular expressions
    List<String> parseRegEx(String textUrl, int numParagraphs=1) {
        def txt = textUrl.toURL().text
        def groups = txt =~ "(?s)<p>(.*?)</p>"

        // Indexes: [group][capture]; usually you want [group][0]
        return groups.collect() { it[0] }.take(numParagraphs)
    }

    // Recommended in general
    List<String> parseJSoup(String textUrl, int numParagraphs=1, int minLenght=30) {
        def doc = Jsoup.connect(textUrl).get()
        def paragraphs = doc.getElementsByTag("p")

        return paragraphs.findAll { it.toString().length() > minLenght }.take(numParagraphs)
                .collect { it.toString() }
    }

    // Recommended for Wikipedia
    String wikipediaApi(String topic, int numSentences=1) {
        def apiUrl = "https://en.wikipedia.org/w/api.php?action=query&titles=$topic&prop=extracts&format=json&explaintext&exsentences=$numSentences"
        def json = new JsonSlurper().parse(apiUrl.toURL())

        return json.query.pages.entrySet()[0].value.extract
    }

    List<String> extractLinks(String textUrl) {
        try {
            def links = Jsoup.connect(textUrl).get().select("a[href]").asList()

            // Keeps only interesting links to other Wikipedia pages
            links.collect { (it.attr("href") =~ /(\/wiki\/Category:.*)/) }
                    .findAll()
                    .collect { "https://en.wikipedia.org" + it[0][1] }
                    .findAll {
                !it.contains("maint") && !it.contains("%") && !it.contains(":Wikipedia") && !it.contains("unsourced")
            }
            .unique()
        }
        catch (e) {
            return []
        }
    }
}
