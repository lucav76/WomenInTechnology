class WomenInTechnology {
    static void showString(item) { println item.toString().replaceAll(/<.*?>/, "").replaceAll("&.*?;", "")+"\n" }

    static void main(String[] args) {
        // Part 1: Html download and Parsing
        def topic = "Pizza"
        def textUrl = "https://en.wikipedia.org/wiki/$topic"

        println "\n * RegEx parsing *\n"
        WikiCrawler.instance.parseRegEx(textUrl, 1).each { showString(it) }

        println "\n * JSoup parsing *\n"
        WikiCrawler.instance.parseJSoup(textUrl, 1).each { showString(it) }

        println "\n * Wikipedia API *\n"
        println WikiCrawler.instance.wikipediaApi(topic, 3)

        // Part 2: Crawling
        def links = WikiCrawler.instance.extractLinks(textUrl)
        def links2 = links.collect { WikiCrawler.instance.extractLinks(it) + it }.flatten().unique()

        println "\n\nLinks first page: ${links.size()} - Total Links: ${links2.size()}"

        // Part 3: Search Engine
        SearchEngine.instance.downloadAndSaveAllUrls(links2)

        println "\n\nPages containing 'gnocco'"
        SearchEngine.instance.search("gnocco").each { println it.url }

        println "\nPages containing 'gno..o'"
        SearchEngine.instance.searchRegEx("gno..o").each { println it.url }

        println "\nPages containing Calzone"
        SearchEngine.instance.search("Calzone").each { println it.url }
    }
}
