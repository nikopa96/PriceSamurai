package ee.pricesamurai;

import ee.pricesamurai.kaup24.Kaup24Parser;
import ee.pricesamurai.kaup24.Kaup24UrlExtractor;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {

        // Parse all URLs from page
        Kaup24UrlExtractor kaup24UrlExtractor = new Kaup24UrlExtractor();
        kaup24UrlExtractor.writeUrlToFile();

        // Parse information from each URL
        Kaup24Parser kaup24Parser = new Kaup24Parser();
        kaup24Parser.getKaup24products();
    }
}
