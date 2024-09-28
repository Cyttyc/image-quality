package pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class UserSaveSelectionDTO implements Serializable {
    private String username;
    private List<SelectionItem> selectedHistory;

    public static class SelectionItem {
        private List<String> folders;
        private String result;

        // Getter 和 Setter
        public List<String> getFolders() {
            return folders;
        }

        public void setFolders(List<String> folders) {
            this.folders = folders;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }
}
