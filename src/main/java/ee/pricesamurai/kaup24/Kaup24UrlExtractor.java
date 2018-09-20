package ee.pricesamurai.kaup24;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Kaup24UrlExtractor {

    private static final String PHONES_URL = "https://kaup24.ee/et/mobiiltelefonid-foto--videokaamerad" +
            "/mobiiltelefonid-ja-aksessuaarid/mobiiltelefonid?page=2";

    private List<String> productsUrl;

    private void extractUrlPromPage() throws IOException {
        Document document = Jsoup.connect(PHONES_URL).get();
        Elements aElements = document.select("div[id^=productBlock] > div > div > a.cover-link");

        productsUrl = aElements.stream().map(element -> element.attr("href")).collect(Collectors.toList());
    }

    public void writeUrlToFile() throws IOException, URISyntaxException {
        extractUrlPromPage();
        Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("kaup24/url.txt")).toURI());
        productsUrl.forEach(url -> {
            try {
                String urlWithNewLine = url + System.lineSeparator();
                Files.write(path, urlWithNewLine.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        System.out.println("All URLs successfully extracted");
    }
}
