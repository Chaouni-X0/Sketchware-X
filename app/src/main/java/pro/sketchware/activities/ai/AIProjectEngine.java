package pro.sketchware.activities.ai;

import android.content.Context;
import com.besome.sketch.beans.BlockBean;
import com.besome.sketch.beans.ViewBean;
import com.besome.sketch.beans.ProjectLibraryBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import a.a.a.FileUtil;

public class AIProjectEngine {

    private Context context;
    private Gson gson;

    public AIProjectEngine(Context context) {
        this.context = context;
        this.gson = new Gson();
    }

    public void processAIResponse(String jsonResponse, String projectPath) {
        try {
            Map<String, Object> responseMap = gson.fromJson(jsonResponse, new TypeToken<Map<String, Object>>(){}.getType());
            
            if (responseMap.containsKey("views")) {
                createViews((List<Map<String, Object>>) responseMap.get("views"), projectPath);
            }
            
            if (responseMap.containsKey("logic")) {
                createLogic((List<Map<String, Object>>) responseMap.get("logic"), projectPath);
            }

            if (responseMap.containsKey("libraries")) {
                updateLibraries((List<Map<String, Object>>) responseMap.get("libraries"), projectPath);
            }

            if (responseMap.containsKey("permissions")) {
                updatePermissions((List<String>) responseMap.get("permissions"), projectPath);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createViews(List<Map<String, Object>> viewsList, String projectPath) {
        ArrayList<ViewBean> viewBeans = new ArrayList<>();
        for (Map<String, Object> viewData : viewsList) {
            String id = (String) viewData.get("id");
            String typeStr = (String) viewData.get("type");
            int type = ViewBean.getViewTypeByTypeName(typeStr);
            ViewBean bean = new ViewBean(id, type);
            if (viewData.containsKey("text")) bean.text.text = (String) viewData.get("text");
            viewBeans.add(bean);
        }
        FileUtil.writeFile(projectPath + "/main.view", gson.toJson(viewBeans));
    }

    private void createLogic(List<Map<String, Object>> logicList, String projectPath) {
        ArrayList<BlockBean> blockBeans = new ArrayList<>();
        for (Map<String, Object> logicData : logicList) {
            List<Map<String, Object>> blocks = (List<Map<String, Object>>) logicData.get("blocks");
            for (Map<String, Object> blockData : blocks) {
                BlockBean bean = new BlockBean("0", (String) blockData.get("spec"), (String) blockData.get("type"), "none");
                blockBeans.add(bean);
            }
        }
        FileUtil.writeFile(projectPath + "/main.logic", gson.toJson(blockBeans));
    }

    private void updateLibraries(List<Map<String, Object>> librariesList, String projectPath) {
        ArrayList<ProjectLibraryBean> libraryBeans = new ArrayList<>();
        for (Map<String, Object> libData : librariesList) {
            int type = ((Double) libData.get("type")).intValue();
            ProjectLibraryBean bean = new ProjectLibraryBean(type);
            bean.useYn = ProjectLibraryBean.LIB_USE_Y;
            libraryBeans.add(bean);
        }
        FileUtil.writeFile(projectPath + "/project.lib", gson.toJson(libraryBeans));
    }

    private void updatePermissions(List<String> permissions, String projectPath) {
        FileUtil.writeFile(projectPath + "/project.permissions", gson.toJson(permissions));
    }
}
