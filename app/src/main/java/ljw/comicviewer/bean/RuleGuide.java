package ljw.comicviewer.bean;

import java.util.List;

public class RuleGuide {
    private String baseUrl;
    private List<RuleBlock> ruleList;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<RuleBlock> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<RuleBlock> ruleList) {
        this.ruleList = ruleList;
    }

    public class RuleBlock{
        private Integer id;
        private String name;
        private String description;
        private String filename;
        private String version;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
