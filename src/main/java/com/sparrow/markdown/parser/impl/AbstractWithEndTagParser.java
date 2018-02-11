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
package com.sparrow.markdown.parser.impl;

import com.sparrow.markdown.mark.MarkContext;
import com.sparrow.markdown.parser.MarkParser;

/**
 * @author by harry
 */
public abstract class AbstractWithEndTagParser implements MarkParser {

    @Override public int validate(MarkContext mark) {
        int startIndex = mark.getCurrentPointer() + this.mark().getStart().length();
        int endMarkIndex = mark.getContent().indexOf(this.mark().getEnd(), startIndex);
        if (endMarkIndex > startIndex) {
            return endMarkIndex;
        }
        return -1;
    }

    @Override public void parse(MarkContext markContext) {
        String content = markContext.getContent().substring(markContext.getCurrentPointer()
            + this.mark().getStart().length(), markContext.getEndPointer() - this.mark().getEnd().length());
        //如果包含复杂结构，至少需要两个字符
        if (content.length() <= 2 || MarkContext.CHILD_MARK_PARSER.get(this.mark()) == null) {
            markContext.append(String.format(this.mark().getFormat(), content));
            markContext.setPointer(markContext.getEndPointer());
            markContext.setEndPointer(-1);
            return;
        }
        MarkContext innerContext = new MarkContext(content);
        innerContext.setParentMark(this.mark());
        MarkdownParserComposite.getInstance().parse(innerContext);
        markContext.append(String.format(this.mark().getFormat(), innerContext.getHtml()));
        markContext.setPointer(markContext.getEndPointer());
        markContext.setEndPointer(-1);
    }
}
