/*
 * Copyright 2011 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package luceneexamples;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class JapaneseSearch {
    @Test
    public void index() throws Exception {
        Directory directory = new RAMDirectory();
//        Directory directory = FSDirectory.open(new File("cjkindex"));
        Analyzer analyzer = new CJKAnalyzer(Version.LUCENE_31);

        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_31, analyzer);
        IndexWriter writer = new IndexWriter(directory, iwc);

        Document doc = new Document();
        doc.add(new Field("str_field", "quick brown fox jumped over the lazy dog.",
                Field.Store.YES, Field.Index.ANALYZED));
        writer.addDocument(doc);
        Document doc2 = new Document();
        doc2.add(new Field("str_field", "貴社の記者が汽車で帰社した",
                Field.Store.YES, Field.Index.ANALYZED));
        writer.addDocument(doc2);
        writer.close();
        IndexSearcher searcher = new IndexSearcher(directory, true);
        QueryParser parser = new QueryParser(Version.LUCENE_31, "str_field", analyzer);
        TopDocs td = searcher.search(parser.parse("記者"), 1000);
        assertThat(td.totalHits, is(1));
        searcher.close();
        directory.close();
    }
}
