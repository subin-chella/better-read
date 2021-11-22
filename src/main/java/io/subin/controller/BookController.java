package io.subin.controller;

import java.nio.file.Path;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.subin.book.Book;
import io.subin.book.BookRepository;
import io.subin.connection.DataStaxAstraProperties;

@Controller
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BookController {
	
	@Autowired
	BookRepository bookRepository;
	
	@GetMapping("/books/{bookId}")
	public String getBook(@PathVariable String bookId, Model model) {
		Optional<Book> optionalBook = bookRepository.findById(bookId);
		if(optionalBook.isPresent()) {
			Book book = optionalBook.get();
			if(book.getCoverIds()!=null && !book.getCoverIds().isEmpty()) {
				String coverImageLink= "https://covers.openlibrary.org/b/id/"+ book.getCoverIds().get(0)+"-L.jpg";
				model.addAttribute("coverImage", coverImageLink);
			} else {
				model.addAttribute("coverImage", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/1024px-No_image_available.svg.png");
			}
			model.addAttribute("book", book);
			
			return "book";
		}
		return "book-not-found";
	}
	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

}
