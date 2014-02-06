/*
 * $Id: AbstractClientDataModel.java,v 1.1 2013/01/11 15:46:58 jbmeslin Exp $
 */
package org.rcfaces.core.model;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:46:58 $
 */
public abstract class AbstractClientDataModel implements IClientDataModel {

    private String contentName;

    private String contentKey;

    private int contentRowCount = -1;

    private IContentIndex[] searchStrategies;

    public String getContentName() {
        return contentName;
    }

    protected void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public String getContentKey() {
        return contentKey;
    }

    protected void setContentKey(String contentKey) {
        this.contentKey = contentKey;
    }

    public int getContentRowCount() {
        return contentRowCount;
    }

    protected void setContentRowCount(int contentRowCount) {
        this.contentRowCount = contentRowCount;
    }

    public IContentIndex[] getSearchStrategies() {
        return searchStrategies;
    }

    protected void setSearchStrategies(IContentIndex[] searchStrategies) {
        this.searchStrategies = searchStrategies;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.1 $ $Date: 2013/01/11 15:46:58 $
     */
    public static class BasicContentIndex implements IContentIndex {

        private final String columnName;

        private final IndexStrategy searchStrategy;

        private final boolean ignoreCase;

        private final boolean ignoreAccent;

        private final boolean eachWord;

        public BasicContentIndex(String columnName, IndexStrategy searchStrategy) {
            this(columnName, searchStrategy, false, false, false);
        }

        public BasicContentIndex(String columnName,
                IndexStrategy searchStrategy, boolean ignoreCase,
                boolean ignoreAccent, boolean eachWord) {
            this.columnName = columnName;
            this.searchStrategy = searchStrategy;
            this.ignoreCase = ignoreCase;
            this.ignoreAccent = ignoreAccent;
            this.eachWord = eachWord;
        }

        public String getFieldName() {
            return columnName;
        }

        public IndexStrategy getStrategy() {
            return searchStrategy;
        }

        public boolean isIgnoreCase() {
            return ignoreCase;
        }

        public boolean isIgnoreAccent() {
            return ignoreAccent;
        }

        public boolean isEachWord() {
            return eachWord;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[SearchStrategy columnName='").append(columnName)
                    .append("' searchStrategy='").append(searchStrategy)
                    .append("' ignoreCase='").append(ignoreCase)
                    .append("' ignoreAccent='").append(ignoreAccent)
                    .append("' eachWord='").append(eachWord).append("']");
            return builder.toString();
        }

    }
}
