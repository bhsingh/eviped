package org.biosemantics.eviped.tools.utility;

import gov.nih.nlm.ncbi.eutils.PubmedRestClient;
import gov.nih.nlm.ncbi.eutils.generated.efetch.MeshHeading;
import gov.nih.nlm.ncbi.eutils.generated.efetch.MeshHeadingList;
import gov.nih.nlm.ncbi.eutils.generated.efetch.PubmedArticle;
import gov.nih.nlm.ncbi.eutils.generated.efetch.PubmedArticleSet;
import gov.nih.nlm.ncbi.eutils.generated.efetch.QualifierName;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Joiner;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class MeshHeadingUtility {

	private static final String INPUT_FILE = "/Users/bhsingh/Desktop/1.txt";
	private static final String OUT_FILE = "/Users/bhsingh/Desktop/eviped/all-mesh-frequency-only-qualifiers.txt";
	private static final String IN_FOLDER = "/Users/bhsingh/Desktop/eviped/final";
	private static final Joiner joiner = Joiner.on(",").skipNulls();

	public static void main(String[] args) throws JAXBException, IOException {
		// readSet();
		readAll();
	}

	private static void readAll() throws JAXBException, IOException {
		PubmedRestClient pubmedRestClient = new PubmedRestClient();
		pubmedRestClient.setBaseUrl("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/");
		File[] files = new File(IN_FOLDER).listFiles();
		Set<String> pmids = new HashSet<String>();
		for (File file : files) {
			CSVReader reader = new CSVReader(new FileReader(file));
			List<String[]> lines = reader.readAll();
			for (String[] columns : lines) {
				pmids.add(columns[0]);
			}
		}
		List<String> fetchPmids = new ArrayList<String>();
		int ctr = 0;
		int size = pmids.size();
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		Map<String, Integer> frequencyMap = new HashMap<String, Integer>();
		for (String pmid : pmids) {
			ctr++;
			fetchPmids.add(pmid);
			if (ctr % 1000 == 0 || ctr >= size) {
				params.clear();
				params.add("db", "pubmed");
				params.add("retmode", "xml");
				params.add("id", joiner.join(fetchPmids));
				try {
					PubmedArticleSet pubmedArticleSet = pubmedRestClient.fetch(params);
					for (PubmedArticle pubmedArticle : pubmedArticleSet.getPubmedArticle()) {
						try {
							List<MeshHeading> meshHeadings = pubmedArticle.getMedlineCitation().getMeshHeadingList()
									.getMeshHeading();
							for (MeshHeading meshHeading : meshHeadings) {
//								String desString = meshHeading.getDescriptorName().getContent();
//								updateMap(frequencyMap, desString);
								List<QualifierName> qualifierNames = meshHeading.getQualifierName();
								for (QualifierName qualifierName : qualifierNames) {
									updateMap(frequencyMap, qualifierName.getContent());
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				fetchPmids.clear();
			}
		}
		CSVWriter csvWriter = new CSVWriter(new FileWriter(OUT_FILE));
		for (Entry<String, Integer> entry : frequencyMap.entrySet()) {
			csvWriter.writeNext(new String[] { entry.getKey(), "" + entry.getValue() });
		}
		csvWriter.flush();
		csvWriter.close();
	}

	private static void readSet() throws JAXBException, IOException {
		PubmedRestClient pubmedRestClient = new PubmedRestClient();
		pubmedRestClient.setBaseUrl("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/");
		List<String> lines = FileUtils.readLines(new File(INPUT_FILE));
		Map<String, Integer> frequencyMap = new HashMap<String, Integer>();
		for (String pmid : lines) {
			try {
				MeshHeadingList meshHeadingList = pubmedRestClient.getMeshHeadingList(pmid);
				List<MeshHeading> meshHeadings = meshHeadingList.getMeshHeading();
				for (MeshHeading meshHeading : meshHeadings) {
					String desString = meshHeading.getDescriptorName().getContent();
					updateMap(frequencyMap, desString);
					List<QualifierName> qualifierNames = meshHeading.getQualifierName();
					for (QualifierName qualifierName : qualifierNames) {
						updateMap(frequencyMap, qualifierName.getContent());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		CSVWriter csvWriter = new CSVWriter(new FileWriter(OUT_FILE));
		for (Entry<String, Integer> entry : frequencyMap.entrySet()) {
			csvWriter.writeNext(new String[] { entry.getKey(), "" + entry.getValue() });
		}
		csvWriter.flush();
		csvWriter.close();
	}

	private static void updateMap(Map<String, Integer> frequencyMap, String text) {
		int frequency = 0;
		if (frequencyMap.containsKey(text)) {
			frequency = frequencyMap.get(text);
		}
		frequencyMap.put(text, (++frequency));

	}
}
