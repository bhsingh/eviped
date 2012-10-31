package org.biosemantics.eviped.web.ui;

import java.util.Collection;

import org.biosemantics.eviped.tools.service.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class SearchResultPanel extends VerticalLayout {

	private Table searchResultsTable = new Table();
	private static final Logger logger = LoggerFactory.getLogger(SearchResultPanel.class);

	private SearchResultPanel() {
		searchResultsTable.setWidth("100%");
		searchResultsTable.addContainerProperty("weight", Integer.class, null);
		searchResultsTable.addContainerProperty("pubmedLink", Link.class, null);
		searchResultsTable.addContainerProperty("country", String.class, null);
		searchResultsTable.addContainerProperty("publishedYear", Integer.class, null);
		searchResultsTable.addContainerProperty("journalName", String.class, null);

		searchResultsTable.setPageLength(0);
		searchResultsTable.setColumnHeader("weight", "Confidence");
		searchResultsTable.setColumnHeader("pubmedLink", "Pubmed Id");
		searchResultsTable.setColumnHeader("country", "Country");
		searchResultsTable.setColumnHeader("publishedYear", "Year");
		searchResultsTable.setColumnHeader("journalName", "Journal");
		searchResultsTable.setColumnExpandRatio("journalName", 1);
		searchResultsTable.setColumnWidth("journalName", 200);

		this.addComponent(searchResultsTable);
	}

	public static SearchResultPanel getInstance() {
		return new SearchResultPanel();
	}

	public void showResults(Collection<Article> articles) {
		searchResultsTable.setCaption(+articles.size() + " articles found");
		int ctr = 0;
		for (Article article : articles) {
			// logger.info("{}", searchQueryResult);
			Link pubmedLink = new Link("" + article.getPmid(), new ExternalResource(
					"http://www.ncbi.nlm.nih.gov/pubmed?term=" + article.getPmid()));
			pubmedLink.setTargetName("_blank");
			pubmedLink.setDescription(article.getTitle());
			searchResultsTable.addItem(
					new Object[] { article.getWeight(), pubmedLink, article.getCountry(), article.getPublishedYear(),
							article.getJournalName() }, ++ctr);
		}

	}
}
