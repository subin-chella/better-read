package io.subin.search;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Controller
public class SearchController {

	private final WebClient webClient;

	public SearchController(WebClient.Builder webclientBuilder) {
		this.webClient = webclientBuilder.baseUrl("http://openlibrary.org/search.json")
				.exchangeStrategies(ExchangeStrategies.builder()
						.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)).build())
				.build();
	}

	@GetMapping(value = "/search")
	public String getSearchResults(@RequestParam String query, Model model) {
		Mono<SearchResults> bodyToMono = this.webClient.get().uri("?q=" + query).retrieve()
				.bodyToMono(SearchResults.class);
		SearchResults searchResults = bodyToMono.block();

		List<SearchResultsBook> book = searchResults.getDocs().stream().limit(10).map(result -> {
			result.setKey(result.getKey().replace("/works/", ""));
			String coverid = result.getCover_i();
			if (StringUtils.hasText(coverid)) {
				coverid = "https://covers.openlibrary.org/b/id/" + coverid + "-M.jpg";
			} else {
				coverid = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/1024px-No_image_available.svg.png";
			}
			result.setCover_i(coverid);
			return result;

		}).collect(Collectors.toList());

		model.addAttribute("book", book);

		return "search";
	}

}
