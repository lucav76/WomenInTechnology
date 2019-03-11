import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Log

@Log
@Singleton
class SearchEngine {
    def outputDir = new File("db")

    void downloadAndSaveAllUrls(List<String> links) {
        outputDir.mkdir()

        links.each { downloadAndSaveSingleUrl(it.toURL()) }
    }

    void downloadAndSaveSingleUrl(URL url) {
        try {
            def file = new File(outputDir, url.getPath().replaceAll("[/:]", "_") + ".json")

            if (!file.exists()) {
                file.text = JsonOutput.toJson([
                        url: url,
                        text: url.text])
            }
        } catch (e) {
            /* Missing page - Do nothing */
        }
    }

    List search(String text) {
        outputDir.listFiles().collect { new JsonSlurper().parseText(it.text) }
                .findAll { it.text.toLowerCase().contains(text.toLowerCase()) }
    }

    List searchRegEx(String text) {
        outputDir.listFiles().collect { new JsonSlurper().parseText(it.text) }
                .findAll { it.text.matches("(?s).*"+text+".*") }
    }
}
