/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene.analysis.snowball;


import java.io.IOException;
import java.util.Arrays;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tr.TurkishLowerCaseFilter; // javadoc @link
import org.tartarus.snowball.SnowballProgram;

/**
 * A filter that stems words using a Snowball-generated stemmer.
 *
 * Available stemmers are listed in {@link org.tartarus.snowball.ext}.
 * <p><b>NOTE</b>: SnowballFilter expects lowercased text.
 * <ul>
 *  <li>For the Turkish language, see {@link TurkishLowerCaseFilter}.
 *  <li>For other languages, see {@link LowerCaseFilter}.
 * </ul>
 *
 * <p>
 * Note: This filter is aware of the {@link KeywordAttribute}. To prevent
 * certain terms from being passed to the stemmer
 * {@link KeywordAttribute#isKeyword()} should be set to <code>true</code>
 * in a previous {@link TokenStream}.
 *
 * Note: For including the original term as well as the stemmed version, see
 * {@link org.apache.lucene.analysis.miscellaneous.KeywordRepeatFilterFactory}
 * </p>
 *
 *
 */
public final class SnowballFilter extends TokenFilter {

  private final SnowballProgram stemmer;

  private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
  private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);

  public SnowballFilter(TokenStream input, SnowballProgram stemmer) {
    super(input);
    this.stemmer = stemmer;
  }

  /**
   * Construct the named stemming filter.
   *
   * Available stemmers are listed in {@link org.tartarus.snowball.ext}.
   * The name of a stemmer is the part of the class name before "Stemmer",
   * e.g., the stemmer in {@link org.tartarus.snowball.ext.EnglishStemmer} is named "English".
   *
   * @param in the input tokens to stem
   * @param name the name of a stemmer
   */
  public SnowballFilter(TokenStream in, String name) {
    super(in);
    //Class.forName is frowned upon in place of the ResourceLoader but in this case,
    // the factory will use the other constructor so that the program is already loaded.
    try {
      Class<? extends SnowballProgram> stemClass =
        Class.forName("org.tartarus.snowball.ext." + name + "Stemmer").asSubclass(SnowballProgram.class);
      stemmer = stemClass.getConstructor().newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid stemmer class specified: " + name, e);
    }
  }

  /** Returns the next input Token, after being stemmed */
  @Override
  public final boolean incrementToken() throws IOException {
    if (input.incrementToken()) {
      if (!keywordAttr.isKeyword()) {
        final String term = new String(Arrays.copyOfRange(termAtt.buffer(), 0, termAtt.length())).trim();
        stemmer.setCurrent(term);
        stemmer.stem();
        final String stem = stemmer.getCurrent();
        if (!stem.equals(term)){
          termAtt.copyBuffer(stem.toCharArray(), 0, stem.length());
        }
        else{
          termAtt.setLength(stem.length());
        }
      }
      return true;
    } else {
      return false;
    }
  }
}
