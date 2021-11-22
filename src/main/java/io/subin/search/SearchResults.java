package io.subin.search;

import java.util.List;

public class SearchResults {

	private int numFound;
	private List<SearchResultsBook> docs;

	public int getNumFound() {
		return numFound;
	}

	public List<SearchResultsBook> getDocs() {
		return docs;
	}

	public void setDocs(List<SearchResultsBook> docs) {
		this.docs = docs;
	}

	public void setNumFound(int numFound) {
		this.numFound = numFound;
	}

}
